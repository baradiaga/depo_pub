// Fichier : src/main/java/com/moscepa/dto/ChapitreDto.java (Version finale à remplacer)

package com.moscepa.dto;

import java.util.List;

/**
 * DTO représentant les données d'un Chapitre.
 * Cette version est enrichie pour pouvoir contenir la liste de ses sections.
 */
public class ChapitreDto {
    private Long id;
    private String nom;
    private String objectif;
    private Integer niveau;
    
    // CHAMP AJOUTÉ pour contenir les détails des sections
    private List<SectionDto> sections;

    // --- Getters et Setters (existants et nouveaux) ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getObjectif() { return objectif; }
    public void setObjectif(String objectif) { this.objectif = objectif; }

    public Integer getNiveau() { return niveau; }
    public void setNiveau(Integer niveau) { this.niveau = niveau; }
    
    // GETTER ET SETTER POUR LE NOUVEAU CHAMP 'sections'
    public List<SectionDto> getSections() { return sections; }
    public void setSections(List<SectionDto> sections) { this.sections = sections; }
}
