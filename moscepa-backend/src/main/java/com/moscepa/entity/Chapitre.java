package com.moscepa.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "moscepa_chapitres")
public class Chapitre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nom;
    private Integer numero;
    private Integer niveau;
    @Lob
    private String objectif;

    // ====================================================================
    // === CORRECTION DE LA RELATION                                    ===
    // ====================================================================
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "element_constitutif_id", nullable = false) // Le nom de la colonne dans la BDD
    @JsonBackReference // Pour éviter les boucles de sérialisation
    private ElementConstitutif elementConstitutif;

    @OneToMany(mappedBy = "chapitre", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Section> sections = new ArrayList<>();

    @OneToMany(mappedBy = "chapitre", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Questionnaire> questionnaires = new ArrayList<>();

    // ... (constructeurs)

    public void addSection(Section section) {
        this.sections.add(section);
        section.setChapitre(this);
    }
    // ... (autres méthodes utilitaires)

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    public Integer getNiveau() { return niveau; }
    public void setNiveau(Integer niveau) { this.niveau = niveau; }
    public String getObjectif() { return objectif; }
    public void setObjectif(String objectif) { this.objectif = objectif; }
    public List<Section> getSections() { return sections; }
    public void setSections(List<Section> sections) { this.sections = sections; }
    public List<Questionnaire> getQuestionnaires() { return questionnaires; }
    public void setQuestionnaires(List<Questionnaire> questionnaires) { this.questionnaires = questionnaires; }

    // ====================================================================
    // === CORRECTION DES GETTERS/SETTERS POUR LA RELATION              ===
    // ====================================================================
    public ElementConstitutif getElementConstitutif() {
        return elementConstitutif;
    }

    public void setElementConstitutif(ElementConstitutif elementConstitutif) {
        this.elementConstitutif = elementConstitutif;
    }
}
