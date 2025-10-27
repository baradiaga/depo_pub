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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapitre_id", nullable = false)
    private Chapitre chapitre;

    // CORRECTION : Assurez-vous que cette relation existe.
    // C'est elle qui définit la liste de questions pour un test.
    @ManyToMany
    @JoinTable(
        name = "moscepa_test_questions",
        joinColumns = @JoinColumn(name = "test_id"),
        inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> questions = new ArrayList<>();

    // --- Getters et Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public Chapitre getChapitre() { return chapitre; }
    public void setChapitre(Chapitre chapitre) { this.chapitre = chapitre; }

    // CORRECTION : Assurez-vous que ce getter est présent.
    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
