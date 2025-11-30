// Fichier : src/main/java/com/moscepa/dto/FormationDetailDto.java

package com.moscepa.dto;

import com.moscepa.entity.ElementConstitutif;
import com.moscepa.entity.Formation;
import com.moscepa.entity.NiveauEtude;
import com.moscepa.entity.StatutFormation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class FormationDetailDto {

    private Long id;
    private String nom;
    private String code;
    private String description;
    private StatutFormation statut;
    private NiveauEtude niveauEtude;
    private Integer dureeAnnees;
    private Long responsableId;
    private String responsableNom; // Nom complet du responsable
    private LocalDateTime dateCreation;
    private Long createurId;
    private String createurNom; // Nom complet du créateur
    private List<ElementConstitutifDto> elementsConstitutifs;

    // Constructeur à partir de l'entité Formation
    public FormationDetailDto(Formation formation) {
        this.id = formation.getId();
        this.nom = formation.getNom();
        this.code = formation.getCode();
        this.description = formation.getDescription();
        this.statut = formation.getStatut();
        this.niveauEtude = formation.getNiveauEtude();
        this.dureeAnnees = formation.getDuree();
        this.dateCreation = formation.getDateCreation();

        if (formation.getResponsablePedagogique() != null) {
            this.responsableId = formation.getResponsablePedagogique().getId();
            this.responsableNom = formation.getResponsablePedagogique().getPrenom() + " " + formation.getResponsablePedagogique().getNom();
        }

        if (formation.getCreateur() != null) {
            this.createurId = formation.getCreateur().getId();
            this.createurNom = formation.getCreateur().getPrenom() + " " + formation.getCreateur().getNom();
        }

        if (formation.getElementsConstitutifs() != null) {
            this.elementsConstitutifs = formation.getElementsConstitutifs().stream()
                    .map(ElementConstitutifDto::new)
                    .collect(Collectors.toList());
        }
    }

    // Getters (Les Setters ne sont généralement pas nécessaires pour un DTO de détail)
    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
    public StatutFormation getStatut() { return statut; }
    public NiveauEtude getNiveauEtude() { return niveauEtude; }
    public Integer getDureeAnnees() { return dureeAnnees; }
    public Long getResponsableId() { return responsableId; }
    public String getResponsableNom() { return responsableNom; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public Long getCreateurId() { return createurId; }
    public String getCreateurNom() { return createurNom; }
    public List<ElementConstitutifDto> getElementsConstitutifs() { return elementsConstitutifs; }

    // DTO interne pour les éléments constitutifs (matières)
    public static class ElementConstitutifDto {
        private Long id;
        private String nom;
        private String code;

        public ElementConstitutifDto(ElementConstitutif element) {
            this.id = element.getId();
            this.nom = element.getNom();
            this.code = element.getCode();
        }

        public Long getId() { return id; }
        public String getNom() { return nom; }
        public String getCode() { return code; }
    }
}
