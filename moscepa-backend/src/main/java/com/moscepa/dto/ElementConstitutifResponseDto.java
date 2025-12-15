// Fichier : ElementConstitutifResponseDto.java (Version Mise à Jour)

package com.moscepa.dto;

import java.util.List; // <-- NOUVEL IMPORT

/**
 * DTO pour renvoyer les informations d'un Élément Constitutif au client.
 */
public class ElementConstitutifResponseDto {

    private Long id;
    private String nom;
    private String code;
    private String description;
    private int credit;
    private EnseignantDto enseignant; // Suppose que vous avez déjà ce DTO
    private Integer volumeHoraireCours;
    private Integer volumeHoraireTD;
    private Integer volumeHoraireTP;

    // ====================================================================
    // === CHAMP MANQUANT AJOUTÉ ICI                                    ===
    // ====================================================================
    private List<ChapitreDto> chapitres; // Pour contenir la liste des chapitres

    // --- Getters et Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCredit() { return credit; }
    public void setCredit(int credit) { this.credit = credit; }

    public EnseignantDto getEnseignant() { return enseignant; }
    public void setEnseignant(EnseignantDto enseignant) { this.enseignant = enseignant; }

    // --- GETTER ET SETTER POUR LE NOUVEAU CHAMP ---
    public List<ChapitreDto> getChapitres() { return chapitres; }
    public void setChapitres(List<ChapitreDto> chapitres) { this.chapitres = chapitres; }
    public Integer getVolumeHoraireCours() { return volumeHoraireCours; }
    public void setVolumeHoraireCours(Integer volumeHoraireCours) { this.volumeHoraireCours = volumeHoraireCours; }

    public Integer getVolumeHoraireTD() { return volumeHoraireTD; }
    public void setVolumeHoraireTD(Integer volumeHoraireTD) { this.volumeHoraireTD = volumeHoraireTD; }

    public Integer getVolumeHoraireTP() { return volumeHoraireTP; }
    public void setVolumeHoraireTP(Integer volumeHoraireTP) { this.volumeHoraireTP = volumeHoraireTP; }
}
