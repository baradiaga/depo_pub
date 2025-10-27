package com.moscepa.entity;

/**
 * Énumération des statuts de recommandation dans le système MOSCEPA
 */
public enum StatutRecommandation {
    NON_COMMENCE("Non commencé"),
    EN_COURS("En cours"),
    TERMINE("Terminé");

    private final String libelle;

    StatutRecommandation(String libelle) {
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

