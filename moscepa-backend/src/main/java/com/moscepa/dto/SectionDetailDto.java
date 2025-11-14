// Fichier : src/main/java/com/moscepa/dto/SectionDetailDto.java (NOUVEAU FICHIER)

package com.moscepa.dto;

import com.moscepa.entity.Section;

/**
 * DTO pour représenter les détails d'une section (id, titre, contenu, ordre).
 * Utilisé lorsque les sections sont imbriquées dans la réponse d'un chapitre.
 */
public class SectionDetailDto {

    private Long id;
    private String titre;
    private String contenu;
    private Integer ordre;

    // Constructeur vide, essentiel pour la sérialisation/désérialisation
    public SectionDetailDto() {}

    /**
     * Constructeur de conversion pour transformer facilement une entité Section en ce DTO.
     * @param section L'entité Section à convertir.
     */
    public SectionDetailDto(Section section) {
        this.id = section.getId();
        this.titre = section.getTitre();
        this.contenu = section.getContenu();
        this.ordre = section.getOrdre();
    }

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }
}
