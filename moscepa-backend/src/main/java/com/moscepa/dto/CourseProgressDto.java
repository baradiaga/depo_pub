package com.moscepa.dto;

import java.util.List;

public class CourseProgressDto {

    private Long courseId;
    private String courseCode;
    private String courseName;
    private int testsPasses;
    private String statutRecommandation;
    
    // NOUVEAU : Liste pour stocker chaque note de test individuellement
    private List<Double> notesDetaillees; 

    public CourseProgressDto() {}

    // Getters et Setters existants
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getTestsPasses() { return testsPasses; }
    public void setTestsPasses(int testsPasses) { this.testsPasses = testsPasses; }

    public String getStatutRecommandation() { return statutRecommandation; }
    public void setStatutRecommandation(String statutRecommandation) { this.statutRecommandation = statutRecommandation; }

    // Nouveaux Getters/Setters pour les notes
    public List<Double> getNotesDetaillees() { return notesDetaillees; }
    public void setNotesDetaillees(List<Double> notesDetaillees) { this.notesDetaillees = notesDetaillees; }
}
