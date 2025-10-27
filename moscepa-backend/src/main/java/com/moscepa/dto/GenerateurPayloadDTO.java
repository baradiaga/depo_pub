package com.moscepa.dto; // Assurez-vous que le package est correct

import java.util.List;

// Ce DTO correspond à l'interface 'ParametresGeneration' du front-end
public class GenerateurPayloadDTO {

    private String titre;
    private Integer duree;
    private int nombreQuestions;
    private List<Long> chapitresIds; // Renommé pour être plus clair

    // Getters et Setters
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public Integer getDuree() { return duree; }
    public void setDuree(Integer duree) { this.duree = duree; }
    public int getNombreQuestions() { return nombreQuestions; }
    public void setNombreQuestions(int nombreQuestions) { this.nombreQuestions = nombreQuestions; }
    public List<Long> getChapitresIds() { return chapitresIds; }
    public void setChapitresIds(List<Long> chapitresIds) { this.chapitresIds = chapitresIds; }
}
