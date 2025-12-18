package com.moscepa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "moscepa_departements")
public class Departement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    @Column(nullable = false, length = 100)
    private String nom;
    
    @NotBlank(message = "Le sigle est obligatoire")
    @Size(min = 2, max = 10, message = "Le sigle doit contenir entre 2 et 10 caractères")
    @Column(unique = true, nullable = false, length = 10)
    private String sigle;
    
    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 200, message = "L'adresse ne doit pas dépasser 200 caractères")
    @Column(nullable = false, length = 200)
    private String adresse;
    
    @NotBlank(message = "Le contact est obligatoire")
    @Size(max = 50, message = "Le contact ne doit pas dépasser 50 caractères")
    @Column(nullable = false, length = 50)
    private String contact;
    
    @Column(length = 255)
    private String logo;
    
    @Column(length = 255)
    private String lien;
    
    // Relation avec UEFR (gardée pour JPA)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uefr_id", nullable = false)
    @NotNull(message = "L'UEFR est obligatoire")
    private Uefr uefr;
    
    // AJOUTÉ ICI SEULEMENT - Relation avec Formation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id")
    private Formation formation;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Getters et Setters EXISTANTS + AJOUTEZ ceux pour formation
    
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
    
    public Uefr getUefr() { return uefr; }
    public void setUefr(Uefr uefr) { this.uefr = uefr; }
    
    // AJOUTÉ ICI SEULEMENT
    public Formation getFormation() { return formation; }
    public void setFormation(Formation formation) { this.formation = formation; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}