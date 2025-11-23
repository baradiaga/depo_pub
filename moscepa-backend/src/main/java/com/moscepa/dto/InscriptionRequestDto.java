// Fichier : src/main/java/com/moscepa/dto/InscriptionRequestDto.java

package com.moscepa.dto;

public class InscriptionRequestDto {
    private Long etudiantId;
    private Long ecId;

    // Getters et Setters
    public Long getEtudiantId() { return etudiantId; }
    public void setEtudiantId(Long etudiantId) { this.etudiantId = etudiantId; }
    public Long getEcId() { return ecId; }
    public void setEcId(Long ecId) { this.ecId = ecId; }
}
