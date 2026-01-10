// Fichier : src/main/java/com/moscepa/entity/EchelleConnaissance.java

package com.moscepa.entity;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;
@Entity
@Table(name = "moscepa_echelles_connaissance")
public class EchelleConnaissance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String intervalle; // Ex: [0% - 34%]

    @Column(nullable = false)
    private String description; // Ex: Faible maîtrise

    @Column(nullable = false)
    private String recommandation; // Ex: Chapitre recommandé automatiquement

     @Column(nullable = false)
    @JsonProperty("seuilMin") // Force la lecture du champ JSON "seuilMin"
    private Double seuilMin;

    @Column(nullable = false)
    @JsonProperty("seuilMax") // Force la lecture du champ JSON "seuilMax"
    private Double seuilMax;

    @Column(nullable = false, length = 7)
    @JsonProperty("couleur") // Force la lecture du champ JSON "couleur"
    private String couleur;

    // Constructeurs
    public EchelleConnaissance() {}

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
