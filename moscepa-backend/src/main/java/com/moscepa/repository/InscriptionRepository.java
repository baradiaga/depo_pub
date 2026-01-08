package com.moscepa.repository;
import com.moscepa.entity.Utilisateur;
import org.springframework.data.jpa.repository.Query; 
import org.springframework.data.repository.query.Param;
import com.moscepa.entity.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long> {
    // Filtrage pour l'admin
    List<Inscription> findByStatut(String statut);
    
    // Filtrage pour l'étudiant (La clé de ton problème)
    List<Inscription> findByEtudiantIdAndStatut(Long etudiantId, String statut);

    // Vérification d'existence
    boolean existsByEtudiantIdAndMatiereId(Long etudiantId, Long matiereId);
      // Récupère les étudiants uniques pour un tuteur (Responsable Pédagogique)
    @Query("SELECT DISTINCT i.etudiant FROM Inscription i " +
           "WHERE i.matiere.formation.responsablePedagogique.id = :tuteurId " +
           "AND i.actif = true")
    List<Utilisateur> findEtudiantsByTuteur(@Param("tuteurId") Long tuteurId);

    // Permet au tuteur de voir les détails (statut de l'inscription, matière concernée)
    @Query("SELECT i FROM Inscription i " +
           "JOIN FETCH i.etudiant " +
           "JOIN FETCH i.matiere " +
           "WHERE i.matiere.formation.responsablePedagogique.id = :tuteurId")
    List<Inscription> findInscriptionsByTuteur(@Param("tuteurId") Long tuteurId);
}
