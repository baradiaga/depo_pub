package com.moscepa.entity;
import com.fasterxml.jackson.annotation.JsonBackReference; 
import jakarta.persistence.*;

@Entity
@Table(name = "moscepa_reponses")
public class Reponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String texte;

    // Renommé de 'correcte' à 'isCorrecte' pour suivre les conventions Java pour les booléens,
    // mais 'correcte' fonctionne aussi. Le getter est 'isCorrecte()'.
    private boolean correcte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonBackReference
    private Question question;

    // Getters et Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTexte() { return texte; }
    public void setTexte(String texte) { this.texte = texte; }

    public boolean isCorrecte() { return correcte; }
    public void setCorrecte(boolean correcte) { this.correcte = correcte; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
}
