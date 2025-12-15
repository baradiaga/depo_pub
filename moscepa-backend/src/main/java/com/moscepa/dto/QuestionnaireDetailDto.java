package com.moscepa.dto;

import com.moscepa.entity.Questionnaire;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionnaireDetailDto {

    private Long id;
    private String titre;
    private String description;
    private int duree;
    private Long chapitreId; // ⚡ CHAMP CRITIQUE
    private String nomChapitre; // Optionnel: pour affichage
    private List<QuestionDto> questions;

    // --- Constructeur vide ---
    public QuestionnaireDetailDto() {
        // Pour la désérialisation JSON
    }

    // --- Constructeur depuis l'entité ---
    public QuestionnaireDetailDto(Questionnaire q) {
        this.id = q.getId();
        this.titre = q.getTitre();
        this.description = q.getDescription();
        this.duree = q.getDuree();
        
        // ⚡ MAPPING CRITIQUE DU CHAPITRE
        if (q.getChapitre() != null) {
            this.chapitreId = q.getChapitre().getId();
            this.nomChapitre = q.getChapitre().getNom();
        }
        
        // Mapper les questions
        if (q.getQuestions() != null) {
            this.questions = q.getQuestions().stream()
                    .map(QuestionDto::new)
                    .collect(Collectors.toList());
        }
    }

    // --- Convertir DTO en entité (MODIFIÉ) ---
    public Questionnaire toEntity() {
        Questionnaire q = new Questionnaire();
        q.setId(this.id);
        q.setTitre(this.titre);
        q.setDescription(this.description);
        q.setDuree(this.duree);
        
        // ⚡ IMPORTANT: NE PAS DÉFINIR LE CHAPITRE ICI
        // Le chapitre sera défini dans le service avec le repository
        // Pour éviter les problèmes de session Hibernate
        
        return q;
    }

    // --- Getters / Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }

    // ⚡ GETTER/SETTER POUR CHAPITREID (ESSENTIEL)
    public Long getChapitreId() { return chapitreId; }
    public void setChapitreId(Long chapitreId) { this.chapitreId = chapitreId; }

    public String getNomChapitre() { return nomChapitre; }
    public void setNomChapitre(String nomChapitre) { this.nomChapitre = nomChapitre; }

    public List<QuestionDto> getQuestions() { return questions; }
    public void setQuestions(List<QuestionDto> questions) { this.questions = questions; }
    
    // --- Méthode toString pour debug ---
    @Override
    public String toString() {
        return "QuestionnaireDetailDto{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", duree=" + duree +
                ", chapitreId=" + chapitreId +
                ", nomChapitre='" + nomChapitre + '\'' +
                ", questions=" + (questions != null ? questions.size() : 0) +
                '}';
    }
}