package com.moscepa.repository;

import com.moscepa.entity.Chapitre;
import com.moscepa.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {

    List<Test> findByChapitre(Chapitre chapitre);

    List<Test> findByChapitreId(@Param("chapitreId") Long chapitreId);

    Optional<Test> findFirstByChapitreId(@Param("chapitreId") Long chapitreId);

    List<Test> findByTitreContainingIgnoreCase(@Param("titre") String titre);

    Long countByChapitreId(@Param("chapitreId") Long chapitreId);

    Optional<Test> findTopByChapitreId(Long chapitreId);

    List<Test> findByChapitreMatiereId(@Param("matiereId") Long matiereId);

    // ====================================================================
    // ===> NOUVELLE MÉTHODE AJOUTÉE POUR LE SERVICE <===
    // ====================================================================
    /**
     * Vérifie rapidement si un test existe pour un chapitre donné,
     * sans avoir besoin de charger l'entité complète. C'est très performant.
     */
    boolean existsByChapitreId(Long chapitreId);
}
