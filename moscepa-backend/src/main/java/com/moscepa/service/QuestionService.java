package com.moscepa.service;

import com.moscepa.dto.QuestionDto;
import com.moscepa.dto.ReponsePourQuestionDto;
import com.moscepa.entity.Question;
import com.moscepa.entity.Reponse;
import com.moscepa.entity.Questionnaire;
import com.moscepa.entity.Test;
import com.moscepa.repository.QuestionRepository;
import com.moscepa.repository.QuestionnaireRepository;
import com.moscepa.repository.TestRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionnaireRepository questionnaireRepository;
    private final TestRepository testRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    public QuestionService(QuestionRepository questionRepository, 
                          QuestionnaireRepository questionnaireRepository,
                          TestRepository testRepository) {
        this.questionRepository = questionRepository;
        this.questionnaireRepository = questionnaireRepository;
        this.testRepository = testRepository;
    }

    // ====================================================================
    // === MÉTHODES CRUD STANDARD                                        ===
    // ====================================================================

    @Transactional
    public Question createQuestion(QuestionDto questionDto, Long questionnaireId) {
        Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new EntityNotFoundException("Questionnaire non trouvé avec l'ID : " + questionnaireId));

        Question question = new Question();
        question.setEnonce(questionDto.getEnonce());
        question.setPoints(questionDto.getPoints());
        question.setTypeQuestion(questionDto.getType());
        question.setQuestionnaire(questionnaire);

        if (questionDto.getReponses() != null && !questionDto.getReponses().isEmpty()) {
            for (ReponsePourQuestionDto reponseDto : questionDto.getReponses()) {
                Reponse nouvelleReponse = new Reponse();
                nouvelleReponse.setTexte(reponseDto.getTexte());
                nouvelleReponse.setCorrecte(reponseDto.isCorrecte());
                question.addReponse(nouvelleReponse);
            }
        }
        
        return questionRepository.save(question);
    }

    public QuestionDto getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question non trouvée avec l'ID : " + id));
        return new QuestionDto(question);
    }

    public List<QuestionDto> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(QuestionDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Question updateQuestion(Long id, QuestionDto questionDto) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question non trouvée avec l'ID : " + id));

        question.setEnonce(questionDto.getEnonce());
        question.setPoints(questionDto.getPoints());
        question.setTypeQuestion(questionDto.getType());

        if (questionDto.getReponses() != null) {
            question.getReponses().clear();
            
            for (ReponsePourQuestionDto reponseDto : questionDto.getReponses()) {
                Reponse reponse = new Reponse();
                reponse.setTexte(reponseDto.getTexte());
                reponse.setCorrecte(reponseDto.isCorrecte());
                question.addReponse(reponse);
            }
        }

        return questionRepository.save(question);
    }

    // ====================================================================
    // === MÉTHODES DE SUPPRESSION (GARANTIES)                          ===
    // ====================================================================

    @Transactional
    public ResponseEntity<Map<String, Object>> deleteQuestionFinal(Long questionId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("Début suppression - Question ID: " + questionId);
            
            if (!questionRepository.existsById(questionId)) {
                response.put("success", false);
                response.put("message", "Question non trouvée avec l'ID : " + questionId);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            
            entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
            System.out.println("FOREIGN_KEY_CHECKS désactivés");
            
            try {
                int testRelationsDeleted = entityManager.createNativeQuery(
                    "DELETE FROM moscepa_test_questions WHERE question_id = :id")
                    .setParameter("id", questionId)
                    .executeUpdate();
                System.out.println("Relations test supprimées: " + testRelationsDeleted);
                
                int reponsesDeleted = entityManager.createNativeQuery(
                    "DELETE FROM moscepa_reponses WHERE question_id = :id")
                    .setParameter("id", questionId)
                    .executeUpdate();
                System.out.println("Réponses supprimées: " + reponsesDeleted);
                
                int questionDeleted = entityManager.createNativeQuery(
                    "DELETE FROM moscepa_questions WHERE id = :id")
                    .setParameter("id", questionId)
                    .executeUpdate();
                System.out.println("Question supprimée: " + questionDeleted);
                
                if (questionDeleted > 0) {
                    response.put("success", true);
                    response.put("message", "Question supprimée avec succès");
                    response.put("deletedRelations", testRelationsDeleted);
                    response.put("deletedReponses", reponsesDeleted);
                    response.put("questionId", questionId);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.put("success", false);
                    response.put("message", "Échec de la suppression de la question");
                    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                
            } finally {
                entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
                System.out.println("FOREIGN_KEY_CHECKS réactivés");
            }
            
        } catch (Exception e) {
            System.err.println("ERREUR lors de la suppression: " + e.getMessage());
            e.printStackTrace();
            
            try {
                entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
            } catch (Exception ex) {
                // Ignorer
            }
            
            response.put("success", false);
            response.put("message", "Erreur lors de la suppression: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public boolean deleteQuestionAlternative(Long questionId) {
        try {
            String sql = 
                "SET FOREIGN_KEY_CHECKS = 0; " +
                "DELETE FROM moscepa_test_questions WHERE question_id = ?; " +
                "DELETE FROM moscepa_reponses WHERE question_id = ?; " +
                "DELETE FROM moscepa_questions WHERE id = ?; " +
                "SET FOREIGN_KEY_CHECKS = 1";
            
            entityManager.createNativeQuery(sql)
                .setParameter(1, questionId)
                .setParameter(2, questionId)
                .setParameter(3, questionId)
                .executeUpdate();
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Erreur alternative: " + e.getMessage());
            return false;
        }
    }

    // ====================================================================
    // === MÉTHODES DE RECHERCHE ET FILTRAGE                            ===
    // ====================================================================

    public List<QuestionDto> getQuestionsByQuestionnaireId(Long questionnaireId) {
        List<Question> questions = questionRepository.findByQuestionnaire_Id(questionnaireId);
        return questions.stream()
                .map(QuestionDto::new)
                .collect(Collectors.toList());
    }

    public Question getQuestionWithRelations(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question non trouvée avec l'ID : " + id));
    }

    public List<QuestionDto> getQuestionsByChapitreId(Long chapitreId) {
        List<Question> questions = questionRepository.findByChapitreId(chapitreId);
        return questions.stream()
                .map(QuestionDto::new)
                .collect(Collectors.toList());
    }

    public List<QuestionDto> getQuestionsByMatiereId(Long matiereId) {
        List<Question> questions = questionRepository.findQuestionsByMatiereId(matiereId);
        return questions.stream()
                .map(QuestionDto::new)
                .collect(Collectors.toList());
    }

    public List<QuestionDto> getBanqueQuestionsByChapitreId(Long chapitreId) {
        List<Question> questions = questionRepository.findBanqueQuestionsByChapitreId(chapitreId);
        return questions.stream()
                .map(QuestionDto::new)
                .collect(Collectors.toList());
    }

    public List<QuestionDto> getQuestionsByQuestionnaireChapitreId(Long chapitreId) {
        List<Question> questions = questionRepository.findByQuestionnaireChapitreId(chapitreId);
        return questions.stream()
                .map(QuestionDto::new)
                .collect(Collectors.toList());
    }

    // ====================================================================
    // === MÉTHODES UTILITAIRES                                          ===
    // ====================================================================

    public boolean questionExists(Long questionId) {
        return questionRepository.existsById(questionId);
    }

    public boolean canDeleteQuestion(Long questionId) {
        try {
            String checkSQL = 
                "SELECT COUNT(*) FROM moscepa_test_questions WHERE question_id = ?";
            
            Long count = ((Number) entityManager.createNativeQuery(checkSQL)
                .setParameter(1, questionId)
                .getSingleResult()).longValue();
            
            return count == 0;
            
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> getQuestionStats(Long questionId) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            boolean exists = questionRepository.existsById(questionId);
            stats.put("exists", exists);
            
            if (!exists) {
                stats.put("message", "Question non trouvée");
                return stats;
            }
            
            Long testCount = ((Number) entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM moscepa_test_questions WHERE question_id = ?")
                .setParameter(1, questionId)
                .getSingleResult()).longValue();
            stats.put("usedInTests", testCount);
            
            Long reponseCount = ((Number) entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM moscepa_reponses WHERE question_id = ?")
                .setParameter(1, questionId)
                .getSingleResult()).longValue();
            stats.put("reponseCount", reponseCount);
            
            stats.put("canDelete", testCount == 0);
            
        } catch (Exception e) {
            stats.put("error", e.getMessage());
        }
        
        return stats;
    }

    @Transactional
    public Question duplicateQuestion(Long questionId) {
        Question original = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question non trouvée"));

        Question duplicate = new Question();
        duplicate.setEnonce("[COPIE] " + original.getEnonce());
        duplicate.setPoints(original.getPoints());
        duplicate.setTypeQuestion(original.getTypeQuestion());
        duplicate.setQuestionnaire(original.getQuestionnaire());
        duplicate.setChapitre(original.getChapitre());
        
        if (original.getReponses() != null) {
            for (Reponse reponse : original.getReponses()) {
                Reponse reponseDupliquee = new Reponse();
                reponseDupliquee.setTexte(reponse.getTexte());
                reponseDupliquee.setCorrecte(reponse.isCorrecte());
                duplicate.addReponse(reponseDupliquee);
            }
        }
        
        return questionRepository.save(duplicate);
    }

    @Transactional
    public Question changeQuestionnaire(Long questionId, Long newQuestionnaireId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question non trouvée"));
        
        Questionnaire newQuestionnaire = questionnaireRepository.findById(newQuestionnaireId)
                .orElseThrow(() -> new EntityNotFoundException("Questionnaire non trouvé"));
        
        if (question.getQuestionnaire() != null) {
            Questionnaire oldQuestionnaire = question.getQuestionnaire();
            oldQuestionnaire.getQuestions().remove(question);
            questionnaireRepository.save(oldQuestionnaire);
        }
        
        question.setQuestionnaire(newQuestionnaire);
        newQuestionnaire.getQuestions().add(question);
        
        return questionRepository.save(question);
    }

    public long countAllQuestions() {
        return questionRepository.count();
    }

    public long countQuestionsInQuestionnaire(Long questionnaireId) {
        return questionRepository.findByQuestionnaire_Id(questionnaireId).size();
    }

    public long countQuestionsInChapitre(Long chapitreId) {
        return questionRepository.findByChapitreId(chapitreId).size();
    }

    public List<QuestionDto> searchQuestions(String searchTerm) {
        return questionRepository.findAll().stream()
                .filter(q -> q.getEnonce().toLowerCase().contains(searchTerm.toLowerCase()))
                .map(QuestionDto::new)
                .collect(Collectors.toList());
    }

    public List<QuestionDto> getQuestionsByType(String typeQuestion) {
        return questionRepository.findAll().stream()
                .filter(q -> q.getTypeQuestion() != null && q.getTypeQuestion().name().equals(typeQuestion))
                .map(QuestionDto::new)
                .collect(Collectors.toList());
    }

    public List<QuestionDto> getQuestionsWithMinPoints(double minPoints) {
        return questionRepository.findAll().stream()
                .filter(q -> q.getPoints() >= minPoints)
                .map(QuestionDto::new)
                .collect(Collectors.toList());
    }
}