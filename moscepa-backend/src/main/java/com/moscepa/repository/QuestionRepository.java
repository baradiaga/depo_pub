package com.moscepa.repository;

import com.moscepa.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Méthode existante pour trouver les questions liées à un questionnaire via un chapitre
    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.reponses WHERE q.questionnaire.chapitre.id = :chapitreId")
    List<Question> findByQuestionnaireChapitreId(@Param("chapitreId") Long chapitreId);

    // --- NOUVELLE MÉTHODE À AJOUTER ---
    /**
     * Récupère les questions de la banque (celles où questionnaire_id est NULL)
     * et les filtre par l'ID du chapitre directement associé à la question.
     * JOIN FETCH est utilisé pour charger les réponses en une seule requête.
     */
    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.reponses WHERE q.questionnaire IS NULL AND q.chapitre.id = :chapitreId")
    List<Question> findBanqueQuestionsByChapitreId(@Param("chapitreId") Long chapitreId);

     @Query("SELECT q FROM Question q LEFT JOIN FETCH q.reponses WHERE q.questionnaire IS NULL AND q.chapitre.id IN :chapitresIds")
    List<Question> findBanqueQuestionsByChapitresIds(@Param("chapitresIds") List<Long> chapitresIds);
}
