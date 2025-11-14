// Fichier : src/main/java/com/moscepa/dto/SoumissionTestDto.java (Nouveau Fichier)

package com.moscepa.dto;

import java.util.List;

/**
 * DTO représentant la soumission complète d'un test par un étudiant.
 * Il est conçu pour être flexible et accepter différents formats de réponses.
 */
public class SoumissionTestDto {

    // Une liste de toutes les réponses données par l'étudiant.
    private List<ReponseSoumiseDto> reponses;

    // --- Getters et Setters ---
    public List<ReponseSoumiseDto> getReponses() {
        return reponses;
    }

    public void setReponses(List<ReponseSoumiseDto> reponses) {
        this.reponses = reponses;
    }
}
