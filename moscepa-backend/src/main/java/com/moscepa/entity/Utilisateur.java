// Fichier : src/main/java/com/moscepa/entity/Utilisateur.java (Version Finale et Corrigée)

package com.moscepa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "moscepa_utilisateurs")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Informations de base ---
    @NotBlank @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nom;

    @NotBlank @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String prenom;

    @NotBlank @Email @Size(max = 255)
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private Boolean actif = true;

    // --- Informations Personnelles Détaillées ---
    @Column(name = "date_de_naissance")
    private String dateDeNaissance;

    @Column(name = "lieu_de_naissance")
    private String lieuDeNaissance;

    private String nationalite;
    private String sexe;
    private String adresse;
    private String telephone;

    // --- Informations Académiques ---
    @Column(name = "annee_academique")
    private String anneeAcademique;

    private String filiere;
    
    // La relation @ManyToMany vers les matières a été correctement supprimée.

    // --- Timestamps ---
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateModification;
    
    // --- Permissions (CORRIGÉ) ---
    @ManyToMany(fetch = FetchType.LAZY) // <-- L'annotation est maintenant correctement présente
    @JoinTable(
        name = "moscepa_utilisateur_permissions",
        joinColumns = @JoinColumn(name = "utilisateur_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    // --- Constructeurs ---
    public Utilisateur() {}

    // --- Getters et Setters ---
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }
    public String getDateDeNaissance() { return dateDeNaissance; }
    public void setDateDeNaissance(String dateDeNaissance) { this.dateDeNaissance = dateDeNaissance; }
    public String getLieuDeNaissance() { return lieuDeNaissance; }
    public void setLieuDeNaissance(String lieuDeNaissance) { this.lieuDeNaissance = lieuDeNaissance; }
    public String getNationalite() { return nationalite; }
    public void setNationalite(String nationalite) { this.nationalite = nationalite; }
    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getAnneeAcademique() { return anneeAcademique; }
    public void setAnneeAcademique(String anneeAcademique) { this.anneeAcademique = anneeAcademique; }
    public String getFiliere() { return filiere; }
    public void setFiliere(String filiere) { this.filiere = filiere; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }
    public Set<Permission> getPermissions() { return permissions; }
    public void setPermissions(Set<Permission> permissions) { this.permissions = permissions; }
}
