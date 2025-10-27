package com.moscepa.controller;

import com.moscepa.dto.EtudiantDto;
import com.moscepa.dto.MatiereDto;
import com.moscepa.service.EtudiantService;
import com.moscepa.service.MatiereService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des étudiants
 */
@RestController
@RequestMapping("/api/etudiants")
@CrossOrigin(origins = "*")
@Tag(name = "Étudiants", description = "API de gestion des étudiants")
public class EtudiantController {

    @Autowired
    private EtudiantService etudiantService;

    @Autowired
    private MatiereService matiereService;

    /**
     * Inscrit un nouvel étudiant
     */
    @PostMapping
    @Operation(summary = "Inscrire un nouvel étudiant")
    public ResponseEntity<EtudiantDto> inscrireEtudiant(@Valid @RequestBody EtudiantDto etudiantDto) {
        try {
            EtudiantDto nouvelEtudiant = etudiantService.inscrireEtudiant(etudiantDto);
            return new ResponseEntity<>(nouvelEtudiant, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Récupère tous les étudiants
     */
    @GetMapping
    @Operation(summary = "Récupérer tous les étudiants")
    public ResponseEntity<List<EtudiantDto>> getEtudiants() {
        List<EtudiantDto> etudiants = etudiantService.getEtudiants();
        return new ResponseEntity<>(etudiants, HttpStatus.OK);
    }

    /**
     * Récupère un étudiant par son ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un étudiant par son ID")
    public ResponseEntity<EtudiantDto> getEtudiantById(@PathVariable Long id) {
        Optional<EtudiantDto> etudiant = etudiantService.getEtudiantById(id);
        return etudiant.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Met à jour un étudiant
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un étudiant")
    public ResponseEntity<EtudiantDto> updateEtudiant(@PathVariable Long id, @Valid @RequestBody EtudiantDto etudiantDto) {
        try {
            EtudiantDto etudiantMisAJour = etudiantService.updateEtudiant(id, etudiantDto);
            return new ResponseEntity<>(etudiantMisAJour, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Supprime un étudiant
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un étudiant")
    public ResponseEntity<Void> deleteEtudiant(@PathVariable Long id) {
        try {
            etudiantService.deleteEtudiant(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

