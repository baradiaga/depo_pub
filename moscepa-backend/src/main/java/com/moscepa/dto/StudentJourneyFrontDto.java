// StudentJourneyFrontDto.java
package com.moscepa.dto;

import com.moscepa.dto.CourseProgressDto;
import java.util.List;

public class StudentJourneyFrontDto {
    private Long studentId;
    private String nomComplet;
    private String email;
    private String formationActuelle;
    private String niveauEtude;
    private Double moyenneGeneraleTests;
    private Integer testsPasses;
    private String parcoursType;
    private List<CourseProgressDto> progressionParCours;
    
    // Constructeurs
    public StudentJourneyFrontDto() {}
    
    // Getters et setters pour TOUTES les propriétés
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public String getNomComplet() { return nomComplet; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFormationActuelle() { return formationActuelle; }
    public void setFormationActuelle(String formationActuelle) { this.formationActuelle = formationActuelle; }
    
    public String getNiveauEtude() { return niveauEtude; }
    public void setNiveauEtude(String niveauEtude) { this.niveauEtude = niveauEtude; }
    
    public Double getMoyenneGeneraleTests() { return moyenneGeneraleTests; }
    public void setMoyenneGeneraleTests(Double moyenneGeneraleTests) { this.moyenneGeneraleTests = moyenneGeneraleTests; }
    
    public Integer getTestsPasses() { return testsPasses; }
    public void setTestsPasses(Integer testsPasses) { this.testsPasses = testsPasses; }
    
    public String getParcoursType() { return parcoursType; }
    public void setParcoursType(String parcoursType) { this.parcoursType = parcoursType; }
    
    public List<CourseProgressDto> getProgressionParCours() { return progressionParCours; }
    public void setProgressionParCours(List<CourseProgressDto> progressionParCours) { 
        this.progressionParCours = progressionParCours; 
    }
}