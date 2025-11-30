// Fichier : src/main/java/com/moscepa/entity/EchelleConnaissance.java

package com.moscepa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "moscepa_echelles_connaissance")
public class EchelleConnaissance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String intervalle; // Ex: [0% - 34%]

    @Column(nullable = false)
    private String description; // Ex: Faible maîtrise

    @Column(nullable = false)
    private String recommandation; // Ex: Chapitre recommandé automatiquement

    // Constructeurs
    public EchelleConnaissance() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIntervalle() { return intervalle; }
    public void setIntervalle(String intervalle) { this.intervalle = intervalle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRecommandation() { return recommandation; }
    public void setRecommandation(String recommandation) { this.recommandation = recommandation; }
}
