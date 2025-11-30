package com.moscepa.repository;

import com.moscepa.entity.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {

    // Recherche par code (utile pour vérifier unicité)
    Optional<Formation> findByCode(String code);

    // Recherche par nom (utile pour vérification avant création)
    Optional<Formation> findByNom(String nom);

    // On pourra ajouter d'autres méthodes spécifiques si nécessaire
    // ex: findAllByStatut, findAllByNiveauEtude, etc.
}
