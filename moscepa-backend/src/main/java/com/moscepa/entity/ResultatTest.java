package com.moscepa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "moscepa_resultats_tests")
public class ResultatTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @Column(name = "score", nullable = false)
    private Double score;

    @Column(name = "score_total", nullable = false)
    private Double scoreTotal;

    @Column(name = "date_test", nullable = false)
    private LocalDateTime dateTest;

    @Column(name = "bonnes_reponses", nullable = false)
    private int bonnesReponses;

    // =======================================================
    // === 1. LE DERNIER CHAMP MANQUANT EST AJOUTÉ ICI ===
    // =======================================================
    @Column(name = "total_questions", nullable = false)
    private int totalQuestions;


    // Constructeur
    public ResultatTest() {
        this.dateTest = LocalDateTime.now();
    }

    // --- Getters et Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Etudiant getEtudiant() { return etudiant; }
    public void setEtudiant(Etudiant etudiant) { this.etudiant = etudiant; }
    public Test getTest() { return test; }
    public void setTest(Test test) { this.test = test; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public Double getScoreTotal() { return scoreTotal; }
    public void setScoreTotal(Double scoreTotal) { this.scoreTotal = scoreTotal; }
    public LocalDateTime getDateTest() { return dateTest; }
    public void setDateTest(LocalDateTime dateTest) { this.dateTest = dateTest; }

    public int getBonnesReponses() {
        return bonnesReponses;
    }
    public void setBonnesReponses(int bonnesReponses) {
        this.bonnesReponses = bonnesReponses;
    }

    // =======================================================
    // === 2. LES DERNIÈRES MÉTHODES MANQUANTES SONT AJOUTÉES ICI ===
    // =======================================================
    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
}
