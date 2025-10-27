package com.moscepa.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.moscepa.entity.UniteEnseignement;

@Repository
public interface UniteEnseignementRepository extends JpaRepository<UniteEnseignement, Long> {

    /**
     * Surcharge la méthode findAll() pour forcer le chargement de la relation 'responsable'. 'LEFT
     * JOIN FETCH' garantit que le responsable est chargé dans la même requête, même s'il est null,
     * évitant ainsi les problèmes de LazyInitializationException. C'est la correction pour
     * l'affichage de la liste.
     */
    @Override
    @Query("SELECT ue FROM UniteEnseignement ue LEFT JOIN FETCH ue.responsable")
    List<UniteEnseignement> findAll();

    /**
     * Surcharge également la méthode findById pour être cohérent et robuste.
     */
    @Override
    @Query("SELECT ue FROM UniteEnseignement ue LEFT JOIN FETCH ue.responsable WHERE ue.id = :id")
    Optional<UniteEnseignement> findById(Long id);
}
