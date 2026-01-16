package com.moscepa.repository;

import com.moscepa.entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
    
    // CETTE MÉTHODE EST LA CLÉ :
    // Elle permet de retrouver l'étudiant via l'ID de son compte utilisateur
    Optional<Etudiant> findByUtilisateurId(Long utilisateurId);
}
