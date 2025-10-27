package com.moscepa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// NOUVEAU FICHIER
public class TestDto {

    private Long id;

    @NotBlank(message = "Le titre du test ne peut pas être vide.")
    private String titre;

    @NotNull(message = "L'ID du chapitre est obligatoire.")
    @Positive(message = "L'ID du chapitre doit être un nombre positif.")
    private Long chapitreId;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public Long getChapitreId() { return chapitreId; }
    public void setChapitreId(Long chapitreId) { this.chapitreId = chapitreId; }
}
