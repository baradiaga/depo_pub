package com.moscepa.controller;

import com.moscepa.dto.*;
import com.moscepa.entity.Utilisateur;
import com.moscepa.service.InscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")
public class InscriptionController {

    private final InscriptionService inscriptionService;

    public InscriptionController(InscriptionService inscriptionService) {
        this.inscriptionService = inscriptionService;
    }

    // --- ACCÈS ÉTUDIANT : Voir seulement ses matières VALIDÉES ---
    @GetMapping("/mes-matieres")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<List<InscriptionResponseDto>> getMesMatieres(
            @AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        // Sécurité : On utilise l'ID issu du Token/Session, pas un paramètre URL
        List<InscriptionResponseDto> data = inscriptionService.getMesInscriptionsValidees(utilisateurConnecte.getId());
        return ResponseEntity.ok(data);
    }

    // --- ACCÈS ADMIN : Inscrire un étudiant ---
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE_FORMATION')")
    public ResponseEntity<InscriptionResponseDto> inscrire(@RequestBody InscriptionRequestDto request) {
        return new ResponseEntity<>(inscriptionService.inscrireEtudiant(request), HttpStatus.CREATED);
    }

    // --- ACCÈS ADMIN : Valider une inscription ---
    @PostMapping("/valider")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE_FORMATION')")
    public ResponseEntity<InscriptionResponseDto> valider(@RequestBody InscriptionValidationRequest req) {
        return ResponseEntity.ok(inscriptionService.validerInscription(req));
    }

    // --- ACCÈS ADMIN : Liste des attentes ---
    @GetMapping("/en-attente")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE_FORMATION')")
    public ResponseEntity<List<InscriptionResponseDto>> getEnAttente() {
        return ResponseEntity.ok(inscriptionService.getInscriptionsEnAttente());
    }
}
