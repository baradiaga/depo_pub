package com.moscepa.dto;

public class MatiereStatutDto {

    private Long id;
    private String nom;
    private double score;   // anciennement ordre
    private String ec;      // code
    private Integer coefficient; // credit
    private String statut;

    public MatiereStatutDto() {}

    public MatiereStatutDto(Long id, String nom, double score, String ec, Integer coefficient, String statut) {
        this.id = id;
        this.nom = nom;
        this.score = score;
        this.ec = ec;
        this.coefficient = coefficient;
        this.statut = statut;
    }

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    public String getEc() { return ec; }
    public void setEc(String ec) { this.ec = ec; }
    public Integer getCoefficient() { return coefficient; }
    public void setCoefficient(Integer coefficient) { this.coefficient = coefficient; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
