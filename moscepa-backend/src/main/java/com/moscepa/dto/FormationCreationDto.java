// Fichier : src/main/java/com/moscepa/dto/FormationCreationDto.java

package com.moscepa.dto;

import com.moscepa.entity.NiveauEtude;
import com.moscepa.entity.StatutFormation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
    private NiveauEtude niveauEtude; // Cycle : LICENCE, MASTER, CERTIFICAT, DOCTORAT

    @NotNull
    private Integer annee; // Année dans le cycle (1,2,3...)

    @NotNull
    private Integer duree; // Durée en années

    private Long responsableId;

    private List<Long> elementsConstitutifsIds;
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

    public Integer getAnnee() { return annee; }
    public void setAnnee(Integer annee) { this.annee = annee; }

    public Integer getDuree() { return duree; }
    public void setDuree(Integer duree) { this.duree = duree; }

    public Long getResponsableId() { return responsableId; }
    public void setResponsableId(Long responsableId) { this.responsableId = responsableId; }

    public List<Long> getElementsConstitutifsIds() { return elementsConstitutifsIds; }
    public void setElementsConstitutifsIds(List<Long> elementsConstitutifsIds) { this.elementsConstitutifsIds = elementsConstitutifsIds; }

    public List<Long> getIntervenantsIds() { return intervenantsIds; }
    public void setIntervenantsIds(List<Long> intervenantsIds) { this.intervenantsIds = intervenantsIds; }

    public List<Long> getDocumentsIds() { return documentsIds; }
    public void setDocumentsIds(List<Long> documentsIds) { this.documentsIds = documentsIds; }
}
