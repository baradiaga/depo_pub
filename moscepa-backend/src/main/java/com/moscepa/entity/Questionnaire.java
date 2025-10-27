package com.moscepa.entity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "moscepa_questionnaires")
public class Questionnaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    private String matiere;
    private int duree;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDate dateCreation;

    private String auteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapitre_id", nullable = false)
    private Chapitre chapitre;

    @OneToMany(
        mappedBy = "questionnaire",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @JsonManagedReference
    private List<Question> questions = new ArrayList<>();

    @PrePersist
    public void onPrePersist() {
        this.dateCreation = LocalDate.now();
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
        question.setQuestionnaire(this);
    }

    public void removeQuestion(Question question) {
        this.questions.remove(question);
        question.setQuestionnaire(null);
    }

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getMatiere() { return matiere; }
    public void setMatiere(String matiere) { this.matiere = matiere; }
    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }
    public Chapitre getChapitre() { return chapitre; }
    public void setChapitre(Chapitre chapitre) { this.chapitre = chapitre; }
    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
}
