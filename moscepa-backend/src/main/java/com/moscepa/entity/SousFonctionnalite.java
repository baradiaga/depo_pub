package com.moscepa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "moscepa_sous_fonctionnalites") // <-- Convention de nommage appliquée
public class SousFonctionnalite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String label; // Le label visible, ex: "Profil utilisateur"

    @Column(nullable = false, unique = true)
    private String featureKey; // La clé unique, ex: "profil_utilisateur"

    @Column(nullable = false)
    private String route; // La route Angular, ex: "/app/profil"

    // Getters et Setters...
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFeatureKey() {
        return featureKey;
    }

    public void setFeatureKey(String featureKey) {
        this.featureKey = featureKey;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }
}
