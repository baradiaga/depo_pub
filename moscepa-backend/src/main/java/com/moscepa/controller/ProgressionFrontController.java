package com.moscepa.controller;

import com.moscepa.dto.ChapitreProgressFrontDto;
import com.moscepa.dto.MatiereStatutDto;
import com.moscepa.security.UserPrincipal;
import com.moscepa.service.ProgressionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/front/progression")
@CrossOrigin(origins = "*")
public class ProgressionFrontController {

    private final ProgressionService progressionService;

    public ProgressionFrontController(ProgressionService progressionService) {
        this.progressionService = progressionService;
    }

    /**
     * Récupère les matières de l'étudiant actuellement connecté (frontend).
     */
    @GetMapping("/mes-matieres")
    public ResponseEntity<List<MatiereStatutDto>> getMatieresPourEtudiantConnecteFront(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).build();
        }

        Long utilisateurId = userPrincipal.getId();
        List<MatiereStatutDto> matieres = progressionService.findMatieresByEtudiant(utilisateurId);
        return ResponseEntity.ok(matieres);
    }

    /**
     * Récupère la progression par chapitre pour un étudiant spécifique (frontend).
     */
    @GetMapping("/etudiant/{studentId}/chapitres")
    public ResponseEntity<List<ChapitreProgressFrontDto>> getChapitresProgressForStudentFront(
            @PathVariable Long studentId,
            @RequestParam(required = false) String parcoursType) {
        
        try {
            List<ChapitreProgressFrontDto> chapitres = 
                progressionService.findChapitresProgressForFrontend(studentId, parcoursType);
            
            if (chapitres.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            
            return ResponseEntity.ok(chapitres);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Récupère la progression par chapitre groupée par matière (frontend).
     */
    @GetMapping("/etudiant/{studentId}/chapitres-groupes")
    public ResponseEntity<Map<String, List<ChapitreProgressFrontDto>>> getChapitresGroupedByMatiereFront(
            @PathVariable Long studentId,
            @RequestParam(required = false) String parcoursType) {
        
        try {
            Map<String, List<ChapitreProgressFrontDto>> chapitresGroupes = 
                progressionService.findChapitresGroupedForFrontend(studentId, parcoursType);
            
            if (chapitresGroupes.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            
            return ResponseEntity.ok(chapitresGroupes);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Récupère la progression par chapitre pour l'étudiant connecté (frontend).
     */
    @GetMapping("/mes-chapitres")
    public ResponseEntity<List<ChapitreProgressFrontDto>> getMesChapitresProgressFront(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) String parcoursType) {
        
        if (userPrincipal == null) {
            return ResponseEntity.status(401).build();
        }

        Long utilisateurId = userPrincipal.getId();
        List<ChapitreProgressFrontDto> chapitres = 
            progressionService.findChapitresProgressForFrontend(utilisateurId, parcoursType);
        
        return ResponseEntity.ok(chapitres);
    }
}