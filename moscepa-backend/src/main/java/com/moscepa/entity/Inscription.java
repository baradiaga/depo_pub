// Fichier : src/main/java/com/moscepa/entity/Inscription.java

package com.moscepa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "moscepa_inscriptions")
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    
    private Utilisateur etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ec_id", nullable = false) // ec_id pour Element Constitutif
    @JsonBackReference("matiere-inscriptions")
    private ElementConstitutif matiere;

    @Column(name = "date_inscription", nullable = false)
    private LocalDateTime dateInscription;

    @Column(name = "statut", nullable = false)
    private String statut; // EN_ATTENTE, VALIDE, REJETE

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @Column(name = "actif", nullable = false)
    private boolean actif; // true par défaut, peut être désactivé par un admin

    // Méthode utilitaire exécutée avant la sauvegarde
    @PrePersist
    protected void onCreate() {
        this.dateInscription = LocalDateTime.now();
        this.statut = "EN_ATTENTE"; // Statut initial
        this.actif = true; // Actif par défaut
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Utilisateur getEtudiant() { return etudiant; }
    public void setEtudiant(Utilisateur etudiant) { this.etudiant = etudiant; }
    public ElementConstitutif getMatiere() { return matiere; }
    public void setMatiere(ElementConstitutif matiere) { this.matiere = matiere; }
    public LocalDateTime getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDateTime dateInscription) { this.dateInscription = dateInscription; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public LocalDateTime getDateValidation() { return dateValidation; }
    public void setDateValidation(LocalDateTime dateValidation) { this.dateValidation = dateValidation; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
}
