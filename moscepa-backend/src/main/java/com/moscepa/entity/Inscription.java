// Fichier : src/main/java/com/moscepa/entity/Inscription.java (Version Simple et Sûre)

package com.moscepa.entity;

// Aucun import de jakarta.persistence.
// Cette classe est maintenant un simple "POJO" (Plain Old Java Object).
// Elle ne sera plus gérée par Hibernate et ne causera plus de conflits.

public class Inscription {

    // Simples champs, sans aucune annotation JPA (@Entity, @Id, @Table, etc.)
    private Long id;
    private Object etudiant; // On utilise Object pour une flexibilité maximale
    private Object matiere;  // On utilise Object pour une flexibilité maximale

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Object getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(Object etudiant) {
        this.etudiant = etudiant;
    }

    public Object getMatiere() {
        return matiere;
    }

    public void setMatiere(Object matiere) {
        this.matiere = matiere;
    }
}
