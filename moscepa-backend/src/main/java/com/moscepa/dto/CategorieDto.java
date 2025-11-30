package com.moscepa.dto;

public class CategorieDto {
    private Long id;
    private String nom;
    private Long echelleId;
    private String echelleIntervalle;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Long getEchelleId() { return echelleId; }
    public void setEchelleId(Long echelleId) { this.echelleId = echelleId; }

    public String getEchelleIntervalle() { return echelleIntervalle; }
    public void setEchelleIntervalle(String echelleIntervalle) { this.echelleIntervalle = echelleIntervalle; }
}
