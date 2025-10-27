package com.moscepa.dto;

// Ce DTO représente un chapitre dans un parcours, avec les informations nécessaires pour l'affichage.
public class ParcoursItemDto {

    private Long chapitreId;
    private String chapitreNom;
    private String matiereNom;
    private double dernierScore; // On simulera ce score pour l'instant

    // --- Constructeurs ---
    public ParcoursItemDto() {}

    public ParcoursItemDto(Long chapitreId, String chapitreNom, String matiereNom, double dernierScore) {
        this.chapitreId = chapitreId;
        this.chapitreNom = chapitreNom;
        this.matiereNom = matiereNom;
        this.dernierScore = dernierScore;
    }

    // --- Getters et Setters ---
    public Long getChapitreId() { return chapitreId; }
    public void setChapitreId(Long chapitreId) { this.chapitreId = chapitreId; }
    public String getChapitreNom() { return chapitreNom; }
    public void setChapitreNom(String chapitreNom) { this.chapitreNom = chapitreNom; }
    public String getMatiereNom() { return matiereNom; }
    public void setMatiereNom(String matiereNom) { this.matiereNom = matiereNom; }
    public double getDernierScore() { return dernierScore; }
    public void setDernierScore(double dernierScore) { this.dernierScore = dernierScore; }
}
