// Fichier : src/main/java/com/moscepa/repository/QuestionRepository.java (Ajout uniquement)

package com.moscepa.repository;

import com.moscepa.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // --- VOS MÉTHODES EXISTANTES (NE PAS TOUCHER) ---
    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.reponses WHERE q.questionnaire.chapitre.id = :chapitreId")
    List<Question> findByQuestionnaireChapitreId(@Param("chapitreId") Long chapitreId);

    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.reponses WHERE q.questionnaire IS NULL AND q.chapitre.id = :chapitreId")
    List<Question> findBanqueQuestionsByChapitreId(@Param("chapitreId") Long chapitreId);

    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.reponses WHERE q.questionnaire IS NULL AND q.chapitre.id IN :chapitresIds")
    List<Question> findBanqueQuestionsByChapitresIds(@Param("chapitresIds") List<Long> chapitresIds);

    List<Question> findByQuestionnaireChapitreElementConstitutifId(Long elementConstitutifId);


    // ====================================================================
    // === AJOUTEZ SEULEMENT CETTE MÉTHODE À LA FIN DE L'INTERFACE      ===
    // ====================================================================
    /**
     * Récupère toutes les questions appartenant à une matière (ElementConstitutif)
     * en se basant sur la relation directe Question -> Chapitre -> ElementConstitutif.
     * C'est la requête la plus efficace pour notre nouveau Test de Connaissance.
     * @param matiereId L'ID de l'ElementConstitutif (la matière).
     * @return Une liste de toutes les questions pour cette matière.
     */
    @Query("SELECT q FROM Question q JOIN q.chapitre c WHERE c.elementConstitutif.id = :matiereId")
    List<Question> findQuestionsByMatiereId(@Param("matiereId") Long matiereId);
    List<Question> findByChapitreId(Long chapitreId);

}
