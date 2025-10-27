package com.moscepa.dto;

/**
 * DTO pour transporter les informations d'une matière et le statut d'inscription
 * d'un étudiant à cette matière.
 * Utilise des types Wrapper (Integer, Double) pour pouvoir accepter les valeurs 'null'
 * provenant de la base de données.
 */
public record MatiereStatutDto(
    Long matiereId,
    String ue,
    Integer ordre,       // <-- Doit être Integer
    String ec,
    Double coefficient,   // <-- Doit être Double
    String statut
) {}
