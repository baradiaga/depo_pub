// Fichier : src/main/java/com/moscepa/dto/ChapitreSyllabusDto.java (Version Complète)

package com.moscepa.dto;

public class ChapitreSyllabusDto {
    private Long id;
    private String nom;
    private Integer ordre;
    
    // Champs pour les nouvelles colonnes du tableau
    private String nomTest;
    private Double resultatScore;
    private String categorie;

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

    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }

    public String getNomTest() {
        return nomTest;
    }

    public void setNomTest(String nomTest) {
        this.nomTest = nomTest;
    }

    public Double getResultatScore() {
        return resultatScore;
    }

    public void setResultatScore(Double resultatScore) {
        this.resultatScore = resultatScore;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
}
