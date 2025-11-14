// Fichier : src/main/java/com/moscepa/dto/ChapitreAvecSectionsDto.java (NOUVEAU FICHIER)

package com.moscepa.dto;

import com.moscepa.entity.Chapitre;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO complet pour les détails d'un chapitre, incluant la liste de ses sections.
 * Ce DTO est spécifiquement utilisé pour la page de gestion où l'on a besoin de tout afficher.
 */
public class ChapitreAvecSectionsDto {

    private Long id;
    private String nom;
    private String objectif; // Ajouté pour être complet
    private Integer niveau;   // Ajouté pour être complet
    private List<SectionDetailDto> sections;

    // Constructeur vide
    public ChapitreAvecSectionsDto() {}

    /**
     * Constructeur de conversion qui prend une entité Chapitre et la transforme
     * en un DTO complet avec ses sections.
     * @param chapitre L'entité Chapitre à convertir.
     */
    public ChapitreAvecSectionsDto(Chapitre chapitre) {
        // 1. Copier les propriétés du chapitre
        this.id = chapitre.getId();
        this.nom = chapitre.getNom();
        this.objectif = chapitre.getObjectif();
        this.niveau = chapitre.getNiveau();

        // 2. Convertir la liste d'entités Section en une liste de SectionDetailDto
        if (chapitre.getSections() != null && !chapitre.getSections().isEmpty()) {
            this.sections = chapitre.getSections().stream()
                .map(SectionDetailDto::new) // Utilise le constructeur de SectionDetailDto
                .collect(Collectors.toList());
        } else {
            // Toujours renvoyer une liste vide, jamais null, pour la sécurité du front-end
            this.sections = Collections.emptyList();
        }
    }

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getObjectif() { return objectif; }
    public void setObjectif(String objectif) { this.objectif = objectif; }

    public Integer getNiveau() { return niveau; }
    public void setNiveau(Integer niveau) { this.niveau = niveau; }

    public List<SectionDetailDto> getSections() { return sections; }
    public void setSections(List<SectionDetailDto> sections) { this.sections = sections; }
}
