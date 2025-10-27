package com.moscepa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "moscepa_parcours")
public class Parcours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapitre_id", nullable = false)
    private Chapitre chapitre;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_parcours", nullable = false)
    private TypeParcours type;

    @Column(name = "date_ajout", nullable = false)
    private LocalDateTime dateAjout;

    // Enumération pour définir les types de parcours possibles
    public enum TypeParcours {
        RECOMMANDE,
        CHOISI
    }

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public Chapitre getChapitre() { return chapitre; }
    public void setChapitre(Chapitre chapitre) { this.chapitre = chapitre; }
    public TypeParcours getType() { return type; }
    public void setType(TypeParcours type) { this.type = type; }
    public LocalDateTime getDateAjout() { return dateAjout; }
    public void setDateAjout(LocalDateTime dateAjout) { this.dateAjout = dateAjout; }
}
