// Fichier : src/main/java/com/moscepa/dto/SectionUpdateRequest.java

package com.moscepa.dto;

import jakarta.validation.constraints.NotBlank;

// On utilise un "record" pour un DTO simple et immuable.
public record SectionUpdateRequest(
    @NotBlank(message = "Le titre ne peut pas être vide")
    String titre,
    
    String contenu // Le contenu peut être vide
) {}
