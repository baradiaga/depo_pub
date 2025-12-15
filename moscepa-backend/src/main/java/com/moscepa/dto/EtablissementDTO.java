package com.moscepa.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class EtablissementDTO {
    
    private Long id;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;
    
    @NotBlank(message = "Le sigle est obligatoire")
    @Size(min = 2, max = 20, message = "Le sigle doit contenir entre 2 et 20 caractères")
    private String sigle;
    
    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 200, message = "L'adresse ne doit pas dépasser 200 caractères")
    private String adresse;
    
    @NotBlank(message = "Le contact est obligatoire")
    @Size(max = 100, message = "Le contact ne doit pas dépasser 100 caractères")
    private String contact;
    
    private String logo;
    
    private String lien;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // Constructeurs
    public EtablissementDTO() {}
    
    public EtablissementDTO(Long id, String nom, String sigle, String adresse, 
                           String contact, String logo, String lien,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nom = nom;
        this.sigle = sigle;
        this.adresse = adresse;
        this.contact = contact;
        this.logo = logo;
        this.lien = lien;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Constructeur simplifié
    public EtablissementDTO(String nom, String sigle, String adresse, String contact) {
        this.nom = nom;
        this.sigle = sigle;
        this.adresse = adresse;
        this.contact = contact;
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
    
    public String getSigle() {
        return sigle;
    }
    
    public void setSigle(String sigle) {
        this.sigle = sigle;
    }
    
    public String getAdresse() {
        return adresse;
    }
    
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    
    public String getContact() {
        return contact;
    }
    
    public void setContact(String contact) {
        this.contact = contact;
    }
    
    public String getLogo() {
        return logo;
    }
    
    public void setLogo(String logo) {
        this.logo = logo;
    }
    
    public String getLien() {
        return lien;
    }
    
    public void setLien(String lien) {
        this.lien = lien;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "EtablissementDTO{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", sigle='" + sigle + '\'' +
                ", adresse='" + adresse + '\'' +
                ", contact='" + contact + '\'' +
                ", logo='" + logo + '\'' +
                ", lien='" + lien + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}