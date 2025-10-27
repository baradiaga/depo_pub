package com.moscepa.dto;

import com.moscepa.dto.ResponseDto;
import com.moscepa.entity.TypeQuestion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public class QuestionCreationDTO {

    @NotBlank(message = "L'énoncé de la question ne peut pas être vide.")
    @Size(min = 5, message = "L'énoncé de la question doit contenir au moins 5 caractères.")
    private String enonce;

    @NotNull(message = "Le type de la question est obligatoire.")
    private TypeQuestion type;

    @NotNull(message = "Le nombre de points est obligatoire.")
    @Positive(message = "Le nombre de points doit être positif.")
    private Double points;

    @Valid
    private List<ResponseDto> reponses;
      // Ce champ transportera la bonne réponse pour les types TEXTE_LIBRE et VRAI_FAUX.
    private String reponseCorrecteTexte;
    // --- Getters et Setters ---
    public String getEnonce() { return enonce; }
    public void setEnonce(String enonce) { this.enonce = enonce; }
    public TypeQuestion getType() { return type; }
    public void setType(TypeQuestion type) { this.type = type; }
    public Double getPoints() { return points; }
    public void setPoints(Double points) { this.points = points; }
    public List<ResponseDto> getReponses() { return reponses; }
    public void setReponses(List<ResponseDto> reponses) { this.reponses = reponses; }
    public String getReponseCorrecteTexte() { return reponseCorrecteTexte; }
    public void setReponseCorrecteTexte(String reponseCorrecteTexte) { this.reponseCorrecteTexte = reponseCorrecteTexte; }
}
