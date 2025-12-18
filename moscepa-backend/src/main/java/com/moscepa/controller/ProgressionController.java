package com.moscepa.controller;

import com.moscepa.dto.ChapitreProgressDto;
import com.moscepa.dto.MatiereStatutDto;
import com.moscepa.security.UserPrincipal;
import com.moscepa.service.ProgressionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/progression")
@CrossOrigin(origins = "*")
public class ProgressionController {

    private final ProgressionService progressionService;

    public ProgressionController(ProgressionService progressionService) {
        this.progressionService = progressionService;
    }

    /**
     * Récupère les matières de l'étudiant actuellement connecté.
     */
    @GetMapping("/mes-matieres")
    public ResponseEntity<List<MatiereStatutDto>> getMatieresPourEtudiantConnecte(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).build();
        }

        Long utilisateurId = userPrincipal.getId();
        List<MatiereStatutDto> matieres = progressionService.findMatieresByEtudiant(utilisateurId);
        return ResponseEntity.ok(matieres);
    }

    /**
     * NOUVEL ENDPOINT: Récupère la progression par chapitre pour un étudiant spécifique
     * Endpoint pour admin
     */
    @GetMapping("/etudiant/{studentId}/chapitres")
    public ResponseEntity<List<ChapitreProgressDto>> getChapitresProgressForStudent(
            @PathVariable Long studentId,
            @RequestParam(required = false) String parcoursType) {
        
        try {
            List<ChapitreProgressDto> chapitres = 
                progressionService.findChapitresProgressByStudent(studentId, parcoursType);
            
            if (chapitres.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            
            return ResponseEntity.ok(chapitres);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * NOUVEL ENDPOINT: Récupère la progression par chapitre groupée par matière
     */
    @GetMapping("/etudiant/{studentId}/chapitres-groupes")
    public ResponseEntity<Map<String, List<ChapitreProgressDto>>> getChapitresGroupedByMatiere(
            @PathVariable Long studentId,
            @RequestParam(required = false) String parcoursType) {
        
        try {
            Map<String, List<ChapitreProgressDto>> chapitresGroupes = 
                progressionService.findChapitresGroupedByMatiere(studentId, parcoursType);
            
            if (chapitresGroupes.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            
            return ResponseEntity.ok(chapitresGroupes);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Récupère la progression par chapitre pour l'étudiant connecté
     */
    @GetMapping("/mes-chapitres")
    public ResponseEntity<List<ChapitreProgressDto>> getMesChapitresProgress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) String parcoursType) {
        
        if (userPrincipal == null) {
            return ResponseEntity.status(401).build();
        }

        Long utilisateurId = userPrincipal.getId();
        List<ChapitreProgressDto> chapitres = 
            progressionService.findChapitresProgressByStudent(utilisateurId, parcoursType);
        
        return ResponseEntity.ok(chapitres);
    }
}