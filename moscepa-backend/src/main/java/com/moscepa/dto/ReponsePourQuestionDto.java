package com.moscepa.dto;

import com.moscepa.entity.Reponse;

public class ReponsePourQuestionDto {

    private Long id;
    private String texte;
    private boolean correcte;

    public ReponsePourQuestionDto() {}

    public ReponsePourQuestionDto(Reponse reponse) {
        this.id = reponse.getId();
        this.texte = reponse.getTexte();
        this.correcte = reponse.isCorrecte();
    }

    public Long getId() {
        return id;
    }

    public String getTexte() {
        return texte;
    }

    public boolean isCorrecte() {
        return correcte;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public void setCorrecte(boolean correcte) {
        this.correcte = correcte;
    }
}
