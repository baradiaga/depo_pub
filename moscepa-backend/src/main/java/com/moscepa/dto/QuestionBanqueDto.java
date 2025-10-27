package com.moscepa.dto; // Assurez-vous que le package est correct

import com.moscepa.entity.TypeQuestion;
import java.util.List;
import java.util.stream.Collectors;

// DTO pour représenter une question dans la banque
public class QuestionBanqueDto {

    private Long id;
    private String enonce;
    private TypeQuestion typeQuestion;
    private List<ReponseDto> reponses; // Nous aurons aussi besoin d'un DTO pour les réponses

    // Constructeur, Getters et Setters

    public QuestionBanqueDto() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEnonce() { return enonce; }
    public void setEnonce(String enonce) { this.enonce = enonce; }
    public TypeQuestion getTypeQuestion() { return typeQuestion; }
    public void setTypeQuestion(TypeQuestion typeQuestion) { this.typeQuestion = typeQuestion; }
    public List<ReponseDto> getReponses() { return reponses; }
    public void setReponses(List<ReponseDto> reponses) { this.reponses = reponses; }

    // DTO interne pour les réponses, pour ne pas exposer l'entité Reponse
    public static class ReponseDto {
        private Long id;
        private String texte;
        private boolean correcte;

        // Getters et Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTexte() { return texte; }
        public void setTexte(String texte) { this.texte = texte; }
        public boolean isCorrecte() { return correcte; }
        public void setCorrecte(boolean correcte) { this.correcte = correcte; }
    }
}