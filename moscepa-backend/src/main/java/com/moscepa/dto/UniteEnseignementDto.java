package com.moscepa.dto;

import com.moscepa.entity.UniteEnseignement;
import java.util.List;
import java.util.stream.Collectors;

public class UniteEnseignementDto {
    private Long id;
    private String nom;
    private Long formationId; // Déjà présent
    private String code;
    private String description;
    private Integer ects;
    private Integer semestre;
    private String objectifs;
    private Integer volumeHoraireCours;
    private Integer volumeHoraireTD;
    private Integer volumeHoraireTP;
    private Long responsableId;
    private List<Long> elementConstitutifIds;
      private Integer anneeCycle; 
    // Constructeur pour la création (utilisé par Spring pour le mapping JSON)
    public UniteEnseignementDto() {}

    // Constructeur pour le mappage depuis l'Entité (utilisé par le Service)
    public UniteEnseignementDto(UniteEnseignement entity) {
        this.id = entity.getId();
        this.nom = entity.getNom();
        this.code = entity.getCode();
        this.description = entity.getDescription();
        this.ects = entity.getEcts();
        this.semestre = entity.getSemestre();
        this.objectifs = entity.getObjectifs();
        this.volumeHoraireCours = entity.getVolumeHoraireCours();
        this.volumeHoraireTD = entity.getVolumeHoraireTD();
        this.volumeHoraireTP = entity.getVolumeHoraireTP();
         this.anneeCycle = entity.getAnneeCycle();
        
        // CORRECTION : Récupérer l'ID de la formation
        if (entity.getFormation() != null) {
            this.formationId = entity.getFormation().getId();
        }
        
        if (entity.getResponsable() != null) {
            this.responsableId = entity.getResponsable().getId();
        }
        
        this.elementConstitutifIds = entity.getElementsConstitutifs().stream()
            .map(ec -> ec.getId())
            .collect(Collectors.toList());
    }

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getEcts() { return ects; }
    public void setEcts(Integer ects) { this.ects = ects; }
    public Integer getSemestre() { return semestre; }
    public void setSemestre(Integer semestre) { this.semestre = semestre; }
    public String getObjectifs() { return objectifs; }
    public void setObjectifs(String objectifs) { this.objectifs = objectifs; }
    public Integer getVolumeHoraireCours() { return volumeHoraireCours; }
    public void setVolumeHoraireCours(Integer volumeHoraireCours) { this.volumeHoraireCours = volumeHoraireCours; }
    public Integer getVolumeHoraireTD() { return volumeHoraireTD; }
    public void setVolumeHoraireTD(Integer volumeHoraireTD) { this.volumeHoraireTD = volumeHoraireTD; }
    public Integer getVolumeHoraireTP() { return volumeHoraireTP; }
    public void setVolumeHoraireTP(Integer volumeHoraireTP) { this.volumeHoraireTP = volumeHoraireTP; }
    public Long getResponsableId() { return responsableId; }
    public void setResponsableId(Long responsableId) { this.responsableId = responsableId; }
    public List<Long> getElementConstitutifIds() { return elementConstitutifIds; }
    public void setElementConstitutifIds(List<Long> elementConstitutifIds) { this.elementConstitutifIds = elementConstitutifIds; }
    public Long getFormationId() { return formationId; }
    public void setFormationId(Long formationId) { this.formationId = formationId; }
    public Integer getAnneeCycle() { return anneeCycle; }
    public void setAnneeCycle(Integer anneeCycle) { this.anneeCycle = anneeCycle; }
}