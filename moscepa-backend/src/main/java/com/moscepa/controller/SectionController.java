// Fichier 4/4 : src/main/java/com/moscepa/controller/SectionController.java (Version Finale et Propre)

package com.moscepa.controller;

import com.moscepa.dto.SectionDto;
import com.moscepa.dto.SectionUpdateRequest;
import com.moscepa.service.SectionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Ce controller gère les opérations qui ciblent directement une section par son ID.
 * URL de base : /api/sections
 */
@RestController
@RequestMapping("/api/sections" )
public class SectionController {

    @Autowired
    private SectionService sectionService;

    /**
     * PUT /api/sections/{id}
     * Met à jour une section existante.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ENSEIGNANT', 'ROLE_RESPONSABLE_FORMATION')")
    public ResponseEntity<SectionDto> updateSection(
            @PathVariable Long id, 
            @Valid @RequestBody SectionUpdateRequest updateRequest) {
        
        SectionDto updatedSection = sectionService.updateSectionContent(id, updateRequest);
        return ResponseEntity.ok(updatedSection);
    }

    /**
     * DELETE /api/sections/{id}
     * Supprime une section par son ID.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ENSEIGNANT', 'ROLE_RESPONSABLE_FORMATION')")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id) {
        sectionService.deleteSection(id);
        return ResponseEntity.noContent().build();
    }
}
