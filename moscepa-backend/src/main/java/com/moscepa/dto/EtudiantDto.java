// Fichier : src/main/java/com/moscepa/dto/EtudiantDto.java (Corrigé)

package com.moscepa.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class EtudiantDto {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String numeroMatricule;
    private LocalDate dateDeNaissance;
    private String lieuDeNaissance;
    private String nationalite;
    private String sexe;
    private String adresse;
    private String telephone;
    private String anneeAcademique;
    private String filiere;
    private List<Long> matiereIds;
    private Long enseignantId;
    private LocalDateTime dateInscription;
    private String statut;
    private String enseignantNom;

    // Getters et Setters pour tous les champs
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNumeroMatricule() { return numeroMatricule; }
    public void setNumeroMatricule(String numeroMatricule) { this.numeroMatricule = numeroMatricule; }
    public LocalDate getDateDeNaissance() { return dateDeNaissance; }
    public void setDateDeNaissance(LocalDate dateDeNaissance) { this.dateDeNaissance = dateDeNaissance; }
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
    public Long getEnseignantId() { return enseignantId; }
    public void setEnseignantId(Long enseignantId) { this.enseignantId = enseignantId; }
    public LocalDateTime getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDateTime dateInscription) { this.dateInscription = dateInscription; }
    public String getEnseignantNom() { return enseignantNom; }
    public void setEnseignantNom(String enseignantNom) { this.enseignantNom = enseignantNom; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
