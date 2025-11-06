// Fichier : src/main/java/com/moscepa/dto/MatiereInscriteDto.java (Nouveau Fichier)

package com.moscepa.dto;

public class MatiereInscriteDto {

    private Long id;          // ID de l'EC pour le lien du syllabus
    private String nomEc;     // Nom de l'EC (ex: "Base de données")
    private String codeEc;    // Code de l'EC (ex: "SID351")
    
    private String nomUe;     // Nom de l'UE parente (ex: "Bases de données et système d'information")
    private String codeUe;    // Code de l'UE parente
    
    private int coefficient;  // Le coefficient (on utilisera les crédits pour l'instant)
    private String statut;    // Le statut (ex: "NV" pour Non Validé)

    // --- Getters et Setters ---
    // Laissez votre IDE les générer pour vous (clic droit -> Source Action -> Generate Getters and Setters)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomEc() {
        return nomEc;
    }

    public void setNomEc(String nomEc) {
        this.nomEc = nomEc;
    }

    public String getCodeEc() {
        return codeEc;
    }

    public void setCodeEc(String codeEc) {
        this.codeEc = codeEc;
    }

    public String getNomUe() {
        return nomUe;
    }

    public void setNomUe(String nomUe) {
        this.nomUe = nomUe;
    }

    public String getCodeUe() {
        return codeUe;
    }

    public void setCodeUe(String codeUe) {
        this.codeUe = codeUe;
    }

    public int getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(int coefficient) {
        this.coefficient = coefficient;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}
