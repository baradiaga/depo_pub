// Fichier : src/main/java/com/moscepa/dto/SectionDto.java

package com.moscepa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// On utilise un "record" pour un DTO simple et immuable.
// C'est la classe que le compilateur ne trouvait pas.
public record SectionDto(
    Long id,

    @NotBlank(message = "Le titre ne peut pas être vide")
    String titre,

    String contenu,

    @NotNull(message = "L'ordre ne peut pas être nul")
    Integer ordre,

    Long chapitreId
) {}
