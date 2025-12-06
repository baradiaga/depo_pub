package com.moscepa.dto;

import java.time.LocalDateTime;

public class QuestionnaireDetailDto {

    private Long id;
    private String titre;
    private Integer duree;
    private String description;
    private String nomChapitre;
    private String nomMatiere;
    private Long chapitreId;
    private Long matiereId; // <-- ajouté
    private int nombreQuestions;
    private LocalDateTime dateCreation;

    // Constructeur
    public QuestionnaireDetailDto(Long id, String titre, String description, LocalDateTime dateCreation,
                              Long chapitreId, String nomChapitre, String nomMatiere, int nombreQuestions, Integer duree) {
    this.id = id;
    this.titre = titre;
    this.description = description;
    this.dateCreation = dateCreation;
    this.chapitreId = chapitreId;
    this.nomChapitre = nomChapitre;
    this.nomMatiere = nomMatiere;
    this.nombreQuestions = nombreQuestions;
    this.duree = duree;
}


    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public Integer getDuree() { return duree; }
    public void setDuree(Integer duree) { this.duree = duree; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNomChapitre() { return nomChapitre; }
    public void setNomChapitre(String nomChapitre) { this.nomChapitre = nomChapitre; }

    public String getNomMatiere() { return nomMatiere; }
    public void setNomMatiere(String nomMatiere) { this.nomMatiere = nomMatiere; }

    public Long getChapitreId() { return chapitreId; }
    public void setChapitreId(Long chapitreId) { this.chapitreId = chapitreId; }

    public Long getMatiereId() { return matiereId; }
    public void setMatiereId(Long matiereId) { this.matiereId = matiereId; }

    public int getNombreQuestions() { return nombreQuestions; }
    public void setNombreQuestions(int nombreQuestions) { this.nombreQuestions = nombreQuestions; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}
