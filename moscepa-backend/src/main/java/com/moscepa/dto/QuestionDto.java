// Fichier : src/main/java/com/moscepa/dto/QuestionDto.java (Version 100% corrigée)

package com.moscepa.dto;

import com.moscepa.entity.Question;
import com.moscepa.entity.Questionnaire;
import com.moscepa.entity.TypeQuestion;

import java.util.List;
import java.util.stream.Collectors;

public class QuestionDto {

    private Long id;
    private String enonce;
    private TypeQuestion type;
    private double points;
    private List<ReponsePourQuestionDto> reponses;
    private Integer dureeTest; // Durée totale du test en minutes

    public QuestionDto() {}

    public QuestionDto(Question question) {
        this.id = question.getId();
        this.enonce = question.getEnonce();
        this.type = question.getTypeQuestion();
        this.points = question.getPoints();
        
        if (question.getReponses() != null) {
            this.reponses = question.getReponses().stream()
                                  .map(ReponsePourQuestionDto::new)
                                  .collect(Collectors.toList());
        }

        // ====================================================================
        // === LOGIQUE CORRIGÉE POUR ÉVITER L'ERREUR DE COMPILATION         ===
        // ====================================================================
        Questionnaire questionnaire = question.getQuestionnaire();
        // On vérifie simplement si le questionnaire parent existe.
        if (questionnaire != null) {
            // Si le champ 'duree' est un type primitif (int), il ne peut pas être null.
            // S'il est de type wrapper (Integer), la vérification est implicite ici.
            this.dureeTest = questionnaire.getDuree();
        } else {
            // Valeur par défaut si la question n'est liée à aucun questionnaire (cas improbable).
            this.dureeTest = 0; 
        }
    }

    // --- Getters et Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEnonce() { return enonce; }
    public void setEnonce(String enonce) { this.enonce = enonce; }
    public TypeQuestion getType() { return type; }
    public void setType(TypeQuestion type) { this.type = type; }
    public double getPoints() { return points; }
    public void setPoints(double points) { this.points = points; }
    public List<ReponsePourQuestionDto> getReponses() { return reponses; }
    public void setReponses(List<ReponsePourQuestionDto> reponses) { this.reponses = reponses; }

    // --- Getter et Setter pour la durée ---
    public Integer getDureeTest() { return dureeTest; }
    public void setDureeTest(Integer dureeTest) { this.dureeTest = dureeTest; }
}
