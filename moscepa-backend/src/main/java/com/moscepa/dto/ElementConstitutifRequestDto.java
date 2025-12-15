package com.moscepa.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ElementConstitutifRequestDto {

    // =======================================================
    // === CHAMP 'id' ET SES MÉTHODES AJOUTÉS CI-DESSOUS ===
    // =======================================================
    private Long id;

    @NotBlank(message = "Le nom ne peut pas être vide.")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères.")
    private String nom;

    @NotBlank(message = "Le code ne peut pas être vide.")
    @Size(min = 3, max = 20, message = "Le code doit contenir entre 3 et 20 caractères.")
    private String code;

    private String description;

    @NotNull(message = "Le nombre de crédits est obligatoire.")
    @Min(value = 0, message = "Le nombre de crédits ne peut pas être négatif.")
    private Integer credit;

    @NotNull(message = "L'enseignant responsable est obligatoire.")
    private Long enseignantId;
    // Volumes horaires
    @NotNull(message = "Le volume horaire de cours est obligatoire.")
    @Min(value = 0, message = "Le volume horaire ne peut pas être négatif.")
    private Integer volumeHoraireCours;

    @NotNull(message = "Le volume horaire de TD est obligatoire.")
    @Min(value = 0, message = "Le volume horaire ne peut pas être négatif.")
    private Integer volumeHoraireTD;

    @NotNull(message = "Le volume horaire de TP est obligatoire.")
    @Min(value = 0, message = "Le volume horaire ne peut pas être négatif.")
    private Integer volumeHoraireTP;

    // --- Getters et Setters ---

    // === GETTER ET SETTER POUR 'id' ===
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    // ===================================

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public Long getEnseignantId() {
        return enseignantId;
    }

    public void setEnseignantId(Long enseignantId) {
        this.enseignantId = enseignantId;
    }
     public Integer getVolumeHoraireCours() { return volumeHoraireCours; }
    public void setVolumeHoraireCours(Integer volumeHoraireCours) { this.volumeHoraireCours = volumeHoraireCours; }

    public Integer getVolumeHoraireTD() { return volumeHoraireTD; }
    public void setVolumeHoraireTD(Integer volumeHoraireTD) { this.volumeHoraireTD = volumeHoraireTD; }

    public Integer getVolumeHoraireTP() { return volumeHoraireTP; }
    public void setVolumeHoraireTP(Integer volumeHoraireTP) { this.volumeHoraireTP = volumeHoraireTP; }
}
