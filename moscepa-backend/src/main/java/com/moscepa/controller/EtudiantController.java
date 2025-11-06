// Fichier : src/main/java/com/moscepa/controller/EtudiantController.java (Nouveau fichier)

package com.moscepa.controller;

import com.moscepa.dto.EtudiantRegistrationDto;
import com.moscepa.service.EtudiantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/etudiants" )
public class EtudiantController {

    @Autowired
    private EtudiantService etudiantService;

    @PostMapping("/inscrire")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESPONSABLE_FORMATION')")
    public ResponseEntity<String> inscrireNouvelEtudiant(@Valid @RequestBody EtudiantRegistrationDto etudiantDto) {
        etudiantService.inscrireEtudiant(etudiantDto);
        java.util.Map<String, String> response = new java.util.HashMap<>();
    response.put("message", "Étudiant inscrit avec succès.");
        return new ResponseEntity<>("Étudiant inscrit avec succès.", HttpStatus.CREATED);
    }
}
