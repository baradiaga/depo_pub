package com.moscepa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * DTO spécifique pour la création d'un chapitre depuis le formulaire Angular.
 * Il correspond exactement à la structure des données envoyées par le frontend.
 */
public class ChapitreCreateDto {

    @NotBlank(message = "La matière est obligatoire")
    private String matiere; // <-- C'est le nom de la matière (String)

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @NotNull(message = "Le niveau est obligatoire")
    private Integer niveau;

    private String objectif;

    private List<SectionDto> sections;

    // --- Getters et Setters ---

    public String getMatiere() { return matiere; }
    public void setMatiere(String matiere) { this.matiere = matiere; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public Integer getNiveau() { return niveau; }
    public void setNiveau(Integer niveau) { this.niveau = niveau; }

    public String getObjectif() { return objectif; }
    public void setObjectif(String objectif) { this.objectif = objectif; }

    public List<SectionDto> getSections() { return sections; }
    public void setSections(List<SectionDto> sections) { this.sections = sections; }

    /**
     * DTO imbriqué pour représenter une section.
     */
        public static class SectionDto {
            private String titre;
            private String contenu; // Ajout du contenu
    
            public String getTitre() { return titre; }
            public void setTitre(String titre) { this.titre = titre; }
    
            public String getContenu() { return contenu; }
            public void setContenu(String contenu) { this.contenu = contenu; }
        }
}