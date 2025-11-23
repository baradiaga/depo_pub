// Fichier : src/main/java/com/moscepa/dto/CreateTestRequest.java

package com.moscepa.dto;

import java.util.List;

public class CreateTestRequest {
    private String titre;
    private Long chapitreId;
    private List<Long> questionIds;

    // Getters et Setters (Indispensables pour que Spring lise le JSON)
    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Long getChapitreId() {
        return chapitreId;
    }

    public void setChapitreId(Long chapitreId) {
        this.chapitreId = chapitreId;
    }

    public List<Long> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<Long> questionIds) {
        this.questionIds = questionIds;
    }
}
