package com.moscepa.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) pour recevoir la liste des IDs de chapitres
 * depuis le front-end lors de l'enregistrement d'un parcours.
 * Le nom de la variable "chapitresChoisisIds" doit correspondre EXACTEMENT
 * à la clé utilisée dans le payload JSON du service Angular.
 */
public class ParcoursRequestDto {

    private List<Long> chapitresChoisisIds;

    // Un constructeur vide est nécessaire pour la désérialisation JSON.
    public ParcoursRequestDto() {
    }

    // --- Getter et Setter ---
    // Indispensables pour que la librairie Jackson puisse remplir l'objet.
    
    public List<Long> getChapitresChoisisIds() {
        return chapitresChoisisIds;
    }

    public void setChapitresChoisisIds(List<Long> chapitresChoisisIds) {
        this.chapitresChoisisIds = chapitresChoisisIds;
    }
}
