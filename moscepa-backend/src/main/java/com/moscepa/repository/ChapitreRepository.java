package com.moscepa.repository;

import com.moscepa.entity.Chapitre;
import com.moscepa.entity.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Chapitre.
 */
@Repository
public interface ChapitreRepository extends JpaRepository<Chapitre, Long> {

    /**
     * Trouve tous les chapitres d'une matière.
     */
    List<Chapitre> findByMatiere(Matiere matiere);

    /**
     * Trouve tous les chapitres d'une matière par son ID.
     */
    @Query("SELECT c FROM Chapitre c WHERE c.matiere.id = :matiereId ORDER BY c.nom ASC")
    List<Chapitre> findByMatiereId(@Param("matiereId") Long matiereId);

    /**
     * Recherche des chapitres par nom (insensible à la casse).
     */
    @Query("SELECT c FROM Chapitre c WHERE LOWER(c.nom) LIKE LOWER(CONCAT('%', :nom, '%'))")
    List<Chapitre> findByNomContainingIgnoreCase(@Param("nom") String nom);

    /**
     * Trouve tous les chapitres triés par matière puis par nom.
     */
    @Query("SELECT c FROM Chapitre c ORDER BY c.matiere.nom ASC, c.nom ASC")
    List<Chapitre> findAllOrderByMatiereAndNom();

    /**
     * Compte le nombre de chapitres pour une matière donnée.
     */
    @Query("SELECT COUNT(c) FROM Chapitre c WHERE c.matiere.id = :matiereId")
    Long countByMatiereId(@Param("matiereId") Long matiereId);

    /**
     * Recherche un chapitre en fonction du nom de sa matière et de son niveau.
     * C'est la méthode utilisée par le composant de construction de chapitre pour charger une structure.
     *
     * @param nomMatiere Le nom de la matière (ex: "Mathématiques").
     * @param niveau Le niveau du chapitre (ex: 1).
     * @return Un Optional contenant le Chapitre s'il est trouvé, sinon un Optional vide.
     */
   @Query("SELECT c FROM Chapitre c WHERE LOWER(c.matiere.nom) = LOWER(:nomMatiere) AND c.niveau = :niveau")
    Optional<Chapitre> findByMatiereNomAndNiveau(@Param("nomMatiere") String nomMatiere, @Param("niveau") Integer niveau);
}
