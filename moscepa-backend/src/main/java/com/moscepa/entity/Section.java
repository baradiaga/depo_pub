// Fichier : src/main/java/com/moscepa/entity/Section.java (Corrigé et Complet)

package com.moscepa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "moscepa_sections")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "titre", nullable = false)
    private String titre;

    /**
     * Contient le contenu pédagogique de la section.
     * `columnDefinition = "TEXT"` est utilisé pour s'assurer que la base de données
     * alloue un type de colonne capable de stocker de longues chaînes de caractères
     * (comme TEXT ou CLOB), idéal pour du HTML ou du Markdown.
     */
    @Lob // Indique que c'est un "Large Object"
    @Column(name = "contenu", columnDefinition = "TEXT")
    private String contenu;

    /**
     * Définit la position de la section à l'intérieur d'un chapitre.
     * Permet d'afficher les sections dans le bon ordre.
     */
    @NotNull(message = "L'ordre ne peut pas être nul")
    @Column(name = "ordre", nullable = false)
    private Integer ordre; // Le champ 'ordre' est nécessaire ici.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapitre_id", nullable = false)
    private Chapitre chapitre;

    // --- Constructeurs, Getters et Setters ---

    public Section() {}

    // Getters et Setters existants...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public Chapitre getChapitre() { return chapitre; }
    public void setChapitre(Chapitre chapitre) { this.chapitre = chapitre; }

    // ====================================================================
    // === CORRECTION APPLIQUÉE ICI                                     ===
    // ====================================================================
    // Les méthodes sont maintenant correctement implémentées.

    public Integer getOrdre() {
        return this.ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }
}
