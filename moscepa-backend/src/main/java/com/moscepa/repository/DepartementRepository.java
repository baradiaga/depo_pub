package com.moscepa.repository;

import com.moscepa.entity.Departement;
import com.moscepa.entity.Uefr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartementRepository extends JpaRepository<Departement, Long> {
    
    Optional<Departement> findBySigle(String sigle);
    
    List<Departement> findByUefr(Uefr uefr);
    
    @Query("SELECT d FROM Departement d WHERE d.uefr.id = :uefrId")
    List<Departement> findByUefrId(@Param("uefrId") Long uefrId);
    
    boolean existsBySigle(String sigle);
    
    boolean existsByNom(String nom);
    
    boolean existsBySigleAndIdNot(String sigle, Long id);
    
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END " +
           "FROM Departement d WHERE d.sigle = :sigle AND d.uefr = :uefr")
    boolean existsBySigleAndUefr(@Param("sigle") String sigle, @Param("uefr") Uefr uefr);
}