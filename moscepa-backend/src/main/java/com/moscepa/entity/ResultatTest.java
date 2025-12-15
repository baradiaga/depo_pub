package com.moscepa.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "moscepa_resultats_tests")
public class ResultatTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ====================================================================
    // === RELATION AVEC ETUDIANT (Utilisateur)                         ===
    // ====================================================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    @JsonBackReference("utilisateur-resultats")  // Si Utilisateur a une liste de résultats
    private Utilisateur etudiant;

    // ====================================================================
    // === RELATION AVEC TEST                                           ===
    // ====================================================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    @JsonBackReference("test-resultats")  // Même nom que dans Test
    private Test test;

    @Column(name = "score", nullable = false)
    private Double score;

    @Column(name = "score_total", nullable = false)
    private Double scoreTotal;

    @Column(name = "date_test", nullable = false)
    private LocalDateTime dateTest;

    @Column(name = "bonnes_reponses", nullable = false)
    private int bonnesReponses;

    @Column(name = "total_questions", nullable = false)
    private int totalQuestions;

    // ====================================================================
    // === CALCUL DU POURCENTAGE                                        ===
    // ====================================================================
    @Transient  // Champ non persisté en base
    private Double pourcentage;

    public Double getPourcentage() {
        if (scoreTotal != null && scoreTotal > 0) {
            return (score / scoreTotal) * 100;
        }
        return 0.0;
    }

    // Constructeur
    public ResultatTest() {
        this.dateTest = LocalDateTime.now();
    }

    // ====================================================================
    // === Getters et Setters                                           ===
    // ====================================================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utilisateur getEtudiant() { return etudiant; }
    public void setEtudiant(Utilisateur etudiant) { this.etudiant = etudiant; }

    public Test getTest() { return test; }
    public void setTest(Test test) { this.test = test; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public Double getScoreTotal() { return scoreTotal; }
    public void setScoreTotal(Double scoreTotal) { this.scoreTotal = scoreTotal; }

    public LocalDateTime getDateTest() { return dateTest; }
    public void setDateTest(LocalDateTime dateTest) { this.dateTest = dateTest; }

    public int getBonnesReponses() { return bonnesReponses; }
    public void setBonnesReponses(int bonnesReponses) { this.bonnesReponses = bonnesReponses; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    // ====================================================================
    // === Méthodes toString, equals, hashCode                          ===
    // ====================================================================
    @Override
    public String toString() {
        return "ResultatTest{" +
                "id=" + id +
                ", score=" + score +
                ", scoreTotal=" + scoreTotal +
                ", dateTest=" + dateTest +
                ", bonnesReponses=" + bonnesReponses +
                ", totalQuestions=" + totalQuestions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResultatTest that = (ResultatTest) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}