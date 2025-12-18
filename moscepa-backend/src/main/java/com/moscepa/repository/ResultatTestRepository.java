// Fichier : src/main/java/com/moscepa/repository/ResultatTestRepository.java (Complet et Corrigé)

package com.moscepa.repository;

import com.moscepa.entity.ResultatTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResultatTestRepository extends JpaRepository<ResultatTest, Long> {

    Optional<ResultatTest> findByEtudiantIdAndTestId(Long etudiantId, Long testId);

    @Query("SELECT r FROM ResultatTest r WHERE r.etudiant.id = :etudiantId AND r.test.chapitre.elementConstitutif.id = :matiereId ORDER BY r.dateTest DESC")
    List<ResultatTest> findByEtudiantIdAndMatiereId(@Param("etudiantId") Long etudiantId, @Param("matiereId") Long matiereId);

    @Query("SELECT r FROM ResultatTest r WHERE r.etudiant.id = :etudiantId AND r.test.chapitre.id = :chapitreId ORDER BY r.dateTest DESC")
    List<ResultatTest> findLatestByEtudiantAndChapitre(@Param("etudiantId") Long etudiantId, @Param("chapitreId") Long chapitreId);

    // ====================================================================
    // === CORRECTION DU NOM DE LA MÉTHODE POUR L'HISTORIQUE            ===
    // ====================================================================
    /**
     * Le nom de la méthode a été corrigé pour utiliser 'DateTest' au lieu de 'DateSoumission',
     * ce qui correspond au nom de la propriété dans l'entité ResultatTest.
     */
    List<ResultatTest> findByEtudiantIdOrderByDateTestDesc(Long etudiantId);
    Optional<ResultatTest> findTopByEtudiantIdAndTestIdOrderByDateTestDesc(Long etudiantId, Long testId);

    
    
     // NOUVELLE MÉTHODE: Récupère tous les résultats d'un étudiant avec jointures
       @Query("SELECT DISTINCT rt FROM ResultatTest rt " +
          "JOIN FETCH rt.test t " +
          "JOIN FETCH t.chapitre c " +
          "JOIN FETCH c.elementConstitutif ec " +
          "WHERE rt.etudiant.id = :etudiantId")
   List<ResultatTest> findByEtudiantIdWithDetails(@Param("etudiantId") Long etudiantId);
}
