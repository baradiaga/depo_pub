// Fichier : src/main/java/com/moscepa/dto/SectionDto.java (Version finale et compatible à remplacer)

package com.moscepa.dto;

import com.moscepa.entity.Section;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO représentant une Section.
 * Ce 'record' est maintenant compatible avec toutes les parties de l'application.
 */
public record SectionDto(
    Long id,
    @NotBlank(message = "Le titre ne peut pas être vide") String titre,
    String contenu,
    @NotNull(message = "L'ordre ne peut pas être nul") Integer ordre,
    Long chapitreId // Le 5ème champ est conservé pour la compatibilité avec SectionService
) {
    /**
     * Constructeur de conversion N°1 : Pour transformer une entité Section en ce DTO.
     * Utilisé par ChapitreDto et ElementConstitutifService.
     * @param section L'entité Section à convertir.
     */
    public SectionDto(Section section) {
        this(
            section.getId(), 
            section.getTitre(), 
            section.getContenu(), 
            section.getOrdre(),
            // On s'assure que le chapitre n'est pas null avant d'appeler getId()
            (section.getChapitre() != null) ? section.getChapitre().getId() : null
        );
    }

    /**
     * Constructeur N°2 : Le constructeur principal (canonique) du record.
     * Il est automatiquement généré par Java car les 5 champs sont déclarés ci-dessus.
     * C'est ce constructeur que votre SectionService.java utilise, et qui causait l'erreur.
     * En gardant les 5 champs dans la déclaration du record, ce constructeur est implicitement disponible.
     */
}
