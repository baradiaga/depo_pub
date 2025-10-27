package com.moscepa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

/**
 * Entité représentant une permission dans le système MOSCEPA
 */
@Entity
@Table(name = "moscepa_permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de la permission est obligatoire")
    @Size(max = 100)
    @Column(name = "nom", nullable = false, unique = true, length = 100)
    private String nom;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Utilisateur> utilisateurs = new HashSet<>();

    // Constructeurs
    public Permission() {}

    public Permission(String nom, String description) {
        this.nom = nom;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Utilisateur> getUtilisateurs() {
        return utilisateurs;
    }

    public void setUtilisateurs(Set<Utilisateur> utilisateurs) {
        this.utilisateurs = utilisateurs;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

