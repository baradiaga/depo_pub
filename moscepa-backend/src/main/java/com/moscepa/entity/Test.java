package com.moscepa.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @Column(name = "tentatives_max")
    private Integer tentativesMax = 1; // valeur par défaut

    @Column(name = "tentatives_illimitees")
    private Boolean tentativesIllimitees = false; // objet pour éviter null

    // Relation avec Chapitre
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapitre_id", nullable = false)
    @JsonBackReference("chapitre-tests")
    private Chapitre chapitre;

    // Relation avec Questionnaire
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionnaire_id")
    @JsonBackReference("questionnaire-tests")
    private Questionnaire questionnaire;

    // Relation ManyToMany avec Question
    @ManyToMany
    @JoinTable(
        name = "moscepa_test_questions",
        joinColumns = @JoinColumn(name = "test_id"),
        inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    @JsonManagedReference("test-questions")
    private List<Question> questions = new ArrayList<>();

    // Relation avec ResultatTest
    @OneToMany(mappedBy = "test", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonManagedReference("test-resultats")
    private List<ResultatTest> resultats = new ArrayList<>();

    // Relation vers ElementConstitutif
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "element_constitutif_id")
    @JsonBackReference("elementConstitutif-tests")
    private ElementConstitutif elementConstitutif;

    // --- Helpers ---
    public void addQuestion(Question question) {
        this.questions.add(question);
        if (!question.getTests().contains(this)) {
            question.getTests().add(this);
        }
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

    public Integer getTentativesMax() { return tentativesMax; }
    public void setTentativesMax(Integer tentativesMax) {
        if (tentativesMax != null && tentativesMax >= 0) {
            this.tentativesMax = tentativesMax;
        }
    }

    public Boolean getTentativesIllimitees() { return tentativesIllimitees; }
    public void setTentativesIllimitees(Boolean tentativesIllimitees) {
        this.tentativesIllimitees = tentativesIllimitees != null && tentativesIllimitees;
    }

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

    // --- Méthodes toString, equals, hashCode ---
    @Override
    public String toString() {
        return "Test{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", duree=" + duree +
                ", description='" + description + '\'' +
                ", tentativesMax=" + tentativesMax +
                ", tentativesIllimitees=" + tentativesIllimitees +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Test test = (Test) o;
        return id != null && id.equals(test.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
