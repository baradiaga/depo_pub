package com.moscepa.repository;

import com.moscepa.entity.Etudiant;
import com.moscepa.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Etudiant
 */
@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    /**
     * Trouve un étudiant par son utilisateur
     */
    Optional<Etudiant> findByUtilisateur(Utilisateur utilisateur);
    
    /**
     * Trouve un étudiant par l'ID de son utilisateur
     */
    @Query("SELECT e FROM Etudiant e WHERE e.utilisateur.id = :utilisateurId")
    Optional<Etudiant> findByUtilisateurId(@Param("utilisateurId") Long utilisateurId);

    /**
     * Trouve un étudiant par l'email de son utilisateur
     */
    @Query("SELECT e FROM Etudiant e WHERE e.utilisateur.email = :email")
    Optional<Etudiant> findByUtilisateurEmail(@Param("email") String email);

    /**
     * Trouve tous les étudiants d'un enseignant
     */
    List<Etudiant> findByEnseignant(Utilisateur enseignant);

    /**
     * Trouve tous les étudiants d'un enseignant par ID
     */
    @Query("SELECT e FROM Etudiant e WHERE e.enseignant.id = :enseignantId")
    List<Etudiant> findByEnseignantId(@Param("enseignantId") Long enseignantId);

    /**
     * Trouve tous les étudiants actifs
     */
    @Query("SELECT e FROM Etudiant e WHERE e.utilisateur.actif = true")
    List<Etudiant> findAllActifs();

    /**
     * Recherche d'étudiants par nom ou prénom
     */
    @Query("SELECT e FROM Etudiant e WHERE " +
           "LOWER(e.utilisateur.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.utilisateur.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Etudiant> findByNomOrPrenomContaining(@Param("searchTerm") String searchTerm);
}

