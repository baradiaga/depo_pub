// Fichier : src/main/java/com/moscepa/controller/QuestionnaireController.java

package com.moscepa.controller;

import com.moscepa.dto.GenerateurPayload;
import com.moscepa.dto.QuestionnaireDetailDto;
import com.moscepa.dto.QuestionnairePayload;
import com.moscepa.service.QuestionnaireService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questionnaires")
@CrossOrigin(origins = "*")
public class QuestionnaireController {

    private static final Logger log = LoggerFactory.getLogger(QuestionnaireController.class);
    private final QuestionnaireService questionnaireService;

    public QuestionnaireController(QuestionnaireService questionnaireService) {
        this.questionnaireService = questionnaireService;
    }

    // ====================================================================
    // === CRÉATION DE QUESTIONNAIRE                                     ===
    // ====================================================================
    @PostMapping
    public ResponseEntity<Void> createQuestionnaire(@RequestBody QuestionnairePayload payload) {
        try {
            log.info("Requête reçue pour CRÉER un questionnaire : {}", payload.getTitre());
            questionnaireService.sauvegarderQuestionnaire(payload);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Erreur lors de la création du questionnaire", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ====================================================================
    // === LISTE DE TOUS LES QUESTIONNAIRES                              ===
    // ====================================================================
    @GetMapping
    public ResponseEntity<List<QuestionnaireDetailDto>> getAllQuestionnaires() {
        try {
            log.info("Requête reçue pour LISTER tous les questionnaires.");
            List<QuestionnaireDetailDto> questionnaires = questionnaireService.findAllQuestionnaires();
            return ResponseEntity.ok(questionnaires);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des questionnaires", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ====================================================================
    // === GÉNÉRATION AUTOMATIQUE DE QUESTIONNAIRE                       ===
    // ====================================================================
    @PostMapping("/generer-depuis-banque")
    public ResponseEntity<QuestionnaireDetailDto> genererQuestionnaire(@RequestBody GenerateurPayload params) {
        try {
            log.info("Requête reçue pour GÉNÉRER un questionnaire : {}", params.getTitre());
            QuestionnaireDetailDto questionnaireGenere = questionnaireService.genererQuestionnaireDepuisBanque(params);
            return ResponseEntity.ok(questionnaireGenere);
        } catch (Exception e) {
            log.error("Erreur lors de la génération automatique", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ====================================================================
    // === SUPPRESSION DE QUESTIONNAIRE                                  ===
    // ====================================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestionnaire(@PathVariable Long id) {
        try {
            log.info("Requête reçue pour SUPPRIMER le questionnaire ID: {}", id);
            questionnaireService.deleteQuestionnaireById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du questionnaire ID: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // ====================================================================
// === TESTS ASSOCIÉS AU QUESTIONNAIRE                               ===
// ====================================================================

/**
 * Récupérer tous les tests liés à un questionnaire.
 */
@GetMapping("/{id}/tests")
public ResponseEntity<?> getTestsByQuestionnaire(@PathVariable Long id) {
    try {
        log.info("Requête reçue pour LISTER les tests du questionnaire ID: {}", id);
        return ResponseEntity.ok(questionnaireService.getTestsByQuestionnaire(id));
    } catch (Exception e) {
        log.error("Erreur lors de la récupération des tests du questionnaire ID: {}", id, e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

/**
 * Créer un nouveau test lié à un questionnaire.
 */
@PostMapping("/{id}/tests")
public ResponseEntity<?> createTest(@PathVariable Long id, @RequestBody com.moscepa.entity.Test test) {
    try {
        log.info("Requête reçue pour CRÉER un test pour le questionnaire ID: {}", id);
        return new ResponseEntity<>(questionnaireService.createTest(id, test), HttpStatus.CREATED);
    } catch (Exception e) {
        log.error("Erreur lors de la création d'un test pour le questionnaire ID: {}", id, e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

/**
 * Mettre à jour un test existant.
 */
@PutMapping("/tests/{testId}")
public ResponseEntity<?> updateTest(@PathVariable Long testId, @RequestBody com.moscepa.entity.Test test) {
    try {
        log.info("Requête reçue pour MODIFIER le test ID: {}", testId);
        return ResponseEntity.ok(questionnaireService.updateTest(testId, test));
    } catch (Exception e) {
        log.error("Erreur lors de la mise à jour du test ID: {}", testId, e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

/**
 * Supprimer un test.
 */
@DeleteMapping("/tests/{testId}")
public ResponseEntity<Void> deleteTest(@PathVariable Long testId) {
    try {
        log.info("Requête reçue pour SUPPRIMER le test ID: {}", testId);
        questionnaireService.deleteTest(testId);
        return ResponseEntity.noContent().build();
    } catch (Exception e) {
        log.error("Erreur lors de la suppression du test ID: {}", testId, e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
// ====================================================================
// === DÉTAIL D’UN QUESTIONNAIRE                                     ===
// ====================================================================
@GetMapping("/{id}")
public ResponseEntity<QuestionnaireDetailDto> getQuestionnaireById(@PathVariable Long id) {
    try {
        log.info("Requête reçue pour DÉTAIL du questionnaire ID: {}", id);
        return ResponseEntity.ok(questionnaireService.findDetailById(id));
    } catch (Exception e) {
        log.error("Erreur lors de la récupération du questionnaire ID: {}", id, e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

}
