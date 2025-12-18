package com.moscepa.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class DepartementDTO {
    
    private Long id;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;
    
    @NotBlank(message = "Le sigle est obligatoire")
    @Size(min = 2, max = 10, message = "Le sigle doit contenir entre 2 et 10 caractères")
    private String sigle;
    
    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 200, message = "L'adresse ne doit pas dépasser 200 caractères")
    private String adresse;
    
    @NotBlank(message = "Le contact est obligatoire")
    @Size(max = 50, message = "Le contact ne doit pas dépasser 50 caractères")
    private String contact;
    
    private String logo;
    
    private String lien;
    
    @NotNull(message = "L'ID UEFR est obligatoire")
    @JsonProperty("uefrId")
    @JsonAlias("uefr_id")
    private Long uefrId;
    
    // AJOUTÉ ICI SEULEMENT
    private Long formationId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // Getters et Setters EXISTANTS + AJOUTEZ ceux pour formationId
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getSigle() { return sigle; }
    public void setSigle(String sigle) { this.sigle = sigle; }
    
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    
    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }
    
    public String getLien() { return lien; }
    public void setLien(String lien) { this.lien = lien; }
    
    public Long getUefrId() { return uefrId; }
    public void setUefrId(Long uefrId) { this.uefrId = uefrId; }
    
    // AJOUTÉ ICI SEULEMENT
    public Long getFormationId() { return formationId; }
    public void setFormationId(Long formationId) { this.formationId = formationId; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}