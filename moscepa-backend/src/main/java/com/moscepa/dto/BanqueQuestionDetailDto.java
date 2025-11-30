// Fichier : src/main/java/com/moscepa/dto/BanqueQuestionDetailDto.java

package com.moscepa.dto;

import com.moscepa.entity.BanqueQuestion;
import com.moscepa.entity.Difficulte;
import com.moscepa.entity.TypeQuestion;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;

public class BanqueQuestionDetailDto {

    private Long id;
    private String enonce;
    private TypeQuestion typeQuestion;
    private Integer points;
    private Difficulte difficulte;
    private Long chapitreId;
    private String chapitreNom;
    private String auteurNom;
    private LocalDateTime dateCreation;
    private Double noteQualite;
    private Integer nombreUtilisations;
    private List<BanqueReponseDetailDto> reponses;
    private Set<String> tags;

    // Constructeur à partir de l'entité
    public BanqueQuestionDetailDto(BanqueQuestion question) {
        this.id = question.getId();
        this.enonce = question.getEnonce();
        this.typeQuestion = question.getTypeQuestion();
        this.points = question.getPoints();
        this.difficulte = question.getDifficulte();
        this.chapitreId = question.getChapitre().getId();
        this.chapitreNom = question.getChapitre().getNom();
        this.auteurNom = question.getAuteur().getPrenom() + " " + question.getAuteur().getNom();
        this.dateCreation = question.getDateCreation();
        this.noteQualite = question.getNoteQualite();
        this.nombreUtilisations = question.getNombreUtilisations();
        this.reponses = question.getReponses().stream()
                .map(BanqueReponseDetailDto::new)
                .collect(Collectors.toList());
        this.tags = question.getTags().stream()
                .map(tag -> tag.getNom())
                .collect(Collectors.toSet());
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEnonce() { return enonce; }
    public void setEnonce(String enonce) { this.enonce = enonce; }

    public TypeQuestion getTypeQuestion() { return typeQuestion; }
    public void setTypeQuestion(TypeQuestion typeQuestion) { this.typeQuestion = typeQuestion; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public Difficulte getDifficulte() { return difficulte; }
    public void setDifficulte(Difficulte difficulte) { this.difficulte = difficulte; }

    public Long getChapitreId() { return chapitreId; }
    public void setChapitreId(Long chapitreId) { this.chapitreId = chapitreId; }

    public String getChapitreNom() { return chapitreNom; }
    public void setChapitreNom(String chapitreNom) { this.chapitreNom = chapitreNom; }

    public String getAuteurNom() { return auteurNom; }
    public void setAuteurNom(String auteurNom) { this.auteurNom = auteurNom; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public Double getNoteQualite() { return noteQualite; }
    public void setNoteQualite(Double noteQualite) { this.noteQualite = noteQualite; }

    public Integer getNombreUtilisations() { return nombreUtilisations; }
    public void setNombreUtilisations(Integer nombreUtilisations) { this.nombreUtilisations = nombreUtilisations; }

    public List<BanqueReponseDetailDto> getReponses() { return reponses; }
    public void setReponses(List<BanqueReponseDetailDto> reponses) { this.reponses = reponses; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }
}
