package com.moscepa.dto;

// --- CORRECTIONS ---
// 1. Importation de la classe renommée ResponseDto
import com.moscepa.dto.ResponseDto; 
import com.moscepa.entity.Question;
import com.moscepa.entity.TypeQuestion;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO pour afficher les informations détaillées d'une question.
 * Utilisé pour transformer une entité Question en un objet JSON à envoyer au client.
 */
public class QuestionDto {

    private Long id;
    private String enonce;
    private TypeQuestion type;
    private double points;
    private List<ResponseDto> reponses; // CORRIGÉ : Utilise ResponseDto

    // Constructeur vide
    public QuestionDto() {
    }

    // Constructeur de conversion (très pratique)
    public QuestionDto(Question question) {
        this.id = question.getId();
        this.enonce = question.getEnonce();
        // 2. CORRECTION : Utilisation du bon getter
        this.type = question.getTypeQuestion(); 
        this.points = question.getPoints();
        // Conversion de la liste d'entités Reponse en liste de ResponseDto
        if (question.getReponses() != null) {
            this.reponses = question.getReponses().stream()
                                  .map(ResponseDto::new) // Utilise le constructeur de ResponseDto
                                  .collect(Collectors.toList());
        }
    }

    // --- Getters et Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnonce() {
        return enonce;
    }

    public void setEnonce(String enonce) {
        this.enonce = enonce;
    }

    public TypeQuestion getType() {
        return type;
    }

    public void setType(TypeQuestion type) {
        this.type = type;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    // CORRIGÉ : Utilise ResponseDto
    public List<ResponseDto> getReponses() {
        return reponses;
    }

    // CORRIGÉ : Utilise ResponseDto
    public void setReponses(List<ResponseDto> reponses) {
        this.reponses = reponses;
    }
}
