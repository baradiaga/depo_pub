// Fichier : src/main/java/com/moscepa/controller/InscriptionController.java

package com.moscepa.controller;

import com.moscepa.dto.InscriptionRequestDto; // On va créer ce DTO
import com.moscepa.entity.Inscription;
import com.moscepa.service.InscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inscriptions" )
public class InscriptionController {

    private final InscriptionService inscriptionService;

    public InscriptionController(InscriptionService inscriptionService) {
        this.inscriptionService = inscriptionService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE_FORMATION')") // Seuls les admins/responsables peuvent inscrire
    public ResponseEntity<Inscription> inscrire(@RequestBody InscriptionRequestDto request) {
        Inscription nouvelleInscription = inscriptionService.inscrireEtudiant(request);
        return new ResponseEntity<>(nouvelleInscription, HttpStatus.CREATED);
    }
}
