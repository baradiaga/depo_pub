package com.moscepa.dto;

import com.moscepa.entity.Chapitre;
import com.moscepa.entity.Section;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO pour afficher les détails complets d'un chapitre.
 * Cette version est mise à jour pour utiliser ElementConstitutif.
 */
public class ChapitreDetailDto {

    private Long id;
    private String nom; // On utilise 'nom' pour être cohérent avec l'entité
    private String elementConstitutifNom;
    private Long elementConstitutifId;
    private Integer niveau;
    private String objectif;
    private List<SectionDto> sections;

    /**
     * DTO interne pour représenter une section.
     */
    public static class SectionDto {
        private Long id;
        private String titre;
        private String contenu;

        public SectionDto(Section section) {
            this.id = section.getId();
            this.titre = section.getTitre();
            this.contenu = section.getContenu();
        }
        // Getters
        public Long getId() { return id; }
        public String getTitre() { return titre; }
        public String getContenu() { return contenu; }
    }

    /**
     * Constructeur qui convertit une entité Chapitre en ce DTO.
     */
    public ChapitreDetailDto(Chapitre chapitre) {
        this.id = chapitre.getId();
        this.nom = chapitre.getNom();
        this.niveau = chapitre.getNiveau();
        this.objectif = chapitre.getObjectif();
        
        // ====================================================================
        // === CORRECTION APPLIQUÉE ICI                                     ===
        // ====================================================================
        // On utilise la nouvelle relation avec ElementConstitutif
        if (chapitre.getElementConstitutif() != null) {
            this.elementConstitutifNom = chapitre.getElementConstitutif().getNom();
            this.elementConstitutifId = chapitre.getElementConstitutif().getId();
        }

        this.sections = chapitre.getSections().stream()
                .map(SectionDto::new)
                .collect(Collectors.toList());
    }

    // --- Getters ---
    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getElementConstitutifNom() { return elementConstitutifNom; }
    public Long getElementConstitutifId() { return elementConstitutifId; }
    public Integer getNiveau() { return niveau; }
    public String getObjectif() { return objectif; }
    public List<SectionDto> getSections() { return sections; }
}
