package com.moscepa.repository;

import com.moscepa.entity.Uefr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UefrRepository extends JpaRepository<Uefr, Long> {
    
    Optional<Uefr> findBySigle(String sigle);
    
    // Correction pour findByEtablissementId
    @Query("SELECT u FROM Uefr u WHERE u.etablissement.id = :etablissementId")
    List<Uefr> findByEtablissementId(@Param("etablissementId") Long etablissementId);
    
    boolean existsBySigle(String sigle);
    
    boolean existsBySigleAndIdNot(String sigle, Long id);
}