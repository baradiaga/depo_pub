package com.moscepa.dto;

import java.time.LocalDateTime;

public class ChapitreProgressDto {
    private Long chapitreId;
    private String chapitreNom;
    private Integer ordre;
    private Double scoreMoyen;
    private String parcoursType; // "RECOMMANDE", "CHOISI", "MIXTE"
    private LocalDateTime dateDernierTest;
    private int nombreTests;
    
    public ChapitreProgressDto() {}
    
    public ChapitreProgressDto(Long chapitreId, String chapitreNom, Integer ordre, 
                              Double scoreMoyen, String parcoursType, 
                              LocalDateTime dateDernierTest, int nombreTests) {
        this.chapitreId = chapitreId;
        this.chapitreNom = chapitreNom;
        this.ordre = ordre;
        this.scoreMoyen = scoreMoyen;
        this.parcoursType = parcoursType;
        this.dateDernierTest = dateDernierTest;
        this.nombreTests = nombreTests;
    }
    
    // Getters and Setters
    public Long getChapitreId() { return chapitreId; }
    public void setChapitreId(Long chapitreId) { this.chapitreId = chapitreId; }
    
    public String getChapitreNom() { return chapitreNom; }
    public void setChapitreNom(String chapitreNom) { this.chapitreNom = chapitreNom; }
    
    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }
    
    public Double getScoreMoyen() { return scoreMoyen; }
    public void setScoreMoyen(Double scoreMoyen) { this.scoreMoyen = scoreMoyen; }
    
    public String getParcoursType() { return parcoursType; }
    public void setParcoursType(String parcoursType) { this.parcoursType = parcoursType; }
    
    public LocalDateTime getDateDernierTest() { return dateDernierTest; }
    public void setDateDernierTest(LocalDateTime dateDernierTest) { this.dateDernierTest = dateDernierTest; }
    
    public int getNombreTests() { return nombreTests; }
    public void setNombreTests(int nombreTests) { this.nombreTests = nombreTests; }
}