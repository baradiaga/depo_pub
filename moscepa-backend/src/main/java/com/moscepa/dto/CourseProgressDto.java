// Fichier : src/main/java/com/moscepa/dto/CourseProgressDto.java

package com.moscepa.dto;

public class CourseProgressDto {

    private Long courseId;
    private String courseCode;
    private String courseName;
    private double scoreMoyen; // Score moyen aux tests de ce cours
    private int testsPasses;
    private String statutRecommandation; // Basé sur l'EchelleConnaissance

    // Constructeur par défaut et Getters/Setters
    public CourseProgressDto() {}

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public double getScoreMoyen() { return scoreMoyen; }
    public void setScoreMoyen(double scoreMoyen) { this.scoreMoyen = scoreMoyen; }

    public int getTestsPasses() { return testsPasses; }
    public void setTestsPasses(int testsPasses) { this.testsPasses = testsPasses; }

    public String getStatutRecommandation() { return statutRecommandation; }
    public void setStatutRecommandation(String statutRecommandation) { this.statutRecommandation = statutRecommandation; }
}
