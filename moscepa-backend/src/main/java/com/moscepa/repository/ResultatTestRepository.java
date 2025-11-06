// Fichier : ResultatTestRepository.java (Version Finale et Corrigée)

package com.moscepa.repository;

import com.moscepa.entity.ResultatTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResultatTestRepository extends JpaRepository<ResultatTest, Long> {

    Optional<ResultatTest> findByEtudiantIdAndTestId(Long etudiantId, Long testId);

    // ====================================================================
    // === CORRECTION DE LA REQUÊTE JPQL                                ===
    // ====================================================================
    /**
     * Trouve les derniers résultats de test pour un étudiant et une matière (EC) donnés.
     * La requête a été corrigée pour utiliser 'elementConstitutif' au lieu de 'matiere'.
     */
    @Query("SELECT r FROM ResultatTest r WHERE r.etudiant.id = :etudiantId AND r.test.chapitre.elementConstitutif.id = :matiereId ORDER BY r.dateTest DESC")
    List<ResultatTest> findByEtudiantIdAndMatiereId(@Param("etudiantId") Long etudiantId, @Param("matiereId") Long matiereId);

    /**
     * Trouve le dernier résultat de test pour un étudiant et un chapitre donnés.
     */
    @Query("SELECT r FROM ResultatTest r WHERE r.etudiant.id = :etudiantId AND r.test.chapitre.id = :chapitreId ORDER BY r.dateTest DESC")
    List<ResultatTest> findLatestByEtudiantAndChapitre(@Param("etudiantId") Long etudiantId, @Param("chapitreId") Long chapitreId);
}
