package com.moscepa.dto;

import com.moscepa.entity.Question;
import com.moscepa.entity.TypeQuestion;

import java.util.List;
import java.util.stream.Collectors;

public class QuestionDto {

    private Long id;
    private String enonce;
    private TypeQuestion type;
    private double points;
    private List<ReponsePourQuestionDto> reponses;

    public QuestionDto() {}

    // Constructeur à partir de l'entité Question
    public QuestionDto(Question question) {
        this.id = question.getId();
        this.enonce = question.getEnonce();
        this.type = question.getTypeQuestion();
        this.points = question.getPoints();

        if (question.getReponses() != null) {
            this.reponses = question.getReponses()
                                    .stream()
                                    .map(ReponsePourQuestionDto::new)
                                    .collect(Collectors.toList());
        }
        // On **évite la référence au questionnaire parent** pour ne pas créer de boucle infinie
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
}
