// Fichier : src/main/java/com/moscepa/dto/InscriptionRequest.java (Version Complète et Correcte)

package com.moscepa.dto;

import jakarta.validation.constraints.NotNull;

public class InscriptionRequest {

    @NotNull(message = "L'ID de l'étudiant ne peut pas être nul.")
    private Long etudiantId;

    @NotNull(message = "L'ID de la matière (EC) ne peut pas être nul.")
    private Long ecId;

    // ====================================================================
    // === GETTERS (MÉTHODES MANQUANTES)                                ===
    // ====================================================================
    public Long getEtudiantId() {
        return etudiantId;
    }

    public Long getEcId() {
        return ecId;
    }

    // ====================================================================
    // === SETTERS (BON À AVOIR)                                        ===
    // ====================================================================
    public void setEtudiantId(Long etudiantId) {
        this.etudiantId = etudiantId;
    }

    public void setEcId(Long ecId) {
        this.ecId = ecId;
    }
}
