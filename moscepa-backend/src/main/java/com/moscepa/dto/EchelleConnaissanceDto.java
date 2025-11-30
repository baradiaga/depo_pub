// Fichier : src/main/java/com/moscepa/dto/EchelleConnaissanceDto.java

package com.moscepa.dto;

import com.moscepa.entity.EchelleConnaissance;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EchelleConnaissanceDto {

    private Long id;

    @NotBlank
    @Size(max = 50)
    private String intervalle;

    @NotBlank
    private String description;

    @NotBlank
    private String recommandation;

    // Constructeur pour convertir l'entité en DTO
    public EchelleConnaissanceDto(EchelleConnaissance echelle) {
        this.id = echelle.getId();
        this.intervalle = echelle.getIntervalle();
        this.description = echelle.getDescription();
        this.recommandation = echelle.getRecommandation();
    }

    // Constructeur par défaut
    public EchelleConnaissanceDto() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIntervalle() { return intervalle; }
    public void setIntervalle(String intervalle) { this.intervalle = intervalle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRecommandation() { return recommandation; }
    public void setRecommandation(String recommandation) { this.recommandation = recommandation; }
}
