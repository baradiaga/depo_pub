// Fichier : ElementConstitutif.java (Version Finale et Corrigée)

package com.moscepa.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @Lob
    private String description;

    @Column(name = "credits", nullable = false)
    private Integer credit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unite_enseignement_id", nullable = false)
    private UniteEnseignement uniteEnseignement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enseignant_id")
    private Utilisateur enseignant;

    @OneToMany(
        mappedBy = "elementConstitutif",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @JsonManagedReference
    private List<Chapitre> chapitres = new ArrayList<>();

    // ====================================================================
    // === LA RELATION VERS LES ÉTUDIANTS A ÉTÉ COMPLÈTEMENT SUPPRIMÉE  ===
    // ====================================================================

    // --- Constructeurs ---
    public ElementConstitutif() {}

    // --- Méthodes utilitaires pour la synchronisation ---
    public void addChapitre(Chapitre chapitre) {
        this.chapitres.add(chapitre);
        chapitre.setElementConstitutif(this);
    }

    public void removeChapitre(Chapitre chapitre) {
        this.chapitres.remove(chapitre);
        chapitre.setElementConstitutif(null);
    }

    // --- Getters et Setters (sans ceux de etudiantsInscrits) ---
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
    public List<Chapitre> getChapitres() { return chapitres; }
    public void setChapitres(List<Chapitre> chapitres) { this.chapitres = chapitres; }
}
