package com.moscepa.dto;

// Ce DTO représente un enseignant avec les infos de base à afficher
public class EnseignantDto {
    private Long id;
    private String nom;
    private String prenom;

    // Constructeur vide pour Jackson
    public EnseignantDto() {}

    // Constructeur plein (utile)
    public EnseignantDto(Long id, String nom, String prenom) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
    }

    // Getters et Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
}
