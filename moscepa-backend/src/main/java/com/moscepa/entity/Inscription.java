// Fichier : src/main/java/com/moscepa/entity/Inscription.java

package com.moscepa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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
    private ElementConstitutif matiere;

    @Column(name = "date_inscription", nullable = false)
    private LocalDateTime dateInscription;

    // Méthode utilitaire exécutée avant la sauvegarde
    @PrePersist
    protected void onCreate() {
        this.dateInscription = LocalDateTime.now();
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
}
