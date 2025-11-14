// Fichier : src/main/java/com/moscepa/controller/TestDiagnosticController.java (Version Finale)

package com.moscepa.controller;

import com.moscepa.dto.QuestionDiagnosticDto;
import com.moscepa.dto.ResultatDiagnosticDto; // Assurez-vous que cet import est là
import com.moscepa.dto.SoumissionTestDto;     // Assurez-vous que cet import est là
import com.moscepa.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnostic" )
public class TestDiagnosticController {

    private static final Logger log = LoggerFactory.getLogger(TestDiagnosticController.class);

    @Autowired
    private TestService testService;

    @GetMapping("/generer-test/{matiereId}")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<List<QuestionDiagnosticDto>> genererTest(@PathVariable Long matiereId) {
        log.info("Requête pour générer un test de diagnostic pour la matière ID: {}", matiereId);
        List<QuestionDiagnosticDto> questions = testService.genererTestDiagnosticPourMatiere(matiereId);
        log.info("Envoi de {} questions pour le test de diagnostic.", questions.size());
        return ResponseEntity.ok(questions);
    }

    // ====================================================================
    // === VERSION FINALE ET FONCTIONNELLE DE LA CORRECTION             ===
    // ====================================================================
    @PostMapping("/corriger-test")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<ResultatDiagnosticDto> corrigerTest(@RequestBody SoumissionTestDto soumission) {
        log.info("Requête de correction reçue avec {} réponses.", soumission.getReponses().size());
        
        // On appelle la vraie méthode de service
        ResultatDiagnosticDto resultat = testService.corrigerTestDiagnostic(soumission);
        
        return ResponseEntity.ok(resultat);
    }
}
