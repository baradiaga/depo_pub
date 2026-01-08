package com.moscepa.controller;

import com.moscepa.entity.Utilisateur;
import com.moscepa.service.TuteurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tuteur")
@CrossOrigin("*") 
public class TuteurController {

    @Autowired
    private TuteurService tuteurService;

    // Endpoint pour voir la liste des étudiants à encadrer
    @GetMapping("/{tuteurId}/etudiants")
    public ResponseEntity<List<Utilisateur>> getEtudiants(@PathVariable Long tuteurId) {
        return ResponseEntity.ok(tuteurService.getMesEtudiants(tuteurId));
    }

    // Endpoint pour valider une inscription spécifique
    @PutMapping("/inscription/{id}/valider")
    public ResponseEntity<String> valider(@PathVariable Long id) {
        tuteurService.validerEtudiant(id);
        return ResponseEntity.ok("Inscription validée avec succès");
    }
}
