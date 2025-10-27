package com.moscepa.dto;

public record UniteEnseignementDto(Long id, String nom, String code, String description, int credit,
                int semestre, String objectifs, ResponsableDto responsable) {
}
