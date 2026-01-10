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
    
private Double seuilMin;


private Double seuilMax;

@NotBlank(message = "La couleur est obligatoire") // @NotBlank reste correct ici car c'est un String
private String couleur;
    // Constructeur pour convertir l'entité en DTO
    public EchelleConnaissanceDto(EchelleConnaissance echelle) {
        this.id = echelle.getId();
        this.intervalle = echelle.getIntervalle();
        this.description = echelle.getDescription();
        this.recommandation = echelle.getRecommandation();
        this.seuilMin = echelle.getSeuilMin();
        this.seuilMax = echelle.getSeuilMax();
        this.couleur = echelle.getCouleur();
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
    public Double getSeuilMin() { return seuilMin; }
    public void setSeuilMin(Double seuilMin) { this.seuilMin = seuilMin; }

    public Double getSeuilMax() { return seuilMax; }
    public void setSeuilMax(Double seuilMax) { this.seuilMax = seuilMax; }

    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur = couleur; }
}
