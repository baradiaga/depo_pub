package com.moscepa.dto;

import com.moscepa.entity.CompetenceDetail;
import com.moscepa.entity.NiveauAcquisition;

public class CompetenceDetailDto {
    private String libelle;
    private String niveauAcquisition;
    private String indicateursEvaluation;

    // Constructeur pour la création
    public CompetenceDetailDto() {}

    // Constructeur pour le mappage depuis l'Entité
    public CompetenceDetailDto(CompetenceDetail entity) {
        this.libelle = entity.getLibelle();
        this.niveauAcquisition = entity.getNiveauAcquisition().name();
        this.indicateursEvaluation = entity.getIndicateursEvaluation();
    }

    // --- Getters et Setters ---
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getNiveauAcquisition() { return niveauAcquisition; }
    public void setNiveauAcquisition(String niveauAcquisition) { this.niveauAcquisition = niveauAcquisition; }
    public String getIndicateursEvaluation() { return indicateursEvaluation; }
    public void setIndicateursEvaluation(String indicateursEvaluation) { this.indicateursEvaluation = indicateursEvaluation; }
}
