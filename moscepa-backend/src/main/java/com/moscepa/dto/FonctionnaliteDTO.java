package com.moscepa.dto;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

public class FonctionnaliteDTO {
    private Long id;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String nom;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String featureKey;
    private String icon;
    private List<SousFonctionnaliteDTO> sousFonctionnalites;

    // Getters et Setters
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

    public List<SousFonctionnaliteDTO> getSousFonctionnalites() {
        return sousFonctionnalites;
    }

    public void setSousFonctionnalites(List<SousFonctionnaliteDTO> sousFonctionnalites) {
        this.sousFonctionnalites = sousFonctionnalites;
    }
}
