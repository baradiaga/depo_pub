package com.moscepa.entity;
import com.fasterxml.jackson.annotation.JsonBackReference; 
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "moscepa_chapitres")
public class Chapitre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du chapitre est obligatoire")
    @Size(max = 200)
    @Column(name = "nom", nullable = false, length = 200)
    private String nom;

    @Column(name = "numero")
    private Integer numero; // Pour le numéro d'ordre du chapitre

    @Column(name = "niveau")
    private Integer niveau;

    @Column(name = "objectif", columnDefinition = "TEXT")
    private String objectif;

    // --- RELATIONS ---

    @NotNull // Un chapitre doit toujours être lié à une matière.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_id", nullable = false)
    @JsonBackReference
    private Matiere matiere;

    // On explicite le FetchType.LAZY pour la clarté.
    @OneToMany(mappedBy = "chapitre", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Section> sections = new ArrayList<>();

    // Relation avec Questionnaire - C'est la contrepartie de la relation dans Questionnaire.java
    @OneToMany(mappedBy = "chapitre", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Questionnaire> questionnaires = new ArrayList<>();

    @OneToMany(mappedBy = "chapitre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Test> tests = new ArrayList<>();


    // --- Constructeurs ---
    public Chapitre() {}

    // --- MÉTHODES UTILITAIRES DE SYNCHRONISATION (BONNE PRATIQUE) ---

    public void addSection(Section section) {
        this.sections.add(section);
        section.setChapitre(this);
    }

    public void removeSection(Section section) {
        this.sections.remove(section);
        section.setChapitre(null);
    }

    public void addQuestionnaire(Questionnaire questionnaire) {
        this.questionnaires.add(questionnaire);
        questionnaire.setChapitre(this);
    }

    public void removeQuestionnaire(Questionnaire questionnaire) {
        this.questionnaires.remove(questionnaire);
        questionnaire.setChapitre(null);
    }

    public void addTest(Test test) {
        this.tests.add(test);
        test.setChapitre(this);
    }

    public void removeTest(Test test) {
        this.tests.remove(test);
        test.setChapitre(null);
    }

    // --- Getters et Setters ---
    // (Aucun changement nécessaire ici, ils sont corrects)

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    public Integer getNiveau() { return niveau; }
    public void setNiveau(Integer niveau) { this.niveau = niveau; }
    public String getObjectif() { return objectif; }
    public void setObjectif(String objectif) { this.objectif = objectif; }
    public Matiere getMatiere() { return matiere; }
    public void setMatiere(Matiere matiere) { this.matiere = matiere; }
    public List<Section> getSections() { return sections; }
    public void setSections(List<Section> sections) { this.sections = sections; }
    public List<Questionnaire> getQuestionnaires() { return questionnaires; }
    public void setQuestionnaires(List<Questionnaire> questionnaires) { this.questionnaires = questionnaires; }
    public List<Test> getTests() { return tests; }
    public void setTests(List<Test> tests) { this.tests = tests; }

    @Override
    public String toString() {
        return "Chapitre{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", numero=" + numero +
                ", matiere=" + (matiere != null ? matiere.getNom() : "null") +
                '}';
    }
}