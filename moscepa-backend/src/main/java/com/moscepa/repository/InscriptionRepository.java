package com.moscepa.repository;

import com.moscepa.entity.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

    
    List<Inscription> findByEtudiantId(Long etudiantId);

}
