package com.moscepa.dto;

public class TestDetailDto {
    private Long id;
    private String titre;
    private Long chapitreId;
    private String chapitreTitre;
    private String matiere;

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public Long getChapitreId() { return chapitreId; }
    public void setChapitreId(Long chapitreId) { this.chapitreId = chapitreId; }
    public String getChapitreTitre() { return chapitreTitre; }
    public void setChapitreTitre(String chapitreTitre) { this.chapitreTitre = chapitreTitre; }
    public String getMatiere() { return matiere; }
    public void setMatiere(String matiere) { this.matiere = matiere; }
    
}
