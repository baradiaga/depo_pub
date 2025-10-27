package com.moscepa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO pour l'entité Chapitre.
 * Mis à jour pour inclure les champs 'niveau' et 'objectif'.
 */
public class ChapitreDto {

    private Long id;

    @NotBlank(message = "Le nom du chapitre est obligatoire")
    @Size(max = 150)
    private String nom;

    @Size(max = 500)
    private String description;

    @NotNull(message = "L'ID de la matière est obligatoire")
    private Long matiereId;

    private String matiereNom;
    private List<TestDto> tests;

    // ====================================================================
    // AJOUT : Champs nécessaires pour les composants d'affichage.
    // ====================================================================
    private Integer niveau;
    private String objectif;

    // Constructeurs
    public ChapitreDto() {}

    public ChapitreDto(Long id, String nom, String description, Long matiereId) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.matiereId = matiereId;
    }

    // Getters et Setters existants
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getMatiereId() { return matiereId; }
    public void setMatiereId(Long matiereId) { this.matiereId = matiereId; }
    public String getMatiereNom() { return matiereNom; }
    public void setMatiereNom(String matiereNom) { this.matiereNom = matiereNom; }
    public List<TestDto> getTests() { return tests; }
    public void setTests(List<TestDto> tests) { this.tests = tests; }

    // ====================================================================
    // AJOUT : Getters et Setters pour les nouveaux champs.
    // ====================================================================
    public Integer getNiveau() {
        return niveau;
    }

    public void setNiveau(Integer niveau) {
        this.niveau = niveau;
    }

    public String getObjectif() {
        return objectif;
    }

    public void setObjectif(String objectif) {
        this.objectif = objectif;
    }

    @Override
    public String toString() {
        return "ChapitreDto{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", matiereId=" + matiereId +
                ", niveau=" + niveau + // Ajout pour le débogage
                '}';
    }
}
