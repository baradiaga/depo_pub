package com.moscepa.dto;

import java.util.List;

public class StudentJourneyDto {

    private Long studentId;
    private String nomComplet;
    private String email;
    private String formationActuelle;
    private String niveauEtude;
    private double moyenneGeneraleTests;
    private int testsPasses;
    private List<CourseProgressDto> progressionParCours;

    // Nouveau champ ajouté pour le type de parcours
    private String parcoursType;

    // Constructeur par défaut
    public StudentJourneyDto() {}

    // Getters et Setters existants
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

    public double getMoyenneGeneraleTests() { return moyenneGeneraleTests; }
    public void setMoyenneGeneraleTests(double moyenneGeneraleTests) { this.moyenneGeneraleTests = moyenneGeneraleTests; }

    public int getTestsPasses() { return testsPasses; }
    public void setTestsPasses(int testsPasses) { this.testsPasses = testsPasses; }

    public List<CourseProgressDto> getProgressionParCours() { return progressionParCours; }
    public void setProgressionParCours(List<CourseProgressDto> progressionParCours) { this.progressionParCours = progressionParCours; }

    // Getters et Setters pour le nouveau champ parcoursType
    public String getParcoursType() { return parcoursType; }
    public void setParcoursType(String parcoursType) { this.parcoursType = parcoursType; }
}
