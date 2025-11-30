// Fichier : src/main/java/com/moscepa/entity/BanqueReponse.java

package com.moscepa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "moscepa_banque_reponses")
public class BanqueReponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String texte;

    @Column(nullable = false)
    private Boolean correcte = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banque_question_id", nullable = false)
    private BanqueQuestion banqueQuestion;

    // Constructeurs
    public BanqueReponse() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTexte() { return texte; }
    public void setTexte(String texte) { this.texte = texte; }

    public Boolean getCorrecte() { return correcte; }
    public void setCorrecte(Boolean correcte) { this.correcte = correcte; }

    public BanqueQuestion getBanqueQuestion() { return banqueQuestion; }
    public void setBanqueQuestion(BanqueQuestion banqueQuestion) { this.banqueQuestion = banqueQuestion; }
}
