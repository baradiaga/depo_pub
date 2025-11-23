// Fichier : src/main/java/com/moscepa/entity/Chapitre.java (Version Définitivement Corrigée)

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
    @Column(name = "ordre")
    private Integer ordre;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "element_constitutif_id", nullable = false)
    @JsonBackReference
    private ElementConstitutif elementConstitutif;

    @OneToMany(mappedBy = "chapitre", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    @OneToMany(mappedBy = "chapitre", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Questionnaire> questionnaires = new ArrayList<>();

    // ====================================================================
    // === CORRECTION DÉFINITIVE : AJOUT DE LA RELATION VERS L'ENTITÉ TEST ===
    // ====================================================================
    @OneToMany(mappedBy = "chapitre", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Test> tests = new ArrayList<>();


    // --- Constructeurs, méthodes utilitaires, etc. ---
    public Chapitre() {}

    public void addSection(Section section) {
        this.sections.add(section);
        section.setChapitre(this);
    }

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
    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }
    public ElementConstitutif getElementConstitutif() { return elementConstitutif; }
    public void setElementConstitutif(ElementConstitutif elementConstitutif) { this.elementConstitutif = elementConstitutif; }
    public List<Section> getSections() { return sections; }
    public void setSections(List<Section> sections) { this.sections = sections; }
    public List<Questionnaire> getQuestionnaires() { return questionnaires; }
    public void setQuestionnaires(List<Questionnaire> questionnaires) { this.questionnaires = questionnaires; }

    // --- GETTERS ET SETTERS POUR LA NOUVELLE RELATION ---
    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }
}
