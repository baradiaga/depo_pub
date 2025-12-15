// Fichier : src/main/java/com/moscepa/controller/InscriptionController.java

package com.moscepa.controller;

import com.moscepa.dto.InscriptionRequestDto;
import com.moscepa.dto.InscriptionResponseDto;
import com.moscepa.dto.InscriptionValidationRequest;
import com.moscepa.service.InscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")
public class InscriptionController {

    private final InscriptionService inscriptionService;

    public InscriptionController(InscriptionService inscriptionService) {
        this.inscriptionService = inscriptionService;
    }

    // ---------------------------------------------------------
    // 1. Inscrire un étudiant
    // ---------------------------------------------------------
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE_FORMATION')")
    public ResponseEntity<InscriptionResponseDto> inscrire(@RequestBody InscriptionRequestDto request) {
        InscriptionResponseDto nouvelleInscription = inscriptionService.inscrireEtudiant(request);
        return new ResponseEntity<>(nouvelleInscription, HttpStatus.CREATED);
    }

    // ---------------------------------------------------------
    // 2. Valider une inscription
    // ---------------------------------------------------------
    @PostMapping("/valider")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE_FORMATION')")
    public ResponseEntity<InscriptionResponseDto> validerInscription(@RequestBody InscriptionValidationRequest request) {
        InscriptionResponseDto inscriptionValidee = inscriptionService.validerInscription(request);
        return ResponseEntity.ok(inscriptionValidee);
    }

    // ---------------------------------------------------------
    // 3. Activer / Désactiver une inscription
    // ---------------------------------------------------------
    @PutMapping("/{id}/actif")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE_FORMATION')")
    public ResponseEntity<InscriptionResponseDto> changerStatutActif(
            @PathVariable Long id,
            @RequestParam boolean actif) {

        InscriptionResponseDto inscriptionMiseAJour = inscriptionService.changerStatutActif(id, actif);
        return ResponseEntity.ok(inscriptionMiseAJour);
    }

    // ---------------------------------------------------------
    // 4. Récupérer inscriptions en attente
    // ---------------------------------------------------------
    @GetMapping("/en-attente")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE_FORMATION')")
    public ResponseEntity<List<InscriptionResponseDto>> getInscriptionsEnAttente() {
        List<InscriptionResponseDto> inscriptionsEnAttente = inscriptionService.getInscriptionsEnAttente();
        return ResponseEntity.ok(inscriptionsEnAttente);
    }
}
