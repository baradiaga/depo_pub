// Fichier : com/moscepa/dto/ChapitreCreateDto.java (Version Finale et Complète)

package com.moscepa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Data Transfer Object pour la création d'un nouveau Chapitre.
 * Contient les données envoyées par le client (Angular).
 */
public class ChapitreCreateDto {

    /**
     * Le nom du chapitre. Ne doit pas être vide.
     */
    @NotBlank(message = "Le nom du chapitre est obligatoire.")
    @Size(max = 255, message = "Le nom du chapitre ne doit pas dépasser 255 caractères.")
    public String nom;

    /**
     * L'objectif pédagogique du chapitre. Peut être vide.
     */
    public String objectif;

    /**
     * Niveau du chapitre (nombre de sections initial).
     */
    public Integer niveau;

    /**
     * Liste des sections du chapitre.
     */
    public List<SectionCreateDto> sections;

    /**
     * Type d'activité pédagogique (TP, TD, Cours, Autre).
     */
    @Size(max = 255, message = "Le type d'activité ne doit pas dépasser 255 caractères.")
    public String typeActivite;

    /**
     * Prérequis pour aborder ce chapitre.
     */
    @Size(max = 2000, message = "Les prérequis ne doivent pas dépasser 2000 caractères.")
    public String prerequis;

    /**
     * Type d'évaluation (Quiz, Exercice, Autre type pertinent, Autre).
     */
    @Size(max = 255, message = "Le type d'évaluation ne doit pas dépasser 255 caractères.")
    public String typeEvaluation;

    // NOTE : Nous n'avons pas besoin de l'ID de la matière ici,
    // car il est déjà passé dans l'URL de la requête (ex: /api/matieres/6/chapitres).
}