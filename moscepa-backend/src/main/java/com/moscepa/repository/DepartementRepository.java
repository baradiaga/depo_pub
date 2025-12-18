package com.moscepa.repository;

import com.moscepa.entity.Departement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartementRepository extends JpaRepository<Departement, Long> {
    
    boolean existsBySigle(String sigle);
    
    boolean existsBySigleAndIdNot(String sigle, Long id);
    
    // Recherche existante (sans formationId)
    @Query("SELECT d FROM Departement d WHERE " +
           "(:searchTerm IS NULL OR :searchTerm = '' OR " +
           "LOWER(d.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(d.sigle) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:uefrId IS NULL OR d.uefr.id = :uefrId)")
    Page<Departement> search(
        @Param("searchTerm") String searchTerm,
        @Param("uefrId") Long uefrId,
        Pageable pageable);
    
    // AJOUT : Recherche avec formationId
    @Query("SELECT d FROM Departement d WHERE " +
           "(:searchTerm IS NULL OR :searchTerm = '' OR " +
           "LOWER(d.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(d.sigle) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:uefrId IS NULL OR d.uefr.id = :uefrId) AND " +
           "(:formationId IS NULL OR (d.formation IS NOT NULL AND d.formation.id = :formationId))")
    Page<Departement> search(
        @Param("searchTerm") String searchTerm,
        @Param("uefrId") Long uefrId,
        @Param("formationId") Long formationId,
        Pageable pageable);
}