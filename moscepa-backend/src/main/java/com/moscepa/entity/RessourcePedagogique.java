// Fichier : src/main/java/com/moscepa/entity/RessourcePedagogique.java

package com.moscepa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "moscepa_ressources_pedagogiques")
public class RessourcePedagogique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String nomFichier; // Nom original du fichier

    @Column(nullable = false)
    private String cheminStockage; // Chemin d'accès sur le serveur (ou S3/Cloud)

    @Column(nullable = false)
    private String typeMime; // Ex: application/pdf, video/mp4

    @Column(nullable = false)
    private Long tailleOctets;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auteur_id", nullable = false)
    private Utilisateur auteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapitre_id")
    private Chapitre chapitre; // Classification par chapitre

    @Column(nullable = false)
    private LocalDateTime dateTeleversement = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "moscepa_ressource_tags",
        joinColumns = @JoinColumn(name = "ressource_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    // Constructeurs
    public RessourcePedagogique() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNomFichier() { return nomFichier; }
    public void setNomFichier(String nomFichier) { this.nomFichier = nomFichier; }

    public String getCheminStockage() { return cheminStockage; }
    public void setCheminStockage(String cheminStockage) { this.cheminStockage = cheminStockage; }

    public String getTypeMime() { return typeMime; }
    public void setTypeMime(String typeMime) { this.typeMime = typeMime; }

    public Long getTailleOctets() { return tailleOctets; }
    public void setTailleOctets(Long tailleOctets) { this.tailleOctets = tailleOctets; }

    public Utilisateur getAuteur() { return auteur; }
    public void setAuteur(Utilisateur auteur) { this.auteur = auteur; }

    public Chapitre getChapitre() { return chapitre; }
    public void setChapitre(Chapitre chapitre) { this.chapitre = chapitre; }

    public LocalDateTime getDateTeleversement() { return dateTeleversement; }
    public void setDateTeleversement(LocalDateTime dateTeleversement) { this.dateTeleversement = dateTeleversement; }

    public Set<Tag> getTags() { return tags; }
    public void setTags(Set<Tag> tags) { this.tags = tags; }
}
