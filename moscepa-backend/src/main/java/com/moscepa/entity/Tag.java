// Fichier : src/main/java/com/moscepa/entity/Tag.java

package com.moscepa.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "moscepa_tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<BanqueQuestion> questions = new HashSet<>();

    // Constructeurs
    public Tag() {}
    public Tag(String nom) { this.nom = nom; }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Set<BanqueQuestion> getQuestions() { return questions; }
    public void setQuestions(Set<BanqueQuestion> questions) { this.questions = questions; }
}
