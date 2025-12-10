package com.moscepa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "moscepa_competence_detail")
public class CompetenceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String libelle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NiveauAcquisition niveauAcquisition;

    @Column(columnDefinition = "TEXT")
    private String indicateursEvaluation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public NiveauAcquisition getNiveauAcquisition() { return niveauAcquisition; }
    public void setNiveauAcquisition(NiveauAcquisition niveauAcquisition) { this.niveauAcquisition = niveauAcquisition; }
    public String getIndicateursEvaluation() { return indicateursEvaluation; }
    public void setIndicateursEvaluation(String indicateursEvaluation) { this.indicateursEvaluation = indicateursEvaluation; }
    public Formation getFormation() { return formation; }
    public void setFormation(Formation formation) { this.formation = formation; }
}
