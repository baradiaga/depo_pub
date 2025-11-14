

package com.moscepa.dto;
import com.moscepa.entity.TypeQuestion;
import java.util.List;


public class QuestionDiagnosticDto {

    private Long id;
    private String enonce;
    private Long chapitreId; 
    private TypeQuestion typeQuestion;
    private List<ReponsePourQuestionDto> options; 

    // --- Getters et Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnonce() {
        return enonce;
    }

    public void setEnonce(String enonce) {
        this.enonce = enonce;
    }

    public Long getChapitreId() {
        return chapitreId;
    }

    public void setChapitreId(Long chapitreId) {
        this.chapitreId = chapitreId;
    }
    public TypeQuestion getTypeQuestion() { return typeQuestion; }
    public void setTypeQuestion(TypeQuestion typeQuestion) { this.typeQuestion = typeQuestion; }

    public List<ReponsePourQuestionDto> getOptions() {
        return options;
    }

    public void setOptions(List<ReponsePourQuestionDto> options) {
        this.options = options;
    }
}
