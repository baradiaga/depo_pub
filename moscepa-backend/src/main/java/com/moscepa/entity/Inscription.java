// Fichier : src/main/java/com/moscepa/entity/Inscription.java

package com.moscepa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "moscepa_inscriptions")
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CORRECTION : Changement de Utilisateur vers Etudiant pour aligner le mappedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    @JsonIgnoreProperties({"inscriptions", "utilisateur", "formation"})
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ec_id", nullable = false)
    @JsonBackReference("matiere-inscriptions")
    private ElementConstitutif matiere;

    @Column(name = "date_inscription", nullable = false)
    private LocalDateTime dateInscription;

    @Column(name = "statut", nullable = false)
    private String statut; // EN_ATTENTE, VALIDE, REJETE

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @Column(name = "actif", nullable = false)
    private boolean actif;

    @PrePersist
    protected void onCreate() {
        this.dateInscription = LocalDateTime.now();
        this.statut = "EN_ATTENTE";
        this.actif = true;
    }

    // ========================
    // Getters et Setters Corrigés
    // ========================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Etudiant getEtudiant() { return etudiant; }
    public void setEtudiant(Etudiant etudiant) { this.etudiant = etudiant; }

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
