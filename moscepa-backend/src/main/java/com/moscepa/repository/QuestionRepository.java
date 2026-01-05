package com.moscepa.repository;

import com.moscepa.entity.Question;
import com.moscepa.entity.Questionnaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * MÉTHODE DE GÉNÉRATION (Version 2026 - Sécurisée)
     * Cette requête utilise des alias (AS) pour faire correspondre le SQL à l'entité Java.
     * Elle simule (NULL AS) les colonnes manquantes dans votre table physique.
     */
    @Query(value = "SELECT " +
                   "id, " +
                   "date_creation, " +
                   "date_modification, " +
                   "enonce AS question_text, " +      /* Mappe vers le champ Java 'enonce' */
                   "difficulte AS niveau, " +         /* Mappe vers le champ Java 'niveau' */
                   "type_question, " +
                   "points, " +
                   "statut, " +
                   "nombre_utilisations, " +
                   "note_qualite, " +
                   "taux_reussite, " +
                   "auteur_id, " +
                   "chapitre_id, " +
                   "NULL AS questionnaire_id, " +      /* Simule la colonne manquante */
                   "NULL AS reponse_correcte_texte, " + /* Simule la colonne manquante */
                   "NULL AS theme " +                   /* Simule la colonne manquante */
                   "FROM moscepa_banque_questions " +
                   "WHERE chapitre_id = :chapitreId AND difficulte = :difficulte", 
           nativeQuery = true)
    List<Question> findByChapitreIdAndDifficulteNative(
        @Param("chapitreId") Long chapitreId, 
        @Param("difficulte") String difficulte
    );

    // --- MÉTHODES POUR LES SERVICES EXISTANTS (QuestionService) ---

    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.reponses WHERE q.questionnaire.chapitre.id = :chapitreId")
    List<Question> findByQuestionnaireChapitreId(@Param("chapitreId") Long chapitreId);

    // Version corrigée pour suivre le lien : Question -> Questionnaire -> Chapitre -> Matière
@Query("SELECT q FROM Question q " +
       "JOIN q.questionnaire qn " +
       "JOIN qn.chapitre c " +
       "WHERE c.elementConstitutif.id = :matiereId")
List<Question> findQuestionsByMatiereId(@Param("matiereId") Long matiereId);


    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.reponses WHERE q.questionnaire IS NULL AND q.chapitre.id = :chapitreId")
    List<Question> findBanqueQuestionsByChapitreId(@Param("chapitreId") Long chapitreId);

    @Query("SELECT q FROM Question q WHERE q.questionnaire IS NULL")
    List<Question> findBanqueQuestions();

    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.reponses WHERE q.id = :id")
    Optional<Question> findByIdWithReponses(@Param("id") Long id);

    List<Question> findByQuestionnaire_Id(Long questionnaireId);

    List<Question> findByChapitreId(Long chapitreId);
   

@Query("SELECT q FROM Questionnaire q " +
           "LEFT JOIN FETCH q.questions qu " +
           "LEFT JOIN FETCH qu.reponses " +
           "WHERE q.id = :id")
    Optional<Questionnaire> findByIdWithQuestions(@Param("id") Long id);

    
     // AJOUTEZ CETTE MÉTHODE ICI :
    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.reponses WHERE q.id IN :ids")
List<Question> findAllWithReponsesByIds(@Param("ids") List<Long> ids);


}
