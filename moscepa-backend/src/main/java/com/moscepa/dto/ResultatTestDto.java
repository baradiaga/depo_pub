package com.moscepa.dto;

import java.time.LocalDateTime;

/**
 * DTO pour représenter le résultat d'un test après sa soumission.
 */
public class ResultatTestDto {

    private Long chapitreId;
    private double scoreObtenu;
    private double totalPointsPossible;
    private LocalDateTime dateSoumission;

    // Constructeurs
    public ResultatTestDto() {}

    public ResultatTestDto(Long chapitreId, double scoreObtenu, double totalPointsPossible, LocalDateTime dateSoumission) {
        this.chapitreId = chapitreId;
        this.scoreObtenu = scoreObtenu;
        this.totalPointsPossible = totalPointsPossible;
        this.dateSoumission = dateSoumission;
    }

    // Getters et Setters
    public Long getChapitreId() {
        return chapitreId;
    }

    public void setChapitreId(Long chapitreId) {
        this.chapitreId = chapitreId;
    }

    public double getScoreObtenu() {
        return scoreObtenu;
    }

    public void setScoreObtenu(double scoreObtenu) {
        this.scoreObtenu = scoreObtenu;
    }

    public double getTotalPointsPossible() {
        return totalPointsPossible;
    }

    public void setTotalPointsPossible(double totalPointsPossible) {
        this.totalPointsPossible = totalPointsPossible;
    }

    public LocalDateTime getDateSoumission() {
        return dateSoumission;
    }

    public void setDateSoumission(LocalDateTime dateSoumission) {
        this.dateSoumission = dateSoumission;
    }
}