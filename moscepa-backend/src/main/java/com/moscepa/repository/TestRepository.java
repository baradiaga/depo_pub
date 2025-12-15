// Fichier : TestRepository.java (Version Corrigée et Complète)

package com.moscepa.repository;

import com.moscepa.entity.Chapitre;
import com.moscepa.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional; 
import java.util.List;
import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {

    List<Test> findByChapitre(Chapitre chapitre);

    List<Test> findByChapitreId(@Param("chapitreId") Long chapitreId);

    Optional<Test> findFirstByChapitreId(@Param("chapitreId") Long chapitreId);

    List<Test> findByTitreContainingIgnoreCase(@Param("titre") String titre);

    Long countByChapitreId(@Param("chapitreId") Long chapitreId);

    Optional<Test> findTopByChapitreId(Long chapitreId);

    // ====================================================================
    // === CORRECTION APPLIQUÉE ICI                                     ===
    // ====================================================================
    /**
     * Trouve une liste de Tests en se basant sur l'ID de l'Élément Constitutif
     * auquel leur chapitre est rattaché.
     * 
     * L'ancien nom était "findByChapitreMatiereId", ce qui était incorrect.
     * Le nouveau nom suit la hiérarchie : Chapitre -> ElementConstitutif -> Id
     */
    List<Test> findByChapitreElementConstitutifId(@Param("elementConstitutifId") Long elementConstitutifId);

    /**
     * Vérifie rapidement si un test existe pour un chapitre donné,
     * sans avoir besoin de charger l'entité complète. C'est très performant.
     */
    boolean existsByChapitreId(Long chapitreId);
    /**
 * Récupère tous les tests liés à un questionnaire donné.
 */
List<Test> findByQuestionnaireId(@Param("questionnaireId") Long questionnaireId);
 @Modifying
    @Transactional
    @Query(value = "DELETE FROM moscepa_test_questions WHERE question_id = :questionId", nativeQuery = true)
    void deleteQuestionFromTests(@Param("questionId") Long questionId);
}
