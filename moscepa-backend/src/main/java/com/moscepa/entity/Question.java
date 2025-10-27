package com.moscepa.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Reponse> reponses = new ArrayList<>();

    // --- MODIFICATION 1 : Le lien vers Questionnaire est maintenant optionnel ---
    // La contrainte nullable = false a été retirée.
    // Une question peut maintenant exister sans être liée à un questionnaire (c'est une question de la "banque").
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionnaire_id") // nullable = false a été supprimé
    @JsonBackReference
    private Questionnaire questionnaire;

    // --- MODIFICATION 2 : Ajout des liens vers Matiere et Chapitre ---
    // Ces liens sont nécessaires pour pouvoir filtrer les questions de la banque.
    // Nous supposons qu'une question est liée à un chapitre, qui lui-même est lié à une matière.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapitre_id")
    private Chapitre chapitre;

    public void addReponse(Reponse reponse) {
        this.reponses.add(reponse);
        reponse.setQuestion(this);
    }

    // --- Getters et Setters (y compris pour les nouveaux champs) ---
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
    public List<Reponse> getReponses() { return reponses; }
    public void setReponses(List<Reponse> reponses) { this.reponses = reponses; }
    public Questionnaire getQuestionnaire() { return questionnaire; }
    public void setQuestionnaire(Questionnaire questionnaire) { this.questionnaire = questionnaire; }
    
    // Getters et Setters pour Chapitre
    public Chapitre getChapitre() { return chapitre; }
    public void setChapitre(Chapitre chapitre) { this.chapitre = chapitre; }
}
