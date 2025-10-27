package com.moscepa.repository;

import com.moscepa.entity.Parcours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ParcoursRepository extends JpaRepository<Parcours, Long> {

    /**
     * Trouve toutes les entrées de parcours pour un utilisateur donné,
     * triées par date d'ajout la plus récente.
     */
    List<Parcours> findByUtilisateurIdOrderByDateAjoutDesc(Long utilisateurId);

    // ====================================================================
    // ===> NOUVELLE MÉTHODE POUR LA VÉRIFICATION ANTI-DOUBLONS <===
    // ====================================================================
    /**
     * Vérifie s'il existe déjà un parcours pour un utilisateur, un chapitre et un type donnés.
     * Spring Data JPA va automatiquement générer la requête SQL correspondante.
     *
     * @param utilisateurId L'ID de l'utilisateur.
     * @param chapitreId L'ID du chapitre.
     * @param type Le type de parcours (RECOMMANDE ou CHOISI).
     * @return true si une entrée correspondante existe, false sinon.
     */
    boolean existsByUtilisateurIdAndChapitreIdAndType(Long utilisateurId, Long chapitreId, Parcours.TypeParcours type);
}
