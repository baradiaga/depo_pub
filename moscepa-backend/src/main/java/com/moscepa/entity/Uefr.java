package com.moscepa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "moscepa_uefrs")
public class Uefr {
    
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
    
    // Relation avec Etablissement (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etablissement_id", nullable = false)
    @NotNull(message = "L'établissement est obligatoire")
    private Etablissement etablissement;
    
    // Relation inverse avec Départements
    @OneToMany(mappedBy = "uefr", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Departement> departements = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructeurs
    public Uefr() {}
    
    public Uefr(Long id, String nom, String sigle, String adresse, 
                String contact, String logo, String lien, Etablissement etablissement,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nom = nom;
        this.sigle = sigle;
        this.adresse = adresse;
        this.contact = contact;
        this.logo = logo;
        this.lien = lien;
        this.etablissement = etablissement;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
    
    public Etablissement getEtablissement() {
        return etablissement;
    }
    
    public void setEtablissement(Etablissement etablissement) {
        this.etablissement = etablissement;
    }
    
    public List<Departement> getDepartements() {
        return departements;
    }
    
    public void setDepartements(List<Departement> departements) {
        this.departements = departements;
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
        return "Uefr{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", sigle='" + sigle + '\'' +
                ", adresse='" + adresse + '\'' +
                ", contact='" + contact + '\'' +
                ", logo='" + logo + '\'' +
                ", lien='" + lien + '\'' +
                ", etablissementId=" + (etablissement != null ? etablissement.getId() : "null") +
                ", departementsCount=" + (departements != null ? departements.size() : 0) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}