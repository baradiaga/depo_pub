package com.moscepa.repository;

import com.moscepa.entity.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long> {
    // Filtrage pour l'admin
    List<Inscription> findByStatut(String statut);
    
    // Filtrage pour l'étudiant (La clé de ton problème)
    List<Inscription> findByEtudiantIdAndStatut(Long etudiantId, String statut);

    // Vérification d'existence
    boolean existsByEtudiantIdAndMatiereId(Long etudiantId, Long matiereId);
}
