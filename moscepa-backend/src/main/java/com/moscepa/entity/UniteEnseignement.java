// Fichier : src/main/java/com/moscepa/entity/UniteEnseignement.java

package com.moscepa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "moscepa_unites_enseignement")
public class UniteEnseignement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom ne peut pas être vide")
    @Column(nullable = false)
    private String nom;

    @NotBlank(message = "Le code ne peut pas être vide")
    @Column(nullable = false, unique = true)
    private String code;

    @Lob
    private String description;

    @Positive(message = "Le nombre de crédits doit être positif")
    private int credit;

    private int semestre;

    @Lob
    private String objectifs;

    // ==========================================================
    // === LA SEULE ET UNIQUE DÉCLARATION POUR LE RESPONSABLE ===
    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "responsable_id") // Nom de la colonne de la clé étrangère
    private Utilisateur responsable;
    // ==========================================================

    // --- Getters et Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCredit() { return credit; }
    public void setCredit(int credit) { this.credit = credit; }

    public int getSemestre() { return semestre; }
    public void setSemestre(int semestre) { this.semestre = semestre; }

    public String getObjectifs() { return objectifs; }
    public void setObjectifs(String objectifs) { this.objectifs = objectifs; }

    // --- Getter et Setter pour l'objet Utilisateur ---
    public Utilisateur getResponsable() {
        return responsable;
    }

    public void setResponsable(Utilisateur responsable) {
        this.responsable = responsable;
    }
}
