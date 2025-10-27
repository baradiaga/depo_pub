package com.moscepa.dto;

import com.moscepa.entity.Role;
import java.time.LocalDateTime;

/**
 * DTO pour la réponse contenant les informations d'un utilisateur
 * Utilisé pour retourner les données utilisateur sans le mot de passe
 */
public class UserResponseDto {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private Role role;
    private Boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    // Constructeurs
    public UserResponseDto() {}

    public UserResponseDto(Long id, String nom, String prenom, String email, Role role, Boolean actif, 
                          LocalDateTime dateCreation, LocalDateTime dateModification) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
        this.actif = actif;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
    }

    // Getters et Setters
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    @Override
    public String toString() {
        return "UserResponseDto{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", actif=" + actif +
                ", dateCreation=" + dateCreation +
                '}';
    }
}

