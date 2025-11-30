// Fichier : src/main/java/com/moscepa/entity/EvaluationQuestion.java

package com.moscepa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "moscepa_evaluation_questions")
public class EvaluationQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banque_question_id", nullable = false)
    private BanqueQuestion banqueQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluateur_id", nullable = false)
    private Utilisateur evaluateur;

    @Column(nullable = false)
    private Integer note; // Note sur 5

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Column(nullable = false)
    private LocalDateTime dateEvaluation = LocalDateTime.now();

    // Constructeurs
    public EvaluationQuestion() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BanqueQuestion getBanqueQuestion() { return banqueQuestion; }
    public void setBanqueQuestion(BanqueQuestion banqueQuestion) { this.banqueQuestion = banqueQuestion; }

    public Utilisateur getEvaluateur() { return evaluateur; }
    public void setEvaluateur(Utilisateur evaluateur) { this.evaluateur = evaluateur; }

    public Integer getNote() { return note; }
    public void setNote(Integer note) { this.note = note; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public LocalDateTime getDateEvaluation() { return dateEvaluation; }
    public void setDateEvaluation(LocalDateTime dateEvaluation) { this.dateEvaluation = dateEvaluation; }
}
