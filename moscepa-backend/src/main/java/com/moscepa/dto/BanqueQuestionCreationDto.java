// Fichier : src/main/java/com/moscepa/dto/BanqueQuestionCreationDto.java

package com.moscepa.dto;

import com.moscepa.entity.Difficulte;
import com.moscepa.entity.TypeQuestion;
import java.util.List;
import java.util.Set;

public class BanqueQuestionCreationDto {

    private String enonce;
    private TypeQuestion typeQuestion;
    private Integer points;
    private Difficulte difficulte;
    private Long chapitreId;
    private List<BanqueReponseCreationDto> reponses;
    private Set<String> tags; // Noms des tags

    // Getters et Setters
    public String getEnonce() { return enonce; }
    public void setEnonce(String enonce) { this.enonce = enonce; }

    public TypeQuestion getTypeQuestion() { return typeQuestion; }
    public void setTypeQuestion(TypeQuestion typeQuestion) { this.typeQuestion = typeQuestion; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public Difficulte getDifficulte() { return difficulte; }
    public void setDifficulte(Difficulte difficulte) { this.difficulte = difficulte; }

    public Long getChapitreId() { return chapitreId; }
    public void setChapitreId(Long chapitreId) { this.chapitreId = chapitreId; }

    public List<BanqueReponseCreationDto> getReponses() { return reponses; }
    public void setReponses(List<BanqueReponseCreationDto> reponses) { this.reponses = reponses; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }
}
