package com.moscepa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "moscepa_unites_enseignement")
public class UniteEnseignement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom ne peut pas être vide")
    @Column(nullable = false)
    private String nom;

    @NotBlank(message = "Le code ne peut pas être vide")
    @Column(nullable = false)
    private String code;

    @Lob
    private String description;

    @Positive(message = "Le nombre de crédits (ECTS) doit être positif")
    @Column(nullable = false)
    private Integer ects; // Renommé de 'credit' à 'ects'

    @Column(nullable = false)
    private Integer semestre; // Conservé

    @Lob
    private String objectifs; // Conservé

    // Nouveaux champs pour la structure académique détaillée
    @Column(nullable = false)
    private Integer volumeHoraireCours = 0;
    @Column(nullable = false)
    private Integer volumeHoraireTD = 0;
    @Column(nullable = false)
    private Integer volumeHoraireTP = 0;

    // Relation ManyToOne vers Formation (Nouveau - Lien parent)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    // Relation ManyToOne vers Utilisateur (Responsable - Conservé)
    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "responsable_id")
    private Utilisateur responsable;

    // Relation ManyToMany avec ElementConstitutif (Matières) (Nouveau)
    @ManyToMany
    @JoinTable(
        name = "moscepa_ue_element_constitutif",
        joinColumns = @JoinColumn(name = "ue_id"),
        inverseJoinColumns = @JoinColumn(name = "element_constitutif_id")
    )
    private List<ElementConstitutif> elementsConstitutifs = new ArrayList<>();
    @Column(name = "annee_cycle")
    private Integer anneeCycle ;

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getEcts() { return ects; }
    public void setEcts(Integer ects) { this.ects = ects; }
    public Integer getSemestre() { return semestre; }
    public void setSemestre(Integer semestre) { this.semestre = semestre; }
    public String getObjectifs() { return objectifs; }
    public void setObjectifs(String objectifs) { this.objectifs = objectifs; }
    public Integer getVolumeHoraireCours() { return volumeHoraireCours; }
    public void setVolumeHoraireCours(Integer volumeHoraireCours) { this.volumeHoraireCours = volumeHoraireCours; }
    public Integer getVolumeHoraireTD() { return volumeHoraireTD; }
    public void setVolumeHoraireTD(Integer volumeHoraireTD) { this.volumeHoraireTD = volumeHoraireTD; }
    public Integer getVolumeHoraireTP() { return volumeHoraireTP; }
    public void setVolumeHoraireTP(Integer volumeHoraireTP) { this.volumeHoraireTP = volumeHoraireTP; }
    public Formation getFormation() { return formation; }
    public void setFormation(Formation formation) { this.formation = formation; }
    public Utilisateur getResponsable() { return responsable; }
    public void setResponsable(Utilisateur responsable) { this.responsable = responsable; }
    public List<ElementConstitutif> getElementsConstitutifs() { return elementsConstitutifs; }
    public void setElementsConstitutifs(List<ElementConstitutif> elementsConstitutifs) { this.elementsConstitutifs = elementsConstitutifs; }
    public Integer getAnneeCycle() { return anneeCycle; }
    public void setAnneeCycle(Integer anneeCycle) { this.anneeCycle = anneeCycle; }
}
