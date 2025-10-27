package com.moscepa.entity;

/**
 * Énumération des types de ressources dans le système MOSCEPA
 */
public enum TypeRessource {
    PDF("PDF"),
    VIDEO("Vidéo"),
    QUIZ("Quiz");

    private final String libelle;

    TypeRessource(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    @Override
    public String toString() {
        return libelle;
    }
}

