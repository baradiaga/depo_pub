package com.moscepa.entity;

/**
 * Énumération des rôles utilisateur dans le système MOSCEPA
 */
public enum Role {
    ADMIN("Administrateur"),
    ETUDIANT("Étudiant"),
    ENSEIGNANT("Enseignant"),
    TUTEUR("Tuteur"),
    TECHNOPEDAGOGUE("Technopédagogue"),
    RESPONSABLE_FORMATION("Responsable de Formation");

    private final String libelle;

    Role(String libelle) {
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

