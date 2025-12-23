// Fichier : src/main/java/com/moscepa/dto/SectionCreateDto.java

package com.moscepa.dto;

import com.moscepa.entity.TypeSection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SectionCreateDto {

    @NotBlank(message = "Le titre de la section est obligatoire.")
    private String titre;

    // Le contenu initial peut être vide
    private String contenu;

    @NotNull(message = "Le type de section est obligatoire.")
    private TypeSection typeSection;

    // Getters et Setters
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public TypeSection getTypeSection() { return typeSection; }
    public void setTypeSection(TypeSection typeSection) { this.typeSection = typeSection; }
}