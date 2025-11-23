// Fichier : src/main/java/com/moscepa/repository/ChapitreRepository.java (Version finale corrigée)

package com.moscepa.repository;

import com.moscepa.entity.Chapitre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- IMPORT AJOUTÉ
import org.springframework.data.repository.query.Param; // <-- IMPORT AJOUTÉ

import java.util.List;
import java.util.Optional;

public interface ChapitreRepository extends JpaRepository<Chapitre, Long> {

    List<Chapitre> findByElementConstitutifId(Long elementConstitutifId);

    Optional<Chapitre> findByElementConstitutifNomAndNiveau(String nom, Integer niveau);

    // ====================================================================
    // === NOUVELLE MÉTHODE AJOUTÉE POUR LA PAGE DE DÉTAIL DE L'ÉTUDIANT   ===
    // ====================================================================
    /**
     * Trouve un chapitre par son ID et charge immédiatement (fetch) sa collection de sections.
     * L'utilisation de 'LEFT JOIN FETCH' est une optimisation qui évite le problème
     * des requêtes N+1 et garantit que la collection 'sections' n'est pas vide.
     * @param id L'ID du chapitre à trouver.
     * @return Un Optional contenant le Chapitre avec ses sections.
     */
    @Query("SELECT c FROM Chapitre c LEFT JOIN FETCH c.sections WHERE c.id = :id")
    Optional<Chapitre> findChapitreCompletById(@Param("id") Long id);

    @Query("SELECT c FROM Chapitre c LEFT JOIN FETCH c.sections WHERE c.elementConstitutif.id = :matiereId ORDER BY c.ordre ASC")
List<Chapitre> findAllChapitresCompletsByMatiereId(@Param("matiereId") Long matiereId);
    Integer countByElementConstitutifId(Long matiereId);

}
