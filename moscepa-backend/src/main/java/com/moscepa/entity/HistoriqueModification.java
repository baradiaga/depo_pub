// Fichier : src/main/java/com/moscepa/entity/HistoriqueModification.java

package com.moscepa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "moscepa_historique_modifications")
public class HistoriqueModification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banque_question_id", nullable = false)
    private BanqueQuestion banqueQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modificateur_id", nullable = false)
    private Utilisateur modificateur;

    @Column(nullable = false)
    private LocalDateTime dateModification = LocalDateTime.now();

    @Column(nullable = false)
    private String typeModification; // Ex: "CREATION", "MODIFICATION_ENONCE", "MODIFICATION_REPONSES"

    @Column(columnDefinition = "TEXT")
    private String ancienneValeur; // JSON de l'objet avant modification

    @Column(columnDefinition = "TEXT")
    private String nouvelleValeur; // JSON de l'objet après modification

    // Constructeurs
    public HistoriqueModification() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BanqueQuestion getBanqueQuestion() { return banqueQuestion; }
    public void setBanqueQuestion(BanqueQuestion banqueQuestion) { this.banqueQuestion = banqueQuestion; }

    public Utilisateur getModificateur() { return modificateur; }
    public void setModificateur(Utilisateur modificateur) { this.modificateur = modificateur; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    public String getTypeModification() { return typeModification; }
    public void setTypeModification(String typeModification) { this.typeModification = typeModification; }

    public String getAncienneValeur() { return ancienneValeur; }
    public void setAncienneValeur(String ancienneValeur) { this.ancienneValeur = ancienneValeur; }

    public String getNouvelleValeur() { return nouvelleValeur; }
    public void setNouvelleValeur(String nouvelleValeur) { this.nouvelleValeur = nouvelleValeur; }
}
