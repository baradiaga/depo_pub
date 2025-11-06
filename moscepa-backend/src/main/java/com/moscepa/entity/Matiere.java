package com.moscepa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "moscepa_matieres")
public class Matiere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de la matière est obligatoire")
    @Size(max = 100)
    @Column(name = "nom", nullable = false, length = 100)
    private String nom; // Ex: "Mathématiques"

    @Column(name = "ec", length = 100)
    private String ec; // Ex: "Algèbre Linéaire"

    // ==================== CORRECTION N°1 ====================
    // 'ordre' est maintenant un Integer pour accepter les valeurs NULL de la base de données.
    @Column(name = "ordre")
    private Integer ordre;

    // ==================== CORRECTION N°2 ====================
    // 'coefficient' est maintenant un Double pour accepter les valeurs NULL et les décimales.
    @Column(name = "coefficient")
    private Double coefficient;
    // ========================================================

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;


    // Constructeurs
    public Matiere() {}
    
    // ==================== CORRECTION N°3 ====================
    // Getters et Setters mis à jour pour correspondre aux nouveaux types (Integer, Double).
    // ========================================================
    
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

    public String getEc() { 
        return ec; 
    }
    public void setEc(String ec) { 
        this.ec = ec; 
    }

    public Integer getOrdre() { // <-- Retourne Integer
        return ordre; 
    }
    public void setOrdre(Integer ordre) { // <-- Accepte Integer
        this.ordre = ordre; 
    }

    public Double getCoefficient() { // <-- Retourne Double
        return coefficient; 
    }
    public void setCoefficient(Double coefficient) { // <-- Accepte Double
        this.coefficient = coefficient; 
    }

    public String getDescription() { 
        return description; 
    }
    public void setDescription(String description) { 
        this.description = description; 
    }


}
