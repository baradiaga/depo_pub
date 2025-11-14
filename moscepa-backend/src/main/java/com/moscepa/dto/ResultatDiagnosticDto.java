// Fichier : src/main/java/com/moscepa/dto/ResultatDiagnosticDto.java
package com.moscepa.dto;

import java.util.List;
import java.util.Map;

public class ResultatDiagnosticDto {
    private double scoreGlobal;
    private int totalQuestions;
    private int bonnesReponses;
    private String message;
    private Map<String, Double> scoreParChapitre;
    private List<ChapitreRecommandationDto> chapitresAReviser;

    // --- Getters et Setters ---
    public double getScoreGlobal() { return scoreGlobal; }
    public void setScoreGlobal(double scoreGlobal) { this.scoreGlobal = scoreGlobal; }
    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public int getBonnesReponses() { return bonnesReponses; }
    public void setBonnesReponses(int bonnesReponses) { this.bonnesReponses = bonnesReponses; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Map<String, Double> getScoreParChapitre() { return scoreParChapitre; }
    public void setScoreParChapitre(Map<String, Double> scoreParChapitre) { this.scoreParChapitre = scoreParChapitre; }
    public List<ChapitreRecommandationDto> getChapitresAReviser() { return chapitresAReviser; }
    public void setChapitresAReviser(List<ChapitreRecommandationDto> chapitresAReviser) { this.chapitresAReviser = chapitresAReviser; }
}
