// Fichier : src/main/java/com/moscepa/repository/TagRepository.java

package com.moscepa.repository;

import com.moscepa.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    // Méthode pour trouver un Tag par son nom (utile pour éviter les doublons)
    Optional<Tag> findByNomIgnoreCase(String nom);
}
