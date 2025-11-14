// Fichier : src/main/java/com/moscepa/dto/ReponsePourQuestionDto.java (Nouveau Fichier)

package com.moscepa.dto;

import com.moscepa.entity.Reponse;

/**
 * DTO "sûr" pour représenter une option de réponse dans une question.
 * Il ne contient PAS la propriété 'correcte' pour éviter la triche.
 */
public class ReponsePourQuestionDto {
    private Long id;
    private String texte;

    // Constructeur par défaut
    public ReponsePourQuestionDto() {}

    // Constructeur pour la conversion
    public ReponsePourQuestionDto(Reponse reponse) {
        this.id = reponse.getId();
        this.texte = reponse.getTexte();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTexte() { return texte; }
    public void setTexte(String texte) { this.texte = texte; }
}
