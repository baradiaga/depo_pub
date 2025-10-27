package com.moscepa.repository;

import com.moscepa.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pour l'entité Section.
 * JpaRepository<Section, Long> nous donne accès à toutes les opérations CRUD de base
 * (Create, Read, Update, Delete) pour l'entité Section, en utilisant un Long comme type pour l'ID.
 * Nous n'avons pas besoin d'ajouter de méthodes personnalisées pour l'instant.
 */
@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
}