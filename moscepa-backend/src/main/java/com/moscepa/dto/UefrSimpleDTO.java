package com.moscepa.dto;

public class UefrSimpleDTO {
    private Long id;
    private String nom;
    private String sigle;
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getSigle() { return sigle; }
    public void setSigle(String sigle) { this.sigle = sigle; }
}