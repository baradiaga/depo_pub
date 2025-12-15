package com.moscepa.dto;

public class InscriptionValidationRequest {
    private Long inscriptionId;
    private String statut; // VALIDE ou REJETE

    // Getters et Setters
    public Long getInscriptionId() {
        return inscriptionId;
    }

    public void setInscriptionId(Long inscriptionId) {
        this.inscriptionId = inscriptionId;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}
