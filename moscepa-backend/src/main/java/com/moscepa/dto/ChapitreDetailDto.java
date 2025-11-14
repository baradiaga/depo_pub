// Fichier : src/main/java/com/moscepa/dto/ChapitreDetailDto.java (Version Simplifiée)

package com.moscepa.dto;

import com.moscepa.entity.Chapitre;

/**
 * DTO pour les détails d'un chapitre, utilisé par la page de test.
 * Il ne contient que les informations strictement nécessaires.
 */
public class ChapitreDetailDto {

    private Long id;
    private String nom;
    private Long elementConstitutifId;

    // Constructeur par défaut (important pour certaines bibliothèques)
    public ChapitreDetailDto() {}

    // Constructeur pour la conversion facile depuis l'entité
    public ChapitreDetailDto(Chapitre chapitre) {
        this.id = chapitre.getId();
        this.nom = chapitre.getNom();
        
        if (chapitre.getElementConstitutif() != null) {
            this.elementConstitutifId = chapitre.getElementConstitutif().getId();
        }
    }

    // --- Getters et Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Long getElementConstitutifId() {
        return elementConstitutifId;
    }

    public void setElementConstitutifId(Long elementConstitutifId) {
        this.elementConstitutifId = elementConstitutifId;
    }
}
