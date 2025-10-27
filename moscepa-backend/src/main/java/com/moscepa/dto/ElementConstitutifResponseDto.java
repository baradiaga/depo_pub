package com.moscepa.dto;

// Ce DTO contient les informations complètes à afficher
public class ElementConstitutifResponseDto {

    private Long id;
    private String nom;
    private String code;
    private String description;
    private Integer credit;

    // LA CORRECTION EST ICI : On envoie un objet complet, pas juste un ID
    private EnseignantDto enseignant;

    // On pourrait aussi ajouter les infos de l'UE si nécessaire
    // private UniteEnseignementSimpleDto uniteEnseignement;

    // Getters et Setters pour tous les champs...
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public EnseignantDto getEnseignant() {
        return enseignant;
    }

    public void setEnseignant(EnseignantDto enseignant) {
        this.enseignant = enseignant;
    }
}
