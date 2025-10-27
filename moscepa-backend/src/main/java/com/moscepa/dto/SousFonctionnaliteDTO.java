package com.moscepa.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class SousFonctionnaliteDTO {
    private Long id;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String label;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String featureKey;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String route;

    // Getters et Setters
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
