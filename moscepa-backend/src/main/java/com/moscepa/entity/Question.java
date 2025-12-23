package com.moscepa.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "moscepa_questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String enonce;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_question", nullable = false)
    private TypeQuestion typeQuestion;

    @Column(nullable = false)
    private double points = 1.0;

    @Column(name = "reponse_correcte_texte")
    private String reponseCorrecteTexte;

    @Column(name = "theme")
    private String theme;

    @Column(name = "niveau")
    private String niveau;

    // ====================================================================
    // Réponses associées à la question
    // ====================================================================
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    @JsonManagedReference("question-reponses")  // Gère les réponses côté parent
    private List<Reponse> reponses = new ArrayList<>();

    // ====================================================================
    // Questionnaire associé (si la question appartient à un questionnaire)
    // ====================================================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionnaire_id")
    @JsonBackReference("questionnaire-questions")  // Même nom que dans Questionnaire
    private Questionnaire questionnaire;

    // ====================================================================
    // Chapitre associé
    // ====================================================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapitre_id")
    @JsonBackReference("chapitre-questions")  // Si Chapitre a une liste de questions
    private Chapitre chapitre;

    // ====================================================================
    // Tests associés via ManyToMany
    // ====================================================================
    @ManyToMany(mappedBy = "questions")
    @JsonIgnore  // Pour ManyToMany, souvent mieux d'ignorer complètement
    private List<Test> tests = new ArrayList<>();

    // ====================================================================
    // Méthodes utilitaires pour gérer les réponses
    // ====================================================================
    public void addReponse(Reponse reponse) {
        this.reponses.add(reponse);
        reponse.setQuestion(this);
    }

    public void removeReponse(Reponse reponse) {
        this.reponses.remove(reponse);
        reponse.setQuestion(null);
    }

    // ====================================================================
    // Getters et Setters
    // ====================================================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEnonce() { return enonce; }
    public void setEnonce(String enonce) { this.enonce = enonce; }

    public TypeQuestion getTypeQuestion() { return typeQuestion; }
    public void setTypeQuestion(TypeQuestion typeQuestion) { this.typeQuestion = typeQuestion; }

    public double getPoints() { return points; }
    public void setPoints(double points) { this.points = points; }

    public String getReponseCorrecteTexte() { return reponseCorrecteTexte; }
    public void setReponseCorrecteTexte(String reponseCorrecteTexte) { this.reponseCorrecteTexte = reponseCorrecteTexte; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }

    public List<Reponse> getReponses() { return reponses; }
    public void setReponses(List<Reponse> reponses) { this.reponses = reponses; }

    public Questionnaire getQuestionnaire() { return questionnaire; }
    public void setQuestionnaire(Questionnaire questionnaire) { this.questionnaire = questionnaire; }

    public Chapitre getChapitre() { return chapitre; }
    public void setChapitre(Chapitre chapitre) { this.chapitre = chapitre; }

    public List<Test> getTests() { return tests; }
    public void setTests(List<Test> tests) { this.tests = tests; }

    // ====================================================================
    // Méthodes toString, equals, hashCode (importantes pour éviter les problèmes)
    // ====================================================================
    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", enonce='" + enonce + '\'' +
                ", typeQuestion=" + typeQuestion +
                ", points=" + points +
                ", theme='" + theme + '\'' +
                ", niveau='" + niveau + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return id != null && id.equals(question.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}