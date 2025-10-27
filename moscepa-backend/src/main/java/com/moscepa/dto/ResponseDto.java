package com.moscepa.dto;

import com.moscepa.entity.Reponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ResponseDto {

    private Long id;

    @NotBlank(message = "Le texte de la réponse ne peut pas être vide.")
    private String texte;

    @NotNull(message = "La propriété 'correcte' (true/false) est obligatoire.")
    private boolean correcte;

    public ResponseDto() {}

    public ResponseDto(Reponse reponse) {
        this.id = reponse.getId();
        this.texte = reponse.getTexte();
        this.correcte = reponse.isCorrecte();
    }

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTexte() { return texte; }
    public void setTexte(String texte) { this.texte = texte; }
    public boolean isCorrecte() { return correcte; }
    public void setCorrecte(boolean correcte) { this.correcte = correcte; }
}
