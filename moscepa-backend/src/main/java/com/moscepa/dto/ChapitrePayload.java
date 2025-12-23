package com.moscepa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class ChapitrePayload {

    @NotBlank(message = "Le nom de la matière est obligatoire")
    private String matiere;

    @NotBlank(message = "Le titre du chapitre est obligatoire")
    private String titre;

    @NotNull(message = "Le niveau est obligatoire")
    private Integer niveau;

    private String objectif;

    private List<SectionPayload> sections;

    // ========== AJOUTE CES 3 NOUVEAUX CHAMPS ==========
    @Size(max = 255, message = "Le type d'activité ne doit pas dépasser 255 caractères")
    private String typeActivite;

    @Size(max = 2000, message = "Les prérequis ne doivent pas dépasser 2000 caractères")
    private String prerequis;

    @Size(max = 255, message = "Le type d'évaluation ne doit pas dépasser 255 caractères")
    private String typeEvaluation;
    // ===================================================

    // Getters et Setters EXISTANTS
    public String getMatiere() { return matiere; }
    public void setMatiere(String matiere) { this.matiere = matiere; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public Integer getNiveau() { return niveau; }
    public void setNiveau(Integer niveau) { this.niveau = niveau; }
    public String getObjectif() { return objectif; }
    public void setObjectif(String objectif) { this.objectif = objectif; }
    public List<SectionPayload> getSections() { return sections; }
    public void setSections(List<SectionPayload> sections) { this.sections = sections; }

    // ========== AJOUTE CES GETTERS ET SETTERS ==========
    public String getTypeActivite() { return typeActivite; }
    public void setTypeActivite(String typeActivite) { this.typeActivite = typeActivite; }
    
    public String getPrerequis() { return prerequis; }
    public void setPrerequis(String prerequis) { this.prerequis = prerequis; }
    
    public String getTypeEvaluation() { return typeEvaluation; }
    public void setTypeEvaluation(String typeEvaluation) { this.typeEvaluation = typeEvaluation; }
    // ===================================================
}