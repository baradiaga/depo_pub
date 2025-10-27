package com.moscepa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entité représentant une ressource pédagogique dans le système MOSCEPA
 */
@Entity
@Table(name = "moscepa_ressources")
public class Ressource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeRessource type;

    @NotBlank(message = "Le titre de la ressource est obligatoire")
    @Size(max = 200)
    @Column(name = "titre", nullable = false, length = 200)
    private String titre;

    @NotBlank(message = "Le lien de la ressource est obligatoire")
    @Size(max = 500)
    @Column(name = "lien", nullable = false, length = 500)
    private String lien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommandation_id")
    private RecommandationChapitre recommandation;

    // Constructeurs
    public Ressource() {}

    public Ressource(TypeRessource type, String titre, String lien) {
        this.type = type;
        this.titre = titre;
        this.lien = lien;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeRessource getType() {
        return type;
    }

    public void setType(TypeRessource type) {
        this.type = type;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getLien() {
        return lien;
    }

    public void setLien(String lien) {
        this.lien = lien;
    }

    public RecommandationChapitre getRecommandation() {
        return recommandation;
    }

    public void setRecommandation(RecommandationChapitre recommandation) {
        this.recommandation = recommandation;
    }

    @Override
    public String toString() {
        return "Ressource{" +
                "id=" + id +
                ", type=" + type +
                ", titre='" + titre + '\'' +
                ", lien='" + lien + '\'' +
                '}';
    }
}

