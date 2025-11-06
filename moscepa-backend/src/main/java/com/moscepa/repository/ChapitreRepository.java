package com.moscepa.repository;

import com.moscepa.entity.Chapitre;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ChapitreRepository extends JpaRepository<Chapitre, Long> {

    List<Chapitre> findByElementConstitutifId(Long elementConstitutifId);

    // ====================================================================
    // === CORRECTION DE LA MÉTHODE DE RECHERCHE                        ===
    // ====================================================================
    /**
     * Trouve un Chapitre par le Nom de son ElementConstitutif et par son Niveau.
     * C'est cette méthode que le service va utiliser.
     */
    Optional<Chapitre> findByElementConstitutifNomAndNiveau(String nom, Integer niveau);
}
