// Fichier : src/main/java/com/moscepa/dto/RessourceTeleversementDto.java

package com.moscepa.dto;

import java.util.Set;

/**
 * DTO pour les métadonnées envoyées avec le fichier lors du téléversement.
 */
public class RessourceTeleversementDto {

    private String titre;
    private String description;
    private Long chapitreId;
    private Set<String> tags;

    // Getters et Setters
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getChapitreId() { return chapitreId; }
    public void setChapitreId(Long chapitreId) { this.chapitreId = chapitreId; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }
}
