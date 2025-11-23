// Fichier : src/main/java/com/moscepa/controller/TestController.java (Version Intégrale et Finale)

package com.moscepa.controller;

import com.moscepa.dto.HistoriqueResultatDto;
import com.moscepa.dto.QuestionDto;
import com.moscepa.dto.ResultatTestDto;
import com.moscepa.entity.Test; // Import de l'entité Test
import com.moscepa.security.UserPrincipal;
import com.moscepa.service.TestService;
import org.springframework.http.HttpStatus; // Import de HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tests" )
// @CrossOrigin(origins = "http://localhost:4200" ) // Optionnel : peut être géré globalement dans SecurityConfig
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    // ====================================================================
    // === VOS ENDPOINTS EXISTANTS (PRÉSERVÉS ET INTACTS)               ===
    // ====================================================================

    @GetMapping("/chapitre/{chapitreId}/questions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<QuestionDto>> getQuestions(@PathVariable Long chapitreId) {
        List<QuestionDto> questions = testService.getQuestionsPourChapitre(chapitreId);
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/chapitre/{chapitreId}/soumettre")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResultatTestDto> soumettreTest(
            @PathVariable Long chapitreId,
            @RequestBody Map<String, Object> reponses,
            Authentication authentication) {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long utilisateurId = userPrincipal.getId();
        ResultatTestDto resultat = testService.calculerEtSauvegarderResultat(chapitreId, utilisateurId, reponses);
        return ResponseEntity.ok(resultat);
    }

    @GetMapping("/mon-historique")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<HistoriqueResultatDto>> getMonHistorique(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long utilisateurId = userPrincipal.getId();
        List<HistoriqueResultatDto> historique = testService.getHistoriquePourEtudiant(utilisateurId);
        return ResponseEntity.ok(historique);
    }

    // ====================================================================
    // === NOUVEL ENDPOINT POUR LA CRÉATION DE TEST (FONCTIONNALITÉ MANQUANTE) ===
    // ====================================================================

    /**
     * Classe interne (DTO) pour recevoir les données de la requête de création de test.
     */
    public static class CreateTestRequest {
        public String titre;
        public Long chapitreId;
        public List<Long> questionIds;
    }

    /**
     * Crée un nouveau test et l'associe à des questions.
     * L'accès est restreint aux ENSEIGNANTS et aux ADMINS.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<Test> createTest(@RequestBody CreateTestRequest request) {
        Test nouveauTest = testService.creerTestAvecQuestions(request.chapitreId, request.titre, request.questionIds);
        return new ResponseEntity<>(nouveauTest, HttpStatus.CREATED);
    }
}
