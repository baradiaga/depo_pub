package com.moscepa.repository;

import com.moscepa.entity.ResultatTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultatTestRepository extends JpaRepository<ResultatTest, Long> {

    // Tes autres méthodes restent ici...
    @Query("SELECT r FROM ResultatTest r WHERE r.etudiant.id = :etudiantId ORDER BY r.dateTest DESC")
    List<ResultatTest> findByEtudiantId(@Param("etudiantId") Long etudiantId);

    @Query("SELECT r FROM ResultatTest r WHERE r.test.id = :testId ORDER BY r.dateTest DESC")
    List<ResultatTest> findByTestId(@Param("testId") Long testId);

    @Query("SELECT r FROM ResultatTest r WHERE r.test.chapitre.id = :chapitreId ORDER BY r.dateTest DESC")
    List<ResultatTest> findByChapitreId(@Param("chapitreId") Long chapitreId);

    @Query("SELECT r FROM ResultatTest r WHERE r.etudiant.id = :etudiantId AND r.test.id = :testId ORDER BY r.dateTest DESC")
    Optional<ResultatTest> findLatestByEtudiantIdAndTestId(@Param("etudiantId") Long etudiantId, @Param("testId") Long testId);

    @Query("SELECT r FROM ResultatTest r WHERE r.etudiant.id = :etudiantId AND r.test.id = :testId ORDER BY r.score DESC")
    Optional<ResultatTest> findBestScoreByEtudiantIdAndTestId(@Param("etudiantId") Long etudiantId, @Param("testId") Long testId);

    /**
     * Récupère le meilleur score (le plus élevé) obtenu par un étudiant pour un test donné.
     */
    @Query("SELECT MAX(r.score) FROM ResultatTest r WHERE r.etudiant.id = :etudiantId AND r.test.id = :testId")
    Optional<Double> findMaxScoreByEtudiantAndTest(@Param("etudiantId") Long etudiantId, @Param("testId") Long testId);

    @Query("SELECT AVG(r.score) FROM ResultatTest r WHERE r.test.id = :testId")
    Double findAverageScoreByTestId(@Param("testId") Long testId);

    @Query("SELECT r FROM ResultatTest r WHERE r.etudiant.id = :etudiantId AND r.test.chapitre.matiere.id = :matiereId ORDER BY r.dateTest DESC")
    List<ResultatTest> findByEtudiantIdAndMatiereId(@Param("etudiantId") Long etudiantId, @Param("matiereId") Long matiereId);


    // ====================================================================
    // ===> CORRECTION FINALE AVEC @Query EXPLICITE <===
    // ====================================================================
    /**
     * Trouve tous les résultats d'un étudiant pour un chapitre, triés par date la plus récente.
     * On utilise une requête JPQL explicite pour une robustesse maximale.
     * La méthode retourne une List, car c'est plus simple à gérer dans le service.
     */
    @Query("SELECT r FROM ResultatTest r WHERE r.etudiant.id = :etudiantId AND r.test.chapitre.id = :chapitreId ORDER BY r.dateTest DESC")
    List<ResultatTest> findLatestByEtudiantAndChapitre(@Param("etudiantId") Long etudiantId, @Param("chapitreId") Long chapitreId);
}
