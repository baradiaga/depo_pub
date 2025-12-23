// Fichier : src/main/java/com/moscepa/entity/BanqueQuestion.java

package com.moscepa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "moscepa_banque_questions")
public class BanqueQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String enonce;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeQuestion typeQuestion;

    @Column(nullable = false)
    private Integer points = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulte difficulte = Difficulte.MOYEN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapitre_id", nullable = false)
    private Chapitre chapitre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auteur_id", nullable = false)
    private Utilisateur auteur;

    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime dateModification = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutQuestion statut = StatutQuestion.BROUILLON;

    @Column(nullable = false)
    private Integer nombreUtilisations = 0;

    @Column(nullable = false)
    private Double tauxReussite = 0.0;

    @Column(nullable = false)
    private Double noteQualite = 0.0;

    @OneToMany(mappedBy = "banqueQuestion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BanqueReponse> reponses = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "moscepa_banque_question_tags",
        joinColumns = @JoinColumn(name = "banque_question_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    // Constructeurs
    public BanqueQuestion() {
        // Initialisation explicite pour plus de clarté
        this.difficulte = Difficulte.MOYEN;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEnonce() { return enonce; }
    public void setEnonce(String enonce) { this.enonce = enonce; }

    public TypeQuestion getTypeQuestion() { return typeQuestion; }
    public void setTypeQuestion(TypeQuestion typeQuestion) { this.typeQuestion = typeQuestion; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { 
        this.points = (points != null) ? points : 1; 
    }

    public Difficulte getDifficulte() { return difficulte; }
    public void setDifficulte(Difficulte difficulte) { 
        this.difficulte = (difficulte != null) ? difficulte : Difficulte.MOYEN;
    }

    public Chapitre getChapitre() { return chapitre; }
    public void setChapitre(Chapitre chapitre) { this.chapitre = chapitre; }

    public Utilisateur getAuteur() { return auteur; }
    public void setAuteur(Utilisateur auteur) { this.auteur = auteur; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { 
        this.dateCreation = (dateCreation != null) ? dateCreation : LocalDateTime.now();
    }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { 
        this.dateModification = (dateModification != null) ? dateModification : LocalDateTime.now();
    }

    public StatutQuestion getStatut() { return statut; }
    public void setStatut(StatutQuestion statut) { 
        this.statut = (statut != null) ? statut : StatutQuestion.BROUILLON;
    }

    public Integer getNombreUtilisations() { return nombreUtilisations; }
    public void setNombreUtilisations(Integer nombreUtilisations) { 
        this.nombreUtilisations = (nombreUtilisations != null) ? nombreUtilisations : 0;
    }

    public Double getTauxReussite() { return tauxReussite; }
    public void setTauxReussite(Double tauxReussite) { 
        this.tauxReussite = (tauxReussite != null) ? tauxReussite : 0.0;
    }

    public Double getNoteQualite() { return noteQualite; }
    public void setNoteQualite(Double noteQualite) { 
        this.noteQualite = (noteQualite != null) ? noteQualite : 0.0;
    }

    public List<BanqueReponse> getReponses() { return reponses; }
    public void setReponses(List<BanqueReponse> reponses) { this.reponses = reponses; }

    public Set<Tag> getTags() { return tags; }
    public void setTags(Set<Tag> tags) { this.tags = tags; }

    // Méthodes utilitaires
    public void incrementerUtilisations() {
        this.nombreUtilisations++;
    }

    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }
}