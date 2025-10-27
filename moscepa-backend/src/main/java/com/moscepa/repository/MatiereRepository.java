package com.moscepa.repository;

import com.moscepa.entity.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Matiere
 */
@Repository
public interface MatiereRepository extends JpaRepository<Matiere, Long> {

    /**
     * Trouve une matière par son nom
     */
    Optional<Matiere> findByNom(String nom);

    /**
     * Vérifie si une matière existe par son nom
     */
    boolean existsByNom(String nom);

    /**
     * Recherche de matières par nom (insensible à la casse)
     */
    @Query("SELECT m FROM Matiere m WHERE LOWER(m.nom) LIKE LOWER(CONCAT('%', :nom, '%'))")
    List<Matiere> findByNomContainingIgnoreCase(@Param("nom") String nom);

    /**
     * Trouve toutes les matières triées par nom
     */
    @Query("SELECT m FROM Matiere m ORDER BY m.nom ASC")
    List<Matiere> findAllOrderByNom();

    /**
     * Récupère uniquement les noms des matières
     */
    @Query("SELECT m.nom FROM Matiere m ORDER BY m.nom ASC")
    List<String> findAllNoms();
}

