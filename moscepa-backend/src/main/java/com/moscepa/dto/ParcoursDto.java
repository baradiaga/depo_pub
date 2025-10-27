package com.moscepa.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) pour envoyer les données des parcours
 * au front-end. Sa structure (recommandes, choisis, mixtes) correspond
 * à l'interface 'ParcoursData' dans le service Angular.
 */
public class ParcoursDto {

    private List<ParcoursItemDto> recommandes;
    private List<ParcoursItemDto> choisis;
    private List<ParcoursItemDto> mixtes;

    // Un constructeur vide est utile.
    public ParcoursDto() {
    }

    // --- Getters et Setters ---
    // Indispensables pour que la librairie Jackson puisse convertir cet objet en JSON.

    public List<ParcoursItemDto> getRecommandes() {
        return recommandes;
    }

    public void setRecommandes(List<ParcoursItemDto> recommandes) {
        this.recommandes = recommandes;
    }

    public List<ParcoursItemDto> getChoisis() {
        return choisis;
    }

    public void setChoisis(List<ParcoursItemDto> choisis) {
        this.choisis = choisis;
    }

    public List<ParcoursItemDto> getMixtes() {
        return mixtes;
    }

    public void setMixtes(List<ParcoursItemDto> mixtes) {
        this.mixtes = mixtes;
    }
}
