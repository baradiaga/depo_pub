// Fichier : src/main/java/com/moscepa/repository/UtilisateurRepository.java (Version Complète et Finale)

package com.moscepa.repository;

import com.moscepa.entity.Role;
import com.moscepa.entity.Utilisateur;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    // --- Méthodes de base ---
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Utilisateur> findByRole(Role role);

    // --- Surcharge de findById pour forcer le chargement des matières ---
    @Override
    @EntityGraph(attributePaths = "matieresInscrites")
    Optional<Utilisateur> findById(Long id);

    // ====================================================================
    // === VOS MÉTHODES PERSONNALISÉES RÉ-AJOUTÉES ICI                  ===
    // ====================================================================

    List<Utilisateur> findByActifTrue();
    List<Utilisateur> findByRoleAndActifTrue(Role role);

    @Query("SELECT u FROM Utilisateur u WHERE " +
           "LOWER(u.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Utilisateur> findByNomOrPrenomContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    @Query("SELECT u FROM Utilisateur u WHERE u.role = 'ENSEIGNANT' AND u.actif = true")
    List<Utilisateur> findAllEnseignantsActifs();

    List<Utilisateur> findAllByOrderByDateCreationDesc();

    List<Utilisateur> findByRoleOrderByNomAscPrenomAsc(Role role);

    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.role = :role")
    long countByRole(@Param("role") Role role);

    @Query("SELECT u FROM Utilisateur u WHERE u.role = 'ADMIN'")
    List<Utilisateur> findAllAdmins();
}
