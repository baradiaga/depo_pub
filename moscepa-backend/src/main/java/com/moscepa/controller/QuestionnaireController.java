package com.moscepa.controller;

import com.moscepa.dto.GenerateurPayloadDTO;
import com.moscepa.dto.QuestionnaireDetailDto;
import com.moscepa.dto.QuestionnairePayloadDTO;
import com.moscepa.service.QuestionnaireService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questionnaires" )
@CrossOrigin(origins = "*")
public class QuestionnaireController {

    private static final Logger log = LoggerFactory.getLogger(QuestionnaireController.class);
    private final QuestionnaireService questionnaireService;

    public QuestionnaireController(QuestionnaireService questionnaireService) {
        this.questionnaireService = questionnaireService;
    }

    @PostMapping
    public ResponseEntity<Void> createQuestionnaire(@RequestBody QuestionnairePayloadDTO payload) {
        try {
            log.info("Requête reçue pour CRÉER un questionnaire : {}", payload.getTitre());
            questionnaireService.sauvegarderQuestionnaire(payload); // Appelle la bonne méthode
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Erreur lors de la création du questionnaire", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<QuestionnaireDetailDto>> getAllQuestionnaires() {
        try {
            log.info("Requête reçue pour LISTER tous les questionnaires.");
            List<QuestionnaireDetailDto> questionnaires = questionnaireService.findAllQuestionnaires(); // Appelle la nouvelle méthode
            return ResponseEntity.ok(questionnaires);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des questionnaires", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/generer-depuis-banque")
    public ResponseEntity<QuestionnaireDetailDto> genererQuestionnaire(@RequestBody GenerateurPayloadDTO params) {
        try {
            log.info("Requête reçue pour GÉNÉRER un questionnaire : {}", params.getTitre());
            // CORRECTION : Appelle la méthode avec le bon nom
            QuestionnaireDetailDto questionnaireGenere = questionnaireService.genererQuestionnaireDepuisBanque(params);
            return ResponseEntity.ok(questionnaireGenere);
        } catch (Exception e) {
            log.error("Erreur lors de la génération automatique", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestionnaire(@PathVariable Long id) {
        try {
            log.info("Requête reçue pour SUPPRIMER le questionnaire ID: {}", id);
            questionnaireService.deleteQuestionnaireById(id); // Appelle la nouvelle méthode
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du questionnaire ID: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
