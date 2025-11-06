package com.moscepa.dto;

import jakarta.validation.constraints.NotBlank;

public class SectionPayload {

    @NotBlank(message = "Le titre de la section est obligatoire")
    private String titre;

    // Getters et Setters
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
}
