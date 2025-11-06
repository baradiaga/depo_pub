// Fichier : src/main/java/com/moscepa/dto/EtudiantRegistrationDto.java (Nouveau fichier)

package com.moscepa.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class EtudiantRegistrationDto {

    @NotBlank private String nom;
    @NotBlank private String prenom;
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 6) private String motDePasse;
    @NotBlank private String dateDeNaissance;
    @NotBlank private String lieuDeNaissance;
    @NotBlank private String nationalite;
    @NotBlank private String sexe;
    @NotBlank private String adresse;
    @NotBlank private String telephone;
    @NotBlank private String anneeAcademique;
    @NotBlank private String filiere;
    
    
    @NotNull @Size(min = 1, message = "L'étudiant doit être inscrit à au moins une matière.")
    private List<Long> matiereIds;

    // Générez tous les Getters et Setters ici...
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
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
    public List<Long> getMatiereIds() { return matiereIds; }
    public void setMatiereIds(List<Long> matiereIds) { this.matiereIds = matiereIds; }
}
