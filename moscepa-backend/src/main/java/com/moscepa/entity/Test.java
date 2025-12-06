package com.moscepa.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "moscepa_tests")
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column
    private Integer duree;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Relation avec Chapitre
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapitre_id", nullable = false)
    private Chapitre chapitre;

    // Relation avec Questionnaire
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionnaire_id")
    private Questionnaire questionnaire;

    // Relation ManyToMany avec Question
    @ManyToMany
    @JoinTable(
        name = "moscepa_test_questions",
        joinColumns = @JoinColumn(name = "test_id"),
        inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> questions = new ArrayList<>();

    // 🔥 Relation avec ResultatTest (sans cascade REMOVE)
    @OneToMany(mappedBy = "test", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<ResultatTest> resultats = new ArrayList<>();

    // 🔥 Relation vers ElementConstitutif pour corriger le mappedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "element_constitutif_id")
    private ElementConstitutif elementConstitutif;

    // --- Helpers ---
    public void addQuestion(Question question) {
        this.questions.add(question);
        question.getTests().add(this);
    }

    public void removeQuestion(Question question) {
        this.questions.remove(question);
        question.getTests().remove(this);
    }

    public void addResultat(ResultatTest resultat) {
        this.resultats.add(resultat);
        resultat.setTest(this);
    }

    public void removeResultat(ResultatTest resultat) {
        this.resultats.remove(resultat);
        resultat.setTest(null);
    }

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public Integer getDuree() { return duree; }
    public void setDuree(Integer duree) { this.duree = duree; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Chapitre getChapitre() { return chapitre; }
    public void setChapitre(Chapitre chapitre) { this.chapitre = chapitre; }

    public Questionnaire getQuestionnaire() { return questionnaire; }
    public void setQuestionnaire(Questionnaire questionnaire) { this.questionnaire = questionnaire; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }

    public List<ResultatTest> getResultats() { return resultats; }
    public void setResultats(List<ResultatTest> resultats) { this.resultats = resultats; }

    public ElementConstitutif getElementConstitutif() { return elementConstitutif; }
    public void setElementConstitutif(ElementConstitutif elementConstitutif) { this.elementConstitutif = elementConstitutif; }
}
