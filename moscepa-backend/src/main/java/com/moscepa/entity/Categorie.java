package com.moscepa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "moscepa_categories")
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @ManyToOne
    @JoinColumn(name = "echelle_id", nullable = false)
    private EchelleConnaissance echelleConnaissance;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public EchelleConnaissance getEchelleConnaissance() { return echelleConnaissance; }
    public void setEchelleConnaissance(EchelleConnaissance echelleConnaissance) { this.echelleConnaissance = echelleConnaissance; }
}
