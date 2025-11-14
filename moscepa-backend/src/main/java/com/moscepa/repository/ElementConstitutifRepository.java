// Fichier : ElementConstitutifRepository.java (Version "Double Recherche")

package com.moscepa.repository;

import com.moscepa.entity.ElementConstitutif;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ElementConstitutifRepository extends JpaRepository<ElementConstitutif, Long> {
    
    // --- Vos méthodes existantes (INCHANGÉES) ---
    List<ElementConstitutif> findByEnseignantId(Long enseignantId);
    List<ElementConstitutif> findByUniteEnseignementId(Long ueId);
    Optional<ElementConstitutif> findByNom(String nom);
    @EntityGraph(attributePaths = {"chapitres"})
    Optional<ElementConstitutif> findById(Long id);
    long countByUniteEnseignementId(Long uniteEnseignementId);

    // ====================================================================
    // === CORRECTION ULTIME : ON CHERCHE DANS LES DEUX TABLES À LA FOIS ===
    // ====================================================================
    @Query(
        value = "SELECT ec.* FROM moscepa_elements_constitutifs ec " +
                "WHERE ec.id IN (" +
                "    SELECT i1.ec_id FROM moscepa_inscriptions i1 WHERE i1.etudiant_id = :etudiantId" +
                "    UNION" +
                "    SELECT i2.ec_id FROM moscepa_inscriptions_ec i2 WHERE i2.etudiant_id = :etudiantId" +
                ")",
        nativeQuery = true
    )
    List<ElementConstitutif> findMatieresByEtudiantIdSqlNatif(@Param("etudiantId") Long etudiantId);
}
