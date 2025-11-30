package com.moscepa.controller;

import com.moscepa.dto.MatiereStatutDto;
import com.moscepa.security.UserPrincipal;
import com.moscepa.service.ProgressionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).build();
        }

        Long utilisateurId = userPrincipal.getId();
        List<MatiereStatutDto> matieres = progressionService.findMatieresByEtudiant(utilisateurId);
        return ResponseEntity.ok(matieres);
    }
}
