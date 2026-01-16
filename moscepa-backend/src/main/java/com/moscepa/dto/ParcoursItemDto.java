package com.moscepa.dto;

import java.util.Objects;

public class ParcoursItemDto {

    private Long chapitreId;
    private String chapitreNom;
    private String matiereNom;
    private double dernierScore;

    public ParcoursItemDto() {}

    public ParcoursItemDto(Long chapitreId, String chapitreNom, String matiereNom, double dernierScore) {
        this.chapitreId = chapitreId;
        this.chapitreNom = chapitreNom;
        this.matiereNom = matiereNom;
        this.dernierScore = dernierScore;
    }

    // --- CRUCIAL POUR LE .distinct() DANS LE SERVICE ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParcoursItemDto that = (ParcoursItemDto) o;
        return Objects.equals(chapitreId, that.chapitreId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chapitreId);
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
