// Fichier 2/4 : src/main/java/com/moscepa/controller/MatiereContenuController.java (Version Finale et Propre)

package com.moscepa.controller;

import com.moscepa.dto.ChapitreContenuDto;
import com.moscepa.dto.ChapitreCreateDto;
import com.moscepa.service.ChapitreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Ce controller gère les opérations de contenu dont le point d'entrée est une matière.
 * URL de base : /api/matieres/{matiereId}
 */
@RestController
@RequestMapping("/api/matieres/{matiereId}" ) 
public class MatiereContenuController {

    @Autowired
    private ChapitreService chapitreService;

    /**
     * GET /api/matieres/{matiereId}/contenu
     * Récupère l'arborescence complète (chapitres et sections) pour une matière donnée.
     */
    @GetMapping("/contenu")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN', 'RESPONSABLE_FORMATION','ETUDIANT')")
    public ResponseEntity<List<ChapitreContenuDto>> getContenuCompletPourMatiere(@PathVariable Long matiereId) {
        List<ChapitreContenuDto> contenu = chapitreService.findContenuCompletPourMatiere(matiereId);
        return ResponseEntity.ok(contenu);
    }

    /**
     * POST /api/matieres/{matiereId}/chapitres
     * Crée un nouveau chapitre dans la matière spécifiée.
     */
    @PostMapping("/chapitres")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN', 'RESPONSABLE_FORMATION')")
    public ResponseEntity<ChapitreContenuDto> createChapitre(
            @PathVariable Long matiereId,
            @Valid @RequestBody ChapitreCreateDto dto) {
        
        ChapitreContenuDto nouveauChapitre = chapitreService.createChapitre(matiereId, dto);
        return new ResponseEntity<>(nouveauChapitre, HttpStatus.CREATED);
    }
}
