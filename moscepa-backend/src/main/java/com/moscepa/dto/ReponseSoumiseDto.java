// Fichier : src/main/java/com/moscepa/dto/ReponseSoumiseDto.java
package com.moscepa.dto;

public class ReponseSoumiseDto {
    private Long questionId;
    private Object reponse;

    // Getters et Setters
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public Object getReponse() { return reponse; }
    public void setReponse(Object reponse) { this.reponse = reponse; }
}
