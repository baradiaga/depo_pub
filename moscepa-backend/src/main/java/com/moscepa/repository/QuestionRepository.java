package com.moscepa.repository;

import com.moscepa.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // --- MÉTHODES EXISTANTES ---
    
    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.reponses WHERE q.questionnaire.chapitre.id = :chapitreId")
    List<Question> findByQuestionnaireChapitreId(@Param("chapitreId") Long chapitreId);

    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.reponses WHERE q.questionnaire IS NULL AND q.chapitre.id = :chapitreId")
    List<Question> findBanqueQuestionsByChapitreId(@Param("chapitreId") Long chapitreId);

    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.reponses WHERE q.questionnaire IS NULL AND q.chapitre.id IN :chapitresIds")
    List<Question> findBanqueQuestionsByChapitresIds(@Param("chapitresIds") List<Long> chapitresIds);

    List<Question> findByQuestionnaireChapitreElementConstitutifId(Long elementConstitutifId);

    // --- MÉTHODES CORRIGÉES ET AJOUTÉES ---
    
    /**
     * Trouve toutes les questions d'un questionnaire
     */
    List<Question> findByQuestionnaire_Id(Long questionnaireId);
    
    /**
     * Trouve toutes les questions d'un chapitre (relation directe)
     */
    List<Question> findByChapitreId(Long chapitreId);
    
    /**
     * Trouve toutes les questions d'une matière (ElementConstitutif)
     */
    @Query("SELECT q FROM Question q JOIN q.chapitre c WHERE c.elementConstitutif.id = :matiereId")
    List<Question> findQuestionsByMatiereId(@Param("matiereId") Long matiereId);
    
    // --- MÉTHODES POUR LE SERVICE DE SUPPRESSION ---
    
    /**
     * Trouve une question avec ses réponses
     */
    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.reponses WHERE q.id = :id")
    Optional<Question> findByIdWithReponses(@Param("id") Long id);
    
    /**
     * Trouve une question avec toutes ses relations (tests, réponses, questionnaire)
     */
    @Query("SELECT q FROM Question q " +
           "LEFT JOIN FETCH q.reponses " +
           "LEFT JOIN FETCH q.tests " +
           "LEFT JOIN FETCH q.questionnaire " +
           "WHERE q.id = :id")
    Optional<Question> findByIdWithRelations(@Param("id") Long id);
    
    /**
     * Trouve une question avec ses tests (pour la suppression)
     */
    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.tests WHERE q.id = :id")
    Optional<Question> findByIdWithTests(@Param("id") Long id);
    
    /**
     * Vérifie si une question appartient à un questionnaire
     */
    @Query("SELECT COUNT(q) > 0 FROM Question q WHERE q.id = :questionId AND q.questionnaire.id = :questionnaireId")
    boolean belongsToQuestionnaire(@Param("questionId") Long questionId, @Param("questionnaireId") Long questionnaireId);
    
    // --- MÉTHODES POUR LA BANQUE DE QUESTIONS ---
    
    /**
     * Trouve toutes les questions sans questionnaire (banque de questions)
     */
    @Query("SELECT q FROM Question q WHERE q.questionnaire IS NULL")
    List<Question> findBanqueQuestions();
    
    /**
     * Trouve toutes les questions avec questionnaire
     */
    @Query("SELECT q FROM Question q WHERE q.questionnaire IS NOT NULL")
    List<Question> findQuestionsWithQuestionnaire();
    
    // --- MÉTHODES DE FILTRAGE ---
    
    /**
     * Trouve les questions par type
     */
    List<Question> findByTypeQuestion(String typeQuestion);
    
    /**
     * Trouve les questions par chapitre et type
     */
    List<Question> findByChapitreIdAndTypeQuestion(Long chapitreId, String typeQuestion);
    
    /**
     * Compte le nombre de questions dans un questionnaire
     */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.questionnaire.id = :questionnaireId")
    long countByQuestionnaireId(@Param("questionnaireId") Long questionnaireId);
    
    /**
     * Compte le nombre de questions dans un chapitre
     */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.chapitre.id = :chapitreId")
    long countByChapitreId(@Param("chapitreId") Long chapitreId);
}