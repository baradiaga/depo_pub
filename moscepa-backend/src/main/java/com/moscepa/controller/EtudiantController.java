package com.moscepa.controller;

import com.moscepa.dto.EtudiantRegistrationDto;
import com.moscepa.service.EtudiantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/etudiants")
@CrossOrigin(origins = "http://localhost:4200") // Assurez-vous que le port correspond
public class EtudiantController {

    @Autowired
    private EtudiantService etudiantService;

    @PostMapping("/inscrire")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESPONSABLE_FORMATION')")
    public ResponseEntity<Map<String, String>> inscrireNouvelEtudiant(@Valid @RequestBody EtudiantRegistrationDto etudiantDto) {
        // Traitement de l'inscription
        etudiantService.inscrireEtudiant(etudiantDto);
        
        // CRUCIAL : On renvoie un objet JSON { "message": "..." }
        // car Angular attend du JSON par défaut
        Map<String, String> response = new HashMap<>();
        response.put("message", "Étudiant inscrit avec succès.");
        response.put("status", "success");
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
