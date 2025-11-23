// Fichier : src/main/java/com/moscepa/dto/ChapitreContenuDto.java (Nouveau Fichier)

package com.moscepa.dto;

import com.moscepa.entity.Chapitre;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO spécifique pour la gestion de contenu.
 * Il représente un chapitre avec la liste complète de ses sections.
 */
public class ChapitreContenuDto {
    private Long id;
    private String nom;
    private String objectif;
    private Integer ordre;
    private List<SectionDto> sections; // La liste des sections est l'élément clé

    // Constructeur vide
    public ChapitreContenuDto() {}

    // Constructeur de conversion depuis l'entité Chapitre
    public ChapitreContenuDto(Chapitre chapitre) {
        this.id = chapitre.getId();
        this.nom = chapitre.getNom();
        this.objectif = chapitre.getObjectif();
        this.ordre = chapitre.getOrdre();
        
        // On convertit la liste des entités Section en une liste de SectionDto
        if (chapitre.getSections() != null) {
            this.sections = chapitre.getSections().stream()
                                    .map(SectionDto::new) // Utilise le constructeur de SectionDto(Section section)
                                    .collect(Collectors.toList());
        }
    }

    // --- Getters et Setters ---
    // (Générez tous les getters et setters pour les champs ci-dessus)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getObjectif() { return objectif; }
    public void setObjectif(String objectif) { this.objectif = objectif; }
    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }
    public List<SectionDto> getSections() { return sections; }
    public void setSections(List<SectionDto> sections) { this.sections = sections; }
}
