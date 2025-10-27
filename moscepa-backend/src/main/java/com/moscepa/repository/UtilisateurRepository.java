package com.moscepa.repository;

import com.moscepa.entity.Role;
import com.moscepa.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Utilisateur
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    /**
     * Trouve un utilisateur par son email
     */
    Optional<Utilisateur> findByEmail(String email);

    /**
     * Vérifie si un email existe déjà
     */
    boolean existsByEmail(String email);

    /**
     * Trouve tous les utilisateurs par rôle
     */
    List<Utilisateur> findByRole(Role role);

    /**
     * Trouve tous les utilisateurs actifs
     */
    List<Utilisateur> findByActifTrue();

    /**
     * Trouve tous les utilisateurs par rôle et statut actif
     */
    List<Utilisateur> findByRoleAndActifTrue(Role role);

    /**
     * Recherche d'utilisateurs par nom ou prénom (insensible à la casse)
     */
    @Query("SELECT u FROM Utilisateur u WHERE " +
           "LOWER(u.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Utilisateur> findByNomOrPrenomContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    /**
     * Trouve tous les enseignants actifs
     */
    @Query("SELECT u FROM Utilisateur u WHERE u.role = 'ENSEIGNANT' AND u.actif = true")
    List<Utilisateur> findAllEnseignantsActifs();

    /**
     * Trouve tous les utilisateurs triés par date de création (plus récents en premier)
     */
    List<Utilisateur> findAllByOrderByDateCreationDesc();

    /**
     * Trouve tous les utilisateurs par rôle triés par nom
     */
    List<Utilisateur> findByRoleOrderByNomAscPrenomAsc(Role role);

    /**
     * Compte le nombre d'utilisateurs par rôle
     */
    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.role = :role")
    long countByRole(@Param("role") Role role);

    /**
     * Trouve tous les administrateurs
     */
    @Query("SELECT u FROM Utilisateur u WHERE u.role = 'ADMIN'")
    List<Utilisateur> findAllAdmins();
}

