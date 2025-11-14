// Fichier : src/main/java/com/moscepa/dto/HistoriqueResultatDto.java (LA VERSION CORRECTE)

package com.moscepa.dto;

import com.moscepa.entity.ResultatTest;
import java.time.LocalDateTime;

public class HistoriqueResultatDto {

    private String nomChapitre;
    private LocalDateTime dateSoumission;
    private double scoreObtenu;
    private double scoreTotalPossible;
    private double pourcentage;

    public HistoriqueResultatDto() {}

    public HistoriqueResultatDto(ResultatTest resultat) {
        // ====================================================================
        // === CORRECTION N°1 : Le chemin d'accès au chapitre est corrigé.    ===
        // ====================================================================
        if (resultat.getTest() != null && resultat.getTest().getChapitre() != null) {
            this.nomChapitre = resultat.getTest().getChapitre().getNom();
        } else {
            this.nomChapitre = "Test non lié à un chapitre";
        }

        // ====================================================================
        // === CORRECTION N°2 : Le nom du getter pour la date est corrigé.   ===
        // ====================================================================
        this.dateSoumission = resultat.getDateTest();
        
        this.scoreObtenu = resultat.getScore();
        this.scoreTotalPossible = resultat.getScoreTotal();

        if (this.scoreTotalPossible > 0) {
            this.pourcentage = (this.scoreObtenu / this.scoreTotalPossible) * 100;
        } else {
            this.pourcentage = 0;
        }
    }

    // --- Getters et Setters (ceux-ci sont corrects et n'ont pas besoin de changer) ---

    public String getNomChapitre() { return nomChapitre; }
    public void setNomChapitre(String nomChapitre) { this.nomChapitre = nomChapitre; }

    public LocalDateTime getDateSoumission() { return dateSoumission; }
    public void setDateSoumission(LocalDateTime dateSoumission) { this.dateSoumission = dateSoumission; }

    public double getScoreObtenu() { return scoreObtenu; }
    public void setScoreObtenu(double scoreObtenu) { this.scoreObtenu = scoreObtenu; }

    public double getScoreTotalPossible() { return scoreTotalPossible; }
    public void setScoreTotalPossible(double scoreTotalPossible) { this.scoreTotalPossible = scoreTotalPossible; }

    public double getPourcentage() { return pourcentage; }
    public void setPourcentage(double pourcentage) { this.pourcentage = pourcentage; }
}
