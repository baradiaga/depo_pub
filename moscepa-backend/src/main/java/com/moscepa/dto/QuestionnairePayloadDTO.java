package com.moscepa.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public class QuestionnairePayloadDTO {

    @NotBlank(message = "Le titre ne peut pas être vide.")
    @Size(min = 3, max = 255, message = "Le titre doit contenir entre 3 et 255 caractères.")
    private String titre;

    @NotBlank(message = "La matière ne peut pas être vide.")
    private String matiere;

    @NotNull(message = "L'ID du chapitre est obligatoire.")
    @Positive(message = "L'ID du chapitre doit être un nombre positif.")
    private Long chapitreId;

    @Positive(message = "La durée doit être un nombre positif.")
    private int duree;

    private String description;

    @NotEmpty(message = "Le questionnaire doit contenir au moins une question.")
    @Valid
    private List<QuestionCreationDTO> questions;

    // --- Getters et Setters ---
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getMatiere() { return matiere; }
    public void setMatiere(String matiere) { this.matiere = matiere; }
    public Long getChapitreId() { return chapitreId; }
    public void setChapitreId(Long chapitreId) { this.chapitreId = chapitreId; }
    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<QuestionCreationDTO> getQuestions() { return questions; }
    public void setQuestions(List<QuestionCreationDTO> questions) { this.questions = questions; }
}
