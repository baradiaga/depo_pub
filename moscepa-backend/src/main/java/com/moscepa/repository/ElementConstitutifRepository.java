// Fichier : ElementConstitutifRepository.java (Version Finale et Complète)

package com.moscepa.repository;
import org.springframework.data.jpa.repository.EntityGraph;
import com.moscepa.entity.ElementConstitutif;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ElementConstitutifRepository extends JpaRepository<ElementConstitutif, Long> {
    
    List<ElementConstitutif> findByEnseignantId(Long enseignantId);

    List<ElementConstitutif> findByUniteEnseignementId(Long ueId);

    Optional<ElementConstitutif> findByNom(String nom);
     @EntityGraph(attributePaths = {"chapitres"})
    Optional<ElementConstitutif> findById(Long id);

    // ====================================================================
    // === MÉTHODE MANQUANTE AJOUTÉE ICI                                ===
    // ====================================================================
    /**
     * Compte le nombre d'Éléments Constitutifs liés à une Unité d'Enseignement.
     * Utilisé pour empêcher la suppression d'une UE si elle n'est pas vide.
     */
    long countByUniteEnseignementId(Long uniteEnseignementId);
}
