// Fichier : src/main/java/com/moscepa/entity/RecommandationChapitre.java (Corrigé)

package com.moscepa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "moscepa_recommandations_chapitres")
public class RecommandationChapitre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ====================================================================
    // === CORRECTION APPLIQUÉE ICI                                     ===
    // ====================================================================
    // On remplace 'Etudiant' par 'Utilisateur'
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Utilisateur etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapitre_id", nullable = false)
    private Chapitre chapitre;

    // ... (le reste des champs reste identique)
    @NotBlank(message = "Le nom du chapitre est obligatoire")
    @Size(max = 150)
    @Column(name = "nom_chapitre", nullable = false, length = 150)
    private String nomChapitre;

    @NotBlank(message = "Le nom de la matière est obligatoire")
    @Size(max = 100)
    @Column(name = "matiere", nullable = false, length = 100)
    private String matiere;

    @NotNull(message = "Le score est obligatoire")
    @Column(name = "score", nullable = false)
    private Double score;

    @Size(max = 500)
    @Column(name = "raison", length = 500)
    private String raison;

    @Enumerated(EnumType.STRING)
    @Column(name = "niveau", nullable = false)
    private NiveauDifficulte niveau;

    @NotNull(message = "La progression est obligatoire")
    @Column(name = "progression", nullable = false)
    private Integer progression = 0;

    @Size(max = 50)
    @Column(name = "duree_estimee", length = 50)
    private String dureeEstimee;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutRecommandation statut = StatutRecommandation.NON_COMMENCE;

    @CreationTimestamp
    @Column(name = "date_recommandation", nullable = false, updatable = false)
    private LocalDateTime dateRecommandation;

    @OneToMany(mappedBy = "recommandation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ressource> ressources = new ArrayList<>();

    // Constructeurs
    public RecommandationChapitre() {}

    // ====================================================================
    // === CORRECTION APPLIQUÉE ICI                                     ===
    // ====================================================================
    public RecommandationChapitre(Utilisateur etudiant, Chapitre chapitre, String nomChapitre, 
                                 String matiere, Double score, String raison, NiveauDifficulte niveau) {
        this.etudiant = etudiant;
        this.chapitre = chapitre;
        this.nomChapitre = nomChapitre;
        this.matiere = matiere;
        this.score = score;
        this.raison = raison;
        this.niveau = niveau;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // ====================================================================
    // === CORRECTION APPLIQUÉE ICI                                     ===
    // ====================================================================
    public Utilisateur getEtudiant() { return etudiant; }
    public void setEtudiant(Utilisateur etudiant) { this.etudiant = etudiant; }

    public Chapitre getChapitre() { return chapitre; }
    public void setChapitre(Chapitre chapitre) { this.chapitre = chapitre; }

    // ... (le reste des getters et setters est identique)
    public String getNomChapitre() { return nomChapitre; }
    public void setNomChapitre(String nomChapitre) { this.nomChapitre = nomChapitre; }
    public String getMatiere() { return matiere; }
    public void setMatiere(String matiere) { this.matiere = matiere; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public String getRaison() { return raison; }
    public void setRaison(String raison) { this.raison = raison; }
    public NiveauDifficulte getNiveau() { return niveau; }
    public void setNiveau(NiveauDifficulte niveau) { this.niveau = niveau; }
    public Integer getProgression() { return progression; }
    public void setProgression(Integer progression) { this.progression = progression; }
    public String getDureeEstimee() { return dureeEstimee; }
    public void setDureeEstimee(String dureeEstimee) { this.dureeEstimee = dureeEstimee; }
    public StatutRecommandation getStatut() { return statut; }
    public void setStatut(StatutRecommandation statut) { this.statut = statut; }
    public LocalDateTime getDateRecommandation() { return dateRecommandation; }
    public void setDateRecommandation(LocalDateTime dateRecommandation) { this.dateRecommandation = dateRecommandation; }
    public List<Ressource> getRessources() { return ressources; }
    public void setRessources(List<Ressource> ressources) { this.ressources = ressources; }

    @Override
    public String toString() {
        return "RecommandationChapitre{" +
                "id=" + id +
                ", nomChapitre='" + nomChapitre + '\'' +
                ", matiere='" + matiere + '\'' +
                ", score=" + score +
                ", niveau=" + niveau +
                ", statut=" + statut +
                '}';
    }
}
