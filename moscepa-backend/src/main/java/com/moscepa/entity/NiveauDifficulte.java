package com.moscepa.entity;

/**
 * Énumération des niveaux de difficulté dans le système MOSCEPA
 */
public enum NiveauDifficulte {
    FACILE("Facile"),
    MOYEN("Moyen"),
    DIFFICILE("Difficile");

    private final String libelle;

    NiveauDifficulte(String libelle) {
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

