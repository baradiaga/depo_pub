package com.moscepa.repository;

import com.moscepa.entity.Etablissement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EtablissementRepository extends JpaRepository<Etablissement, Long> {
    
    Optional<Etablissement> findBySigle(String sigle);
    
    boolean existsBySigle(String sigle);
    
    boolean existsBySigleAndIdNot(String sigle, Long id);
}