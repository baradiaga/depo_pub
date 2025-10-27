package com.moscepa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "moscepa_elements_constitutifs")
public class ElementConstitutif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Le nom de la colonne est 'credit' (singulier)
    @Column(name = "credits", nullable = false)
    private Integer credit;

    // Le nom de la colonne est 'unite_enseignement_id'
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unite_enseignement_id", nullable = false)
    private UniteEnseignement uniteEnseignement;

    // Le nom de la colonne est 'enseignant_id'
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enseignant_id")
    private Utilisateur enseignant;

    // --- Constructeurs ---
    public ElementConstitutif() {}

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getCredit() { return credit; }
    public void setCredit(Integer credit) { this.credit = credit; }
    public UniteEnseignement getUniteEnseignement() { return uniteEnseignement; }
    public void setUniteEnseignement(UniteEnseignement uniteEnseignement) { this.uniteEnseignement = uniteEnseignement; }
    public Utilisateur getEnseignant() { return enseignant; }
    public void setEnseignant(Utilisateur enseignant) { this.enseignant = enseignant; }
}
