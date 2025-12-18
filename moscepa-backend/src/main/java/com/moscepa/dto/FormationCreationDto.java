package com.moscepa.dto;

import com.moscepa.dto.CompetenceDetailDto;
import com.moscepa.entity.NiveauEtude;
import com.moscepa.entity.StatutFormation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public class FormationCreationDto {

    @NotBlank
    @Size(max = 200)
    private String nom;

    @NotBlank
    @Size(max = 50)
    private String code;

    private String description;

    @NotNull
    private StatutFormation statut;

    @NotNull
    private NiveauEtude niveauEtude;

    @NotNull
    private Integer duree;

    private Long responsableId;

    private String objectifs;
    private String prerequis;
    private String debouches;
    private String evaluationModalites;
    private String modaliteEnseignement;
    private String lieu;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Integer capacite;
    private Double tarif;
    private String certificationProfessionnelle;

    // ========================
    // NOUVEAUX CHAMPS - Références administratives
    // ========================
    @NotNull(message = "L'établissement est obligatoire")
    private Long etablissementId;

    @NotNull(message = "L'UFR est obligatoire")
    private Long uefrId;

    @NotNull(message = "Le département est obligatoire")
    private Long departementId;

    private List<CompetenceDetailDto> competences;
    private List<UniteEnseignementDto> unitesEnseignement;

    // L'ancien champ elementsConstitutifsIds est retiré car les EC sont gérés par les UEs.
    // private List<Long> elementsConstitutifsIds;
    private List<Long> intervenantsIds; // Optionnel
    private List<Long> documentsIds; // Optionnel

    // ========================
    // Getters et Setters
    // ========================
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public StatutFormation getStatut() { return statut; }
    public void setStatut(StatutFormation statut) { this.statut = statut; }

    public NiveauEtude getNiveauEtude() { return niveauEtude; }
    public void setNiveauEtude(NiveauEtude niveauEtude) { this.niveauEtude = niveauEtude; }

    public Integer getDuree() { return duree; }
    public void setDuree(Integer duree) { this.duree = duree; }

    public Long getResponsableId() { return responsableId; }
    public void setResponsableId(Long responsableId) { this.responsableId = responsableId; }

    public String getObjectifs() { return objectifs; }
    public void setObjectifs(String objectifs) { this.objectifs = objectifs; }
    public String getPrerequis() { return prerequis; }
    public void setPrerequis(String prerequis) { this.prerequis = prerequis; }
    public String getDebouches() { return debouches; }
    public void setDebouches(String debouches) { this.debouches = debouches; }
    public String getEvaluationModalites() { return evaluationModalites; }
    public void setEvaluationModalites(String evaluationModalites) { this.evaluationModalites = evaluationModalites; }
    public String getModaliteEnseignement() { return modaliteEnseignement; }
    public void setModaliteEnseignement(String modaliteEnseignement) { this.modaliteEnseignement = modaliteEnseignement; }
    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public Integer getCapacite() { return capacite; }
    public void setCapacite(Integer capacite) { this.capacite = capacite; }
    public Double getTarif() { return tarif; }
    public void setTarif(Double tarif) { this.tarif = tarif; }
    public String getCertificationProfessionnelle() { return certificationProfessionnelle; }
    public void setCertificationProfessionnelle(String certificationProfessionnelle) { this.certificationProfessionnelle = certificationProfessionnelle; }

    // --- Getters et Setters pour les nouveaux champs administratifs ---
    public Long getEtablissementId() { return etablissementId; }
    public void setEtablissementId(Long etablissementId) { this.etablissementId = etablissementId; }

    public Long getUefrId() { return uefrId; }
    public void setUefrId(Long uefrId) { this.uefrId = uefrId; }

    public Long getDepartementId() { return departementId; }
    public void setDepartementId(Long departementId) { this.departementId = departementId; }

    public List<CompetenceDetailDto> getCompetences() { return competences; }
    public void setCompetences(List<CompetenceDetailDto> competences) { this.competences = competences; }
    public List<UniteEnseignementDto> getUnitesEnseignement() { return unitesEnseignement; }
    public void setUnitesEnseignement(List<UniteEnseignementDto> unitesEnseignement) { this.unitesEnseignement = unitesEnseignement; }

    // L'ancien champ elementsConstitutifsIds est retiré car les EC sont gérés par les UEs.
    // public List<Long> getElementsConstitutifsIds() { return elementsConstitutifsIds; }
    // public void setElementsConstitutifsIds(List<Long> elementsConstitutifsIds) { this.elementsConstitutifsIds = elementsConstitutifsIds; }

    public List<Long> getIntervenantsIds() { return intervenantsIds; }
    public void setIntervenantsIds(List<Long> intervenantsIds) { this.intervenantsIds = intervenantsIds; }

    public List<Long> getDocumentsIds() { return documentsIds; }
    public void setDocumentsIds(List<Long> documentsIds) { this.documentsIds = documentsIds; }
}