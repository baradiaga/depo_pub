// ChapitreProgressFrontDto.java
package com.moscepa.dto;

import java.time.LocalDateTime;

public class ChapitreProgressFrontDto {
    private Long chapitreId;
    private String chapitreNom;
    private Integer ordre;
    private Double scoreMoyen;
    private String parcoursType;
    private LocalDateTime dateDernierTest;
    private Integer nombreTests;
    
    // Constructeurs
    public ChapitreProgressFrontDto() {}
    
    public ChapitreProgressFrontDto(Long chapitreId, String chapitreNom, Integer ordre, 
                                   Double scoreMoyen, String parcoursType, 
                                   LocalDateTime dateDernierTest, Integer nombreTests) {
        this.chapitreId = chapitreId;
        this.chapitreNom = chapitreNom;
        this.ordre = ordre;
        this.scoreMoyen = scoreMoyen;
        this.parcoursType = parcoursType;
        this.dateDernierTest = dateDernierTest;
        this.nombreTests = nombreTests;
    }
    
    // Getters et Setters
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
    
    public Integer getNombreTests() { return nombreTests; }
    public void setNombreTests(Integer nombreTests) { this.nombreTests = nombreTests; }
}