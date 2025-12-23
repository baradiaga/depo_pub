package com.moscepa.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class GenerationRequestDto {
    
    @NotNull(message = "La liste des thèmes est obligatoire")
    @NotEmpty(message = "Au moins un thème doit être sélectionné")
    private List<String> themes;
    
    @Min(value = 1, message = "Le nombre de questions doit être d'au moins 1")
    @Max(value = 100, message = "Le nombre de questions ne peut dépasser 100")
    private int nombreQuestions;
    
    @NotNull(message = "Le niveau de difficulté est obligatoire")
    private String niveau;
    
    public GenerationRequestDto() {
    }
    
    public GenerationRequestDto(List<String> themes, int nombreQuestions, String niveau) {
        this.themes = themes;
        this.nombreQuestions = nombreQuestions;
        this.niveau = niveau;
    }
    
    public List<String> getThemes() {
        return themes;
    }
    
    public void setThemes(List<String> themes) {
        this.themes = themes;
    }
    
    public int getNombreQuestions() {
        return nombreQuestions;
    }
    
    public void setNombreQuestions(int nombreQuestions) {
        this.nombreQuestions = nombreQuestions;
    }
    
    public String getNiveau() {
        return niveau;
    }
    
    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }
    
    @Override
    public String toString() {
        return "GenerationRequestDto{" +
                "themes=" + themes +
                ", nombreQuestions=" + nombreQuestions +
                ", niveau='" + niveau + '\'' +
                '}';
    }
}