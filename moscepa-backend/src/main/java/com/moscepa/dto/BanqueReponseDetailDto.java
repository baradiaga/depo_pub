// Fichier : src/main/java/com/moscepa/dto/BanqueReponseDetailDto.java

package com.moscepa.dto;

import com.moscepa.entity.BanqueReponse;

public class BanqueReponseDetailDto {

    private Long id;
    private String texte;
    private Boolean correcte;

    // Constructeur à partir de l'entité
    public BanqueReponseDetailDto(BanqueReponse reponse) {
        this.id = reponse.getId();
        this.texte = reponse.getTexte();
        this.correcte = reponse.getCorrecte();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTexte() { return texte; }
    public void setTexte(String texte) { this.texte = texte; }

    public Boolean getCorrecte() { return correcte; }
    public void setCorrecte(Boolean correcte) { this.correcte = correcte; }
}
