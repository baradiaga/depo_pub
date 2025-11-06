// Fichier : src/main/java/com/moscepa/dto/MatiereSyllabusDto.java (Version Complète)

package com.moscepa.dto;

import java.util.List;

public class MatiereSyllabusDto {
    private Long id;
    private String nom;
    private String code;
    private String description;
    private List<ChapitreSyllabusDto> chapitres;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ChapitreSyllabusDto> getChapitres() {
        return chapitres;
    }

    public void setChapitres(List<ChapitreSyllabusDto> chapitres) {
        this.chapitres = chapitres;
    }
}
