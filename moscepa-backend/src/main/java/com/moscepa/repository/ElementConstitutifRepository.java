package com.moscepa.repository;

import com.moscepa.entity.ElementConstitutif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- NOUVEL IMPORT
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElementConstitutifRepository extends JpaRepository<ElementConstitutif, Long> {

    /**
     * MODIFIÉ : Trouve tous les Éléments Constitutifs pour une UE donnée,
     * en forçant le chargement des relations 'uniteEnseignement' et 'enseignant'.
     * 
     * 'LEFT JOIN FETCH' est la clé : il dit à Hibernate de charger ces relations
     * dans la même requête SQL, évitant ainsi les LazyInitializationException.
     */
    @Query("SELECT ec FROM ElementConstitutif ec " +
           "LEFT JOIN FETCH ec.uniteEnseignement " +
           "LEFT JOIN FETCH ec.enseignant " +
           "WHERE ec.uniteEnseignement.id = :ueId")
    List<ElementConstitutif> findByUniteEnseignementId(Long ueId);
}
