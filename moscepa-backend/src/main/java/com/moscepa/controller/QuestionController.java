package com.moscepa.controller;

import com.moscepa.dto.QuestionDto;
import com.moscepa.entity.Question;
import com.moscepa.service.QuestionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class QuestionController {

    @Autowired
    private QuestionService questionService;
    
    @PersistenceContext
    private EntityManager entityManager;

    // ====================================================================
    // === ENDPOINTS CRUD STANDARD                                       ===
    // ====================================================================

    @PostMapping
    public ResponseEntity<?> createQuestion(
            @RequestBody QuestionDto questionDto,
            @RequestParam(required = false) Long questionnaireId) {
        try {
            Question question;
            
            if (questionnaireId != null) {
                question = questionService.createQuestion(questionDto, questionnaireId);
            } else {
                Question newQuestion = new Question();
                newQuestion.setEnonce(questionDto.getEnonce());
                newQuestion.setPoints(questionDto.getPoints());
                newQuestion.setTypeQuestion(questionDto.getType());
                entityManager.persist(newQuestion);
                question = newQuestion;
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Question créée avec succès");
            response.put("questionId", question.getId());
            response.put("question", new QuestionDto(question));
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "success", false,
                        "message", "Erreur lors de la création: " + e.getMessage()
                    ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestion(@PathVariable Long id) {
        try {
            QuestionDto questionDto = questionService.getQuestionById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("question", questionDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllQuestions() {
        try {
            List<QuestionDto> questions = questionService.getAllQuestions();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("questions", questions);
            response.put("count", questions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuestion(
            @PathVariable Long id,
            @RequestBody QuestionDto questionDto) {
        try {
            Question updatedQuestion = questionService.updateQuestion(id, questionDto);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Question mise à jour avec succès");
            response.put("question", new QuestionDto(updatedQuestion));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ====================================================================
    // === ENDPOINTS DE SUPPRESSION (GARANTIS)                           ===
    // ====================================================================

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        return questionService.deleteQuestionFinal(id);
    }

    @DeleteMapping("/alternative/{id}")
    public ResponseEntity<?> deleteQuestionAlternative(@PathVariable Long id) {
        try {
            boolean deleted = questionService.deleteQuestionAlternative(id);
            
            Map<String, Object> response = new HashMap<>();
            if (deleted) {
                response.put("success", true);
                response.put("message", "Question supprimée avec succès");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Question non trouvée");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/direct/{id}")
    @Transactional
    public ResponseEntity<?> deleteQuestionDirect(@PathVariable Long id) {
        try {
            entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
            
            int testRelations = entityManager.createNativeQuery(
                "DELETE FROM moscepa_test_questions WHERE question_id = ?")
                .setParameter(1, id)
                .executeUpdate();
            
            int reponses = entityManager.createNativeQuery(
                "DELETE FROM moscepa_reponses WHERE question_id = ?")
                .setParameter(1, id)
                .executeUpdate();
            
            int question = entityManager.createNativeQuery(
                "DELETE FROM moscepa_questions WHERE id = ?")
                .setParameter(1, id)
                .executeUpdate();
            
            entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
            
            Map<String, Object> response = new HashMap<>();
            if (question > 0) {
                response.put("success", true);
                response.put("message", "Question supprimée");
                response.put("deletedRelations", testRelations);
                response.put("deletedReponses", reponses);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Question non trouvée");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            
        } catch (Exception e) {
            try {
                entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
            } catch (Exception ex) {
                // Ignorer
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ====================================================================
    // === ENDPOINTS DE RECHERCHE ET FILTRAGE                            ===
    // ====================================================================

    @GetMapping("/questionnaire/{questionnaireId}")
    public ResponseEntity<?> getQuestionsByQuestionnaire(@PathVariable Long questionnaireId) {
        try {
            List<QuestionDto> questions = questionService.getQuestionsByQuestionnaireId(questionnaireId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("questions", questions);
            response.put("count", questions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/chapitre/{chapitreId}")
    public ResponseEntity<?> getQuestionsByChapitre(@PathVariable Long chapitreId) {
        try {
            List<QuestionDto> questions = questionService.getQuestionsByChapitreId(chapitreId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("questions", questions);
            response.put("count", questions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/matiere/{matiereId}")
    public ResponseEntity<?> getQuestionsByMatiere(@PathVariable Long matiereId) {
        try {
            List<QuestionDto> questions = questionService.getQuestionsByMatiereId(matiereId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("questions", questions);
            response.put("count", questions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/banque/{chapitreId}")
    public ResponseEntity<?> getBanqueQuestions(@PathVariable Long chapitreId) {
        try {
            List<QuestionDto> questions = questionService.getBanqueQuestionsByChapitreId(chapitreId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("questions", questions);
            response.put("count", questions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ====================================================================
    // === ENDPOINTS UTILITAIRES                                         ===
    // ====================================================================

    @GetMapping("/debug/{id}")
    public ResponseEntity<?> debugQuestion(@PathVariable Long id) {
        try {
            Map<String, Object> stats = questionService.getQuestionStats(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("id", id);
            response.put("stats", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/duplicate/{id}")
    public ResponseEntity<?> duplicateQuestion(@PathVariable Long id) {
        try {
            Question duplicated = questionService.duplicateQuestion(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Question dupliquée avec succès");
            response.put("newQuestionId", duplicated.getId());
            response.put("question", new QuestionDto(duplicated));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/change-questionnaire")
    public ResponseEntity<?> changeQuestionnaire(
            @PathVariable Long id,
            @RequestParam Long newQuestionnaireId) {
        try {
            Question updated = questionService.changeQuestionnaire(id, newQuestionnaireId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Question déplacée avec succès");
            response.put("question", new QuestionDto(updated));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            long totalQuestions = questionService.countAllQuestions();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalQuestions", totalQuestions);
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchQuestions(@RequestParam String q) {
        try {
            List<QuestionDto> results = questionService.searchQuestions(q);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("results", results);
            response.put("count", results.size());
            response.put("searchTerm", q);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getQuestionsByType(@PathVariable String type) {
        try {
            List<QuestionDto> questions = questionService.getQuestionsByType(type);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("questions", questions);
            response.put("count", questions.size());
            response.put("type", type);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<?> questionExists(@PathVariable Long id) {
        try {
            boolean exists = questionService.questionExists(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("exists", exists);
            response.put("id", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/can-delete/{id}")
    public ResponseEntity<?> canDeleteQuestion(@PathVariable Long id) {
        try {
            boolean canDelete = questionService.canDeleteQuestion(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("canDelete", canDelete);
            response.put("id", id);
            response.put("message", canDelete ? 
                "La question peut être supprimée" : 
                "La question est utilisée dans des tests");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/questionnaire/{questionnaireId}/count")
    public ResponseEntity<?> countQuestionsInQuestionnaire(@PathVariable Long questionnaireId) {
        try {
            long count = questionService.countQuestionsInQuestionnaire(questionnaireId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);
            response.put("questionnaireId", questionnaireId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/chapitre/{chapitreId}/count")
    public ResponseEntity<?> countQuestionsInChapitre(@PathVariable Long chapitreId) {
        try {
            long count = questionService.countQuestionsInChapitre(chapitreId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);
            response.put("chapitreId", chapitreId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/min-points/{minPoints}")
    public ResponseEntity<?> getQuestionsWithMinPoints(@PathVariable double minPoints) {
        try {
            List<QuestionDto> questions = questionService.getQuestionsWithMinPoints(minPoints);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("questions", questions);
            response.put("count", questions.size());
            response.put("minPoints", minPoints);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "Question Service");
        response.put("timestamp", System.currentTimeMillis());
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }
}