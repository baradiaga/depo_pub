package com.moscepa.dto;

import java.util.List;

// Ce DTO correspond à l'interface 'ParametresGeneration' du front-end
public class GenerateurPayload {

    private String titre;
    private Integer duree;
    private int nombreQuestions;

    // Un seul chapitre (utilisé par le service)
    private Long chapitreId;

    // Plusieurs chapitres (optionnel, utilisé si besoin côté front)
    private List<Long> chapitresIds;

    // --- Getters et Setters ---
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public Integer getDuree() { return duree; }
    public void setDuree(Integer duree) { this.duree = duree; }

    public int getNombreQuestions() { return nombreQuestions; }
    public void setNombreQuestions(int nombreQuestions) { this.nombreQuestions = nombreQuestions; }

    public Long getChapitreId() { return chapitreId; }
    public void setChapitreId(Long chapitreId) { this.chapitreId = chapitreId; }

    public List<Long> getChapitresIds() { return chapitresIds; }
    public void setChapitresIds(List<Long> chapitresIds) { this.chapitresIds = chapitresIds; }
}
