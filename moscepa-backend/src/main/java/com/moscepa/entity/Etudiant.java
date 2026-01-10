package com.moscepa.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity // AJOUTÉ : Indispensable pour créer la table
@Table(name = "moscepa_etudiants")
public class Etudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liaison avec le compte utilisateur (nom, email, mot de passe)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    // REMPLACÉ : On n'utilise plus String filiere, mais l'objet Formation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    @Column(name = "numero_matricule", unique = true, length = 20)
    private String numeroMatricule;

    @Column(name = "date_de_naissance")
    private LocalDate dateDeNaissance;

    @Column(name = "lieu_de_naissance", length = 100)
    private String lieuDeNaissance;

    @Column(name = "nationalite", length = 50)
    private String nationalite;

    @Column(name = "sexe", length = 10)
    private String sexe;

    @Column(name = "adresse", length = 255)
    private String adresse;

    @Column(name = "telephone", length = 20)
    private String telephone;

    @Column(name = "annee_academique", length = 10)
    private String anneeAcademique;

    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Inscription> inscriptions = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "date_inscription", nullable = false, updatable = false)
    private LocalDateTime dateInscription;

    // Constructeurs
    public Etudiant() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public Formation getFormation() { return formation; }
    public void setFormation(Formation formation) { this.formation = formation; }
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
    public List<Inscription> getInscriptions() { return inscriptions; }
    public void setInscriptions(List<Inscription> inscriptions) { this.inscriptions = inscriptions; }
}
