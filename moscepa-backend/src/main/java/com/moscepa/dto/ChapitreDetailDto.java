package com.moscepa.dto;

import com.moscepa.entity.Chapitre;
import com.moscepa.entity.Section;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO pour afficher les détails complets d'un chapitre.
 */
public class ChapitreDetailDto {

    private Long id;
    private String titre;
    private String matiereNom;
    private Integer niveau;
    private String objectif;
    private List<SectionDto> sections;

    // ==========================================================
    // ===> CHAMP AJOUTÉ POUR RÉSOUDRE LE PROBLÈME <====
    // ==========================================================
    private Long matiereId;

    /**
     * DTO interne pour représenter une section.
     */
    public static class SectionDto {
        // ... (votre code de SectionDto reste inchangé)
        private Long id;
        private String titre;
        private String contenu;
        public SectionDto(Section section) { this.id = section.getId(); this.titre = section.getTitre(); this.contenu = section.getContenu(); }
        public Long getId() { return id; }
        public String getTitre() { return titre; }
        public String getContenu() { return contenu; }
    }

    /**
     * Constructeur qui convertit une entité Chapitre en ce DTO.
     */
    public ChapitreDetailDto(Chapitre chapitre) {
        this.id = chapitre.getId();
        this.titre = chapitre.getNom();
        this.niveau = chapitre.getNiveau();
        this.objectif = chapitre.getObjectif();
        
        // On vérifie que la matière n'est pas nulle avant d'y accéder
        if (chapitre.getMatiere() != null) {
            this.matiereNom = chapitre.getMatiere().getNom();
            // On récupère l'ID de la matière ici
            this.matiereId = chapitre.getMatiere().getId(); 
        }

        this.sections = chapitre.getSections().stream()
                .map(SectionDto::new)
                .collect(Collectors.toList());
    }

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public String getTitre() { return titre; }
    public String getMatiereNom() { return matiereNom; }
    public Integer getNiveau() { return niveau; }
    public String getObjectif() { return objectif; }
    public List<SectionDto> getSections() { return sections; }

    // ==========================================================
    // ===> GETTER AJOUTÉ POUR LE NOUVEAU CHAMP <====
    // ==========================================================
    public Long getMatiereId() {
        return matiereId;
    }
}
