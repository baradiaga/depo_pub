package com.moscepa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
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

    // ========================
    // Helpers
    // ========================
    public void addElementConstitutif(ElementConstitutif ec) {
        elementsConstitutifs.add(ec);
        ec.setFormation(this);
    }

    public void removeElementConstitutif(ElementConstitutif ec) {
        elementsConstitutifs.remove(ec);
        ec.setFormation(null);
    }
}
