// Fichier : src/main/java/com/moscepa/dto/ChapitreRecommandationDto.java
package com.moscepa.dto;

public class ChapitreRecommandationDto {
    private Long id;
    private String nom;
    private double score;

    // Constructeur utilisé par le service
    public ChapitreRecommandationDto(Long id, String nom, double score) {
        this.id = id;
        this.nom = nom;
        this.score = score;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
}
