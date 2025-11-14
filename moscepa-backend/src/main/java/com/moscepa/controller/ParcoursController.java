// Fichier : src/main/java/com/moscepa/controller/ParcoursController.java (Version finale corrigée)

package com.moscepa.controller;

import com.moscepa.dto.ParcoursDto;
import com.moscepa.dto.ParcoursRequestDto;
import com.moscepa.security.UserPrincipal;
import com.moscepa.service.ParcoursService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parcours" )
@CrossOrigin(origins = "http://localhost:4200" )
public class ParcoursController {

    private final ParcoursService parcoursService;

    public ParcoursController(ParcoursService parcoursService) {
        this.parcoursService = parcoursService;
    }

    // ====================================================================
    // === NOUVEL ENDPOINT POUR L'ÉTUDIANT CONNECTÉ                     ===
    // ====================================================================
    /**
     * Récupère les parcours (recommandés et choisis) pour l'utilisateur
     * actuellement authentifié.
     */
    @GetMapping("/etudiant/me") // URL claire et non ambiguë : /api/parcours/etudiant/me
    public ResponseEntity<ParcoursDto> getMesParcours(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long utilisateurId = userPrincipal.getId();
        ParcoursDto parcours = parcoursService.getParcoursPourEtudiant(utilisateurId);
        return ResponseEntity.ok(parcours);
    }

    // ====================================================================
    // === ANCIEN ENDPOINT (GARDÉ POUR L'ADMINISTRATION PAR EXEMPLE)    ===
    // ====================================================================
    /**
     * Récupère les parcours pour un étudiant spécifique par son ID.
     * Utile pour un administrateur qui voudrait consulter le parcours d'un étudiant.
     */
    @GetMapping("/etudiant/{etudiantId}")
    public ResponseEntity<ParcoursDto> getParcoursPourEtudiant(@PathVariable Long etudiantId) {
        ParcoursDto parcours = parcoursService.getParcoursPourEtudiant(etudiantId);
        return ResponseEntity.ok(parcours);
    }

    @PostMapping("/etudiant")
    public ResponseEntity<Void> enregistrerParcours(Authentication authentication, @RequestBody ParcoursRequestDto request) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long utilisateurId = userPrincipal.getId();
        List<Long> chapitreIds = request.getChapitresChoisisIds();
        parcoursService.enregistrerChoixEtudiant(utilisateurId, chapitreIds);
        return ResponseEntity.ok().build();
    }
}
