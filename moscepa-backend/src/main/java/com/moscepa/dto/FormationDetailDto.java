package com.moscepa.dto;

import com.moscepa.entity.ElementConstitutif;
import com.moscepa.entity.Formation;
import com.moscepa.entity.NiveauEtude;
import com.moscepa.entity.StatutFormation;
import com.moscepa.entity.UniteEnseignement;

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
    private String objectifs;
    private String prerequis;
    private String debouches;
    private String evaluationModalites;
    private String modaliteEnseignement;
    private String lieu;
    private String dateDebut; // String pour LocalDate
    private String dateFin;   // String pour LocalDate
    private Integer capacite;
    private Double tarif;
    private String certificationProfessionnelle;

    // ========================
    // NOUVEAUX CHAMPS - Références administratives
    // ========================
    private Long etablissementId;
    private String etablissementNom;
    
    private Long uefrId;
    private String uefrNom;
    
    private Long departementId;
    private String departementNom;

    private Integer volumeHoraireTotal; // Champ calculé
    private Integer ectsTotal;          // Champ calculé

    private List<CompetenceDetailDto> competences;
    private List<UniteEnseignementDto> unitesEnseignement;
    private Long responsableId;
    private String responsableNom; // Nom complet du responsable
    private LocalDateTime dateCreation;
    private Long createurId;
    private String createurNom; // Nom complet du créateur
    // private List<ElementConstitutifDto> elementsConstitutifs; // Retiré car géré par les UEs.
    private List<ElementConstitutifDto> elementsConstitutifs; // Conservé pour la compatibilité du DTO existant, mais sera vide ou géré différemment.

    // Constructeur à partir de l'entité Formation
    public FormationDetailDto(Formation formation) {
        // Calcul des totaux
        Integer totalVolume = formation.getUnitesEnseignement().stream()
            .mapToInt(ue -> ue.getVolumeHoraireCours() + ue.getVolumeHoraireTD() + ue.getVolumeHoraireTP())
            .sum();
        Integer totalEcts = formation.getUnitesEnseignement().stream()
            .mapToInt(UniteEnseignement::getEcts)
            .sum();

        this.volumeHoraireTotal = totalVolume;
        this.ectsTotal = totalEcts;
        this.id = formation.getId();
        this.nom = formation.getNom();
        this.code = formation.getCode();
        this.description = formation.getDescription();
        this.statut = formation.getStatut();
        this.niveauEtude = formation.getNiveauEtude();
        this.dureeAnnees = formation.getDuree();

        // Nouveaux champs
        this.objectifs = formation.getObjectifs();
        this.prerequis = formation.getPrerequis();
        this.debouches = formation.getDebouches();
        this.evaluationModalites = formation.getEvaluationModalites();
        this.modaliteEnseignement = formation.getModaliteEnseignement();
        this.lieu = formation.getLieu();
        this.dateDebut = formation.getDateDebut() != null ? formation.getDateDebut().toString() : null;
        this.dateFin = formation.getDateFin() != null ? formation.getDateFin().toString() : null;
        this.capacite = formation.getCapacite();
        this.tarif = formation.getTarif();
        this.certificationProfessionnelle = formation.getCertificationProfessionnelle();

        // NOUVEAUX CHAMPS - Références administratives
        if (formation.getEtablissement() != null) {
            this.etablissementId = formation.getEtablissement().getId();
            this.etablissementNom = formation.getEtablissement().getNom();
        }
        
        if (formation.getUefr() != null) {
            this.uefrId = formation.getUefr().getId();
            this.uefrNom = formation.getUefr().getNom();
        }
        
        if (formation.getDepartement() != null) {
            this.departementId = formation.getDepartement().getId();
            this.departementNom = formation.getDepartement().getNom();
        }

        // Relations imbriquées
        this.competences = formation.getCompetences().stream()
            .map(CompetenceDetailDto::new)
            .collect(Collectors.toList());
        this.unitesEnseignement = formation.getUnitesEnseignement().stream()
            .map(UniteEnseignementDto::new)
            .collect(Collectors.toList());
        this.dateCreation = formation.getDateCreation();

        if (formation.getResponsablePedagogique() != null) {
            this.responsableId = formation.getResponsablePedagogique().getId();
            this.responsableNom = formation.getResponsablePedagogique().getPrenom() + " " + formation.getResponsablePedagogique().getNom();
        }

        if (formation.getCreateur() != null) {
            this.createurId = formation.getCreateur().getId();
            this.createurNom = formation.getCreateur().getPrenom() + " " + formation.getCreateur().getNom();
        }

        // if (formation.getElementsConstitutifs() != null) {
        //     this.elementsConstitutifs = formation.getElementsConstitutifs().stream()
        //             .map(ElementConstitutifDto::new)
        //             .collect(Collectors.toList());
        // }
    }

    // Getters (Les Setters ne sont généralement pas nécessaires pour un DTO de détail)
    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
    public StatutFormation getStatut() { return statut; }
    public NiveauEtude getNiveauEtude() { return niveauEtude; }
    public Integer getDureeAnnees() { return dureeAnnees; }
    public String getObjectifs() { return objectifs; }
    public String getPrerequis() { return prerequis; }
    public String getDebouches() { return debouches; }
    public String getEvaluationModalites() { return evaluationModalites; }
    public String getModaliteEnseignement() { return modaliteEnseignement; }
    public String getLieu() { return lieu; }
    public String getDateDebut() { return dateDebut; }
    public String getDateFin() { return dateFin; }
    public Integer getCapacite() { return capacite; }
    public Double getTarif() { return tarif; }
    public String getCertificationProfessionnelle() { return certificationProfessionnelle; }
    
    // --- Getters pour les nouveaux champs administratifs ---
    public Long getEtablissementId() { return etablissementId; }
    public String getEtablissementNom() { return etablissementNom; }
    
    public Long getUefrId() { return uefrId; }
    public String getUefrNom() { return uefrNom; }
    
    public Long getDepartementId() { return departementId; }
    public String getDepartementNom() { return departementNom; }

    public Integer getVolumeHoraireTotal() { return volumeHoraireTotal; }
    public Integer getEctsTotal() { return ectsTotal; }
    public List<CompetenceDetailDto> getCompetences() { return competences; }
    public List<UniteEnseignementDto> getUnitesEnseignement() { return unitesEnseignement; }
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