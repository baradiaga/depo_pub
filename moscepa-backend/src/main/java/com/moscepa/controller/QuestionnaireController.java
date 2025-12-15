package com.moscepa.controller;

import com.moscepa.dto.QuestionnaireDetailDto;
import com.moscepa.service.QuestionnaireService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questionnaires")
@CrossOrigin("*")
public class QuestionnaireController {

    private static final Logger logger = LoggerFactory.getLogger(QuestionnaireController.class);
    
    @Autowired
    private QuestionnaireService questionnaireService;

    // --- Récupérer tous les questionnaires avec leurs questions ---
    @GetMapping
    public ResponseEntity<List<QuestionnaireDetailDto>> getAllQuestionnaires() {
        logger.info("📥 GET /api/questionnaires");
        List<QuestionnaireDetailDto> questionnaires = questionnaireService.getAllQuestionnairesDetail();
        logger.info("📤 Retourne {} questionnaires", questionnaires.size());
        return ResponseEntity.ok(questionnaires);
    }

    // --- Récupérer un questionnaire par son ID avec ses questions ---
    @GetMapping("/{id}")
    public ResponseEntity<QuestionnaireDetailDto> getQuestionnaireById(@PathVariable Long id) {
        logger.info("📥 GET /api/questionnaires/{}", id);
        QuestionnaireDetailDto questionnaire = questionnaireService.getQuestionnaireDetailById(id);
        return ResponseEntity.ok(questionnaire);
    }

    // --- Créer un nouveau questionnaire ---
    @PostMapping
    public ResponseEntity<?> createQuestionnaire(@RequestBody QuestionnaireDetailDto dto) {
        logger.info("📥 POST /api/questionnaires");
        logger.info("Données reçues: titre={}, chapitreId={}", dto.getTitre(), dto.getChapitreId());
        
        try {
            QuestionnaireDetailDto created = questionnaireService.createQuestionnaire(dto);
            logger.info("✅ Questionnaire créé avec ID: {}", created.getId());
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            logger.error("❌ Erreur validation: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        } catch (Exception e) {
            logger.error("❌ Erreur création questionnaire: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Erreur serveur: " + e.getMessage());
        }
    }

    // --- Mettre à jour un questionnaire existant ---
    @PutMapping("/{id}")
    public ResponseEntity<QuestionnaireDetailDto> updateQuestionnaire(@PathVariable Long id,
                                                                       @RequestBody QuestionnaireDetailDto dto) {
        logger.info("📥 PUT /api/questionnaires/{}", id);
        QuestionnaireDetailDto updated = questionnaireService.updateQuestionnaire(id, dto);
        return ResponseEntity.ok(updated);
    }

    // --- Supprimer un questionnaire ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestionnaire(@PathVariable Long id) {
        logger.info("📥 DELETE /api/questionnaires/{}", id);
        boolean deleted = questionnaireService.deleteQuestionnaire(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}