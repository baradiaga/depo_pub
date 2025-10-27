package com.moscepa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "moscepa_inscriptions")
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_id", nullable = false)
    private Matiere matiere;

    @Column(name = "statut", nullable = false)
    private String statut; // "À reprendre", "Terminé", etc.

    // Getters et Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Etudiant getEtudiant() { return etudiant; }
    public void setEtudiant(Etudiant etudiant) { this.etudiant = etudiant; }
    public Matiere getMatiere() { return matiere; }
    public void setMatiere(Matiere matiere) { this.matiere = matiere; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
