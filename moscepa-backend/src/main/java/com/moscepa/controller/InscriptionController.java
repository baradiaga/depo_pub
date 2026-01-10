package com.moscepa.controller;

import com.moscepa.dto.*;
import com.moscepa.entity.Utilisateur;
import com.moscepa.service.EtudiantService;
import com.moscepa.service.InscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/inscriptions")
public class InscriptionController {

    private final InscriptionService inscriptionService;
    @Autowired 
    private EtudiantService etudiantService;

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
    @PostMapping("/inscrire")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESPONSABLE_FORMATION')")
public ResponseEntity<Map<String, String>> inscrireNouvelEtudiant(@Valid @RequestBody EtudiantRegistrationDto etudiantDto) {
    etudiantService.inscrireEtudiant(etudiantDto);
    
    Map<String, String> response = new HashMap<>();
    response.put("message", "Étudiant inscrit avec succès.");
    
    return new ResponseEntity<>(response, HttpStatus.CREATED);
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
