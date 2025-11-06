// Fichier : src/main/java/com/moscepa/repository/SectionRepository.java (Complété)

package com.moscepa.repository;

import com.moscepa.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pour l'entité Section.
 * JpaRepository<Section, Long> nous donne accès à toutes les opérations CRUD de base
 * (Create, Read, Update, Delete) pour l'entité Section, en utilisant un Long comme type pour l'ID.
 */
@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    // ====================================================================
    // === MÉTHODE AJOUTÉE ICI                                          ===
    // ====================================================================
    /**
     * Compte le nombre de sections associées à un chapitre spécifique.
     * Spring Data JPA va automatiquement générer la requête SQL "SELECT count(*) FROM moscepa_sections WHERE chapitre_id = ?".
     * C'est une méthode très efficace pour déterminer le prochain numéro d'ordre.
     *
     * @param chapitreId L'ID du chapitre pour lequel on veut compter les sections.
     * @return Le nombre de sections existantes.
     */
    int countByChapitreId(Long chapitreId);
}
