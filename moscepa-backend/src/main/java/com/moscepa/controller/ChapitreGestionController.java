// Fichier 3/4 : src/main/java/com/moscepa/controller/ChapitreGestionController.java (Version Finale et Propre)

package com.moscepa.controller;

import com.moscepa.dto.SectionCreateDto;
import com.moscepa.dto.SectionDto;
import com.moscepa.service.ChapitreService;
import com.moscepa.service.SectionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Ce controller gère les opérations de contenu dont le point d'entrée est un chapitre.
 * URL de base : /api/chapitres
 */
@RestController
@RequestMapping("/api/chapitres" )
public class ChapitreGestionController {

    @Autowired
    private ChapitreService chapitreService;

    @Autowired
    private SectionService sectionService; // Nécessaire pour la création de section

    /**
     * DELETE /api/chapitres/{id}
     * Supprime un chapitre par son ID.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<Void> deleteChapitre(@PathVariable Long id) {
        chapitreService.deleteChapitre(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/chapitres/{chapitreId}/sections
     * Crée une nouvelle section dans un chapitre spécifique.
     */
    @PostMapping("/{chapitreId}/sections")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ENSEIGNANT', 'ROLE_RESPONSABLE_FORMATION')")
    public ResponseEntity<SectionDto> createSection(
            @PathVariable Long chapitreId, 
            @Valid @RequestBody SectionCreateDto sectionDto) {
        
        SectionDto createdSection = sectionService.createSection(chapitreId, sectionDto);
        return new ResponseEntity<>(createdSection, HttpStatus.CREATED);
    }
}
