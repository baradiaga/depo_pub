package com.moscepa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

@Entity
@Table(name = "moscepa_formations")
public class Formation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(nullable = false, unique = true, length = 50)
    private String code; // Ex: L1-INFO

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutFormation statut = StatutFormation.EN_PREPARATION;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NiveauEtude niveauEtude = NiveauEtude.LICENCE;

    @Column(nullable = false)
    private Integer duree = 1; // Correspond à "duree" côté frontend

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_pedagogique_id")
    @JsonIgnore
    private Utilisateur responsablePedagogique;

    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createur_id", nullable = false)
    @JsonIgnore
    private Utilisateur createur;

    @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ElementConstitutif> elementsConstitutifs = new HashSet<>();

    // Nouveaux champs pour la structure académique professionnelle
    @Column(columnDefinition = "TEXT")
    private String objectifs;

    @Column(columnDefinition = "TEXT")
    private String prerequis;

    @Column(columnDefinition = "TEXT")
    private String debouches;

    @Column(columnDefinition = "TEXT")
    private String evaluationModalites;

    @Column(length = 50)
    private String modaliteEnseignement; // PRESENTIEL, DISTANCIEL, HYBRIDE

    @Column(length = 255)
    private String lieu;

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;

    private Integer capacite;
    private Double tarif;

    @Column(length = 255)
    private String certificationProfessionnelle; // RNCP/RS

    // Relation OneToMany pour les Compétences
    @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompetenceDetail> competences = new ArrayList<>();

    // Relation OneToMany pour les Unités d'Enseignement
    @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UniteEnseignement> unitesEnseignement = new ArrayList<>();
    // ========================
// NOUVEAUX CHAMPS - Références administratives
// ========================

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "etablissement_id", nullable = false)
private Etablissement etablissement;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "uefr_id", nullable = false)
private Uefr uefr;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "departement_id", nullable = false)
private Departement departement;

    // ========================
    // Constructeurs
    // ========================
    public Formation() {}

    // ========================
    // Getters et Setters
    // ========================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public StatutFormation getStatut() { return statut; }
    public void setStatut(StatutFormation statut) { this.statut = statut; }

    public NiveauEtude getNiveauEtude() { return niveauEtude; }
    public void setNiveauEtude(NiveauEtude niveauEtude) { this.niveauEtude = niveauEtude; }

    public Integer getDuree() { return duree; }
    public void setDuree(Integer duree) { this.duree = duree; }

    public Utilisateur getResponsablePedagogique() { return responsablePedagogique; }
    public void setResponsablePedagogique(Utilisateur responsablePedagogique) { this.responsablePedagogique = responsablePedagogique; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public Utilisateur getCreateur() { return createur; }
    public void setCreateur(Utilisateur createur) { this.createur = createur; }

    public Set<ElementConstitutif> getElementsConstitutifs() { return elementsConstitutifs; }
    public void setElementsConstitutifs(Set<ElementConstitutif> elementsConstitutifs) { 
        this.elementsConstitutifs = elementsConstitutifs; 
    }

    // --- Getters et Setters pour les nouveaux champs ---
    public String getObjectifs() { return objectifs; }
    public void setObjectifs(String objectifs) { this.objectifs = objectifs; }

    public String getPrerequis() { return prerequis; }
    public void setPrerequis(String prerequis) { this.prerequis = prerequis; }

    public String getDebouches() { return debouches; }
    public void setDebouches(String debouches) { this.debouches = debouches; }

    public String getEvaluationModalites() { return evaluationModalites; }
    public void setEvaluationModalites(String evaluationModalites) { this.evaluationModalites = evaluationModalites; }

    public String getModaliteEnseignement() { return modaliteEnseignement; }
    public void setModaliteEnseignement(String modaliteEnseignement) { this.modaliteEnseignement = modaliteEnseignement; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }

    public Integer getCapacite() { return capacite; }
    public void setCapacite(Integer capacite) { this.capacite = capacite; }

    public Double getTarif() { return tarif; }
    public void setTarif(Double tarif) { this.tarif = tarif; }

    public String getCertificationProfessionnelle() { return certificationProfessionnelle; }
    public void setCertificationProfessionnelle(String certificationProfessionnelle) { this.certificationProfessionnelle = certificationProfessionnelle; }

    public List<CompetenceDetail> getCompetences() { return competences; }
    public void setCompetences(List<CompetenceDetail> competences) { this.competences = competences; }

    public List<UniteEnseignement> getUnitesEnseignement() { return unitesEnseignement; }
    public void setUnitesEnseignement(List<UniteEnseignement> unitesEnseignement) { this.unitesEnseignement = unitesEnseignement; }

    // ========================
    // Helpers (Conservés pour la compatibilité EC)
    // ========================
    public void addElementConstitutif(ElementConstitutif ec) {
        elementsConstitutifs.add(ec);
        ec.setFormation(this);
    }

    public void removeElementConstitutif(ElementConstitutif ec) {
        elementsConstitutifs.remove(ec);
        ec.setFormation(null);
    }
    // --- Getters et Setters pour les nouveaux champs administratifs ---
public Etablissement getEtablissement() { return etablissement; }
public void setEtablissement(Etablissement etablissement) { this.etablissement = etablissement; }

public Uefr getUefr() { return uefr; }
public void setUefr(Uefr uefr) { this.uefr = uefr; }

public Departement getDepartement() { return departement; }
public void setDepartement(Departement departement) { this.departement = departement; }
}
