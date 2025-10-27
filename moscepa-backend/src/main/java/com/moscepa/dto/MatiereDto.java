package com.moscepa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO pour l'entité Matiere
 */
public class MatiereDto {

    private Long id;

    @NotBlank(message = "Le nom de la matière est obligatoire")
    @Size(max = 100)
    private String nom;

    @Size(max = 500)
    private String description;

    private List<ChapitreDto> chapitres;

    // Constructeurs
    public MatiereDto() {}

    public MatiereDto(Long id, String nom, String description) {
        this.id = id;
        this.nom = nom;
        this.description = description;
    }

    // Getters et Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ChapitreDto> getChapitres() {
        return chapitres;
    }

    public void setChapitres(List<ChapitreDto> chapitres) {
        this.chapitres = chapitres;
    }

    @Override
    public String toString() {
        return "MatiereDto{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

