package com.moscepa.controller;

import com.moscepa.dto.MatiereSyllabusDto;
import com.moscepa.security.UserPrincipal;
import com.moscepa.service.SyllabusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/syllabus")
@CrossOrigin(origins = "*")
public class SyllabusController {

    private final SyllabusService syllabusService;

    @Autowired
    public SyllabusController(SyllabusService syllabusService) {
        this.syllabusService = syllabusService;
    }

    /**
     * Endpoint pour récupérer les détails du syllabus d'une matière pour l'étudiant connecté.
     */
    @GetMapping("/matiere/{ecId}")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<MatiereSyllabusDto> getSyllabusPourMatiere(
            @PathVariable Long ecId,
            Authentication authentication) {
        
        // Récupération de l'ID de l'utilisateur connecté
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long utilisateurId = userPrincipal.getId();
        
        // Appel du service (qui inclut maintenant le mapping de l'échelle/catégorie)
        MatiereSyllabusDto syllabusDto = syllabusService.getSyllabusPourEtudiant(ecId, utilisateurId);
        
        return ResponseEntity.ok(syllabusDto);
    }
}
