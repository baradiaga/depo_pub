package com.moscepa.entity;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "moscepa_fonctionnalites") // <-- Convention de nommage appliquée
public class Fonctionnalite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom; // Le label visible, ex: "Gestion des utilisateurs"

    @Column(nullable = false, unique = true)
    private String featureKey; // La clé unique, ex: "gestion_utilisateurs"

    @Column
    private String icon; // L'icône du menu principal

    // Utilisation de @OneToMany pour les sous-fonctionnalités pour plus de flexibilité
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "fonctionnalite_id") // Clé étrangère dans la table des sous-fonctionnalités
    private List<SousFonctionnalite> sousFonctionnalites = new ArrayList<>();

    // Getters et Setters...
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

    public String getFeatureKey() {
        return featureKey;
    }

    public void setFeatureKey(String featureKey) {
        this.featureKey = featureKey;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<SousFonctionnalite> getSousFonctionnalites() {
        return sousFonctionnalites;
    }

    public void setSousFonctionnalites(List<SousFonctionnalite> sousFonctionnalites) {
        this.sousFonctionnalites = sousFonctionnalites;
    }
}
