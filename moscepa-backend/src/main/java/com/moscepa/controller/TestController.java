// Fichier : src/main/java/com/moscepa/controller/TestController.java (Version Intégrale et Finale)

package com.moscepa.controller;
import com.moscepa.service.QuestionnaireService;

import com.moscepa.dto.QuestionnaireDetailDto; 
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tests" )
// @CrossOrigin(origins = "http://localhost:4200" ) // Optionnel : peut être géré globalement dans SecurityConfig
public class TestController {

    private final TestService testService;
    private final QuestionnaireService questionnaireService;

    public TestController(TestService testService, QuestionnaireService questionnaireService) {
        this.testService = testService;
        this.questionnaireService = questionnaireService;
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
@PreAuthorize("hasRole('ETUDIANT')")
public ResponseEntity<ResultatTestDto> soumettreTest(
        @PathVariable Long chapitreId,
        @RequestBody Map<String, Object> reponses,
        @AuthenticationPrincipal UserPrincipal userPrincipal) { // Injection directe

    ResultatTestDto resultat = testService.calculerEtSauvegarderResultat(
            chapitreId, 
            userPrincipal.getId(), 
            reponses
    );
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
    // 1. Pour lister les questionnaires disponibles pour un chapitre
@GetMapping("/chapitre/{chapitreId}/choix-questionnaires")
@PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
public ResponseEntity<List<QuestionnaireDetailDto>> getChoixQuestionnaires(@PathVariable Long chapitreId) {
    List<QuestionnaireDetailDto> choix = questionnaireService.getQuestionnairesParChapitre(chapitreId);
    return ResponseEntity.ok(choix);
}

// 2. Pour assigner le questionnaire choisi au test du chapitre
@PostMapping("/chapitre/{chapitreId}/assigner/{questionnaireId}")
@PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
public ResponseEntity<Test> assignerTest(@PathVariable Long chapitreId, @PathVariable Long questionnaireId) {
    Test test = testService.assignerQuestionnaireAuTest(chapitreId, questionnaireId);
    return ResponseEntity.ok(test);
}
@PostMapping("/questionnaire/{questionnaireId}/entrainement")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ResultatTestDto> verifierEntrainement(
        @PathVariable Long questionnaireId,
        @RequestBody Map<String, Object> reponses) {
    return ResponseEntity.ok(testService.calculerResultatEntrainement(questionnaireId, reponses));
}


}
