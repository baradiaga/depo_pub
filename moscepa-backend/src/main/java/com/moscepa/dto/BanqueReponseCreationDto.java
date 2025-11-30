// Fichier : src/main/java/com/moscepa/dto/BanqueReponseCreationDto.java

package com.moscepa.dto;

public class BanqueReponseCreationDto {

    private String texte;
    private Boolean correcte;

    // Getters et Setters
    public String getTexte() { return texte; }
    public void setTexte(String texte) { this.texte = texte; }

    public Boolean getCorrecte() { return correcte; }
    public void setCorrecte(Boolean correcte) { this.correcte = correcte; }
}
