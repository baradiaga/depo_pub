package com.moscepa.controller;

import com.moscepa.dto.QuestionDto;
import com.moscepa.dto.ResultatTestDto;
import com.moscepa.security.UserPrincipal; // Assure-toi que cet import est correct
import com.moscepa.service.TestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tests" )
@CrossOrigin(origins = "http://localhost:4200" )
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/chapitre/{chapitreId}/questions")
    public ResponseEntity<List<QuestionDto>> getQuestionsByChapitre(@PathVariable Long chapitreId) {
        List<QuestionDto> questions = testService.getQuestionsByChapitre(chapitreId);
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/{chapitreId}/submit")
    public ResponseEntity<ResultatTestDto> submitTest(
            @PathVariable Long chapitreId,
            @RequestBody Map<String, Object> reponses,
            Authentication authentication) {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long utilisateurId = userPrincipal.getId();

        // ====================================================================
        // ===> POINT DE CONTRÔLE N°1 : VÉRIFIER L'ID EXTRAIT DU TOKEN <===
        // ====================================================================
        System.out.println("\n\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("!!! [TestController] ID utilisateur extrait du TOKEN: " + utilisateurId + " !!!");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n");

        ResultatTestDto resultat = testService.calculerEtSauvegarderResultat(chapitreId, utilisateurId, reponses);
        
        return ResponseEntity.ok(resultat);
    }
}
