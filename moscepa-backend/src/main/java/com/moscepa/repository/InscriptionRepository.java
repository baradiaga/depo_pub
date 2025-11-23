// Fichier : src/main/java/com/moscepa/repository/InscriptionRepository.java

package com.moscepa.repository;

import com.moscepa.entity.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {
    // On peut ajouter ici des méthodes de recherche si nécessaire, par exemple :
    // boolean existsByEtudiantIdAndMatiereId(Long etudiantId, Long matiereId);
    boolean existsByEtudiantIdAndMatiereId(Long etudiantId, Long matiereId);
}
