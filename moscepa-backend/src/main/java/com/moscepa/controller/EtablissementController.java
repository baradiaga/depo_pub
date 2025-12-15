package com.moscepa.controller;

import com.moscepa.dto.EtablissementDTO;
import com.moscepa.service.EtablissementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/etablissements")
@Tag(name = "Établissements", description = "API de gestion des établissements")
@CrossOrigin(origins = "http://localhost:4200")
public class EtablissementController {
    
    private final EtablissementService etablissementService;
    
    @Autowired
    public EtablissementController(EtablissementService etablissementService) {
        this.etablissementService = etablissementService;
    }
    
    @GetMapping
    @Operation(summary = "Récupérer tous les établissements")
    public ResponseEntity<List<EtablissementDTO>> getAllEtablissements() {
        List<EtablissementDTO> etablissements = etablissementService.getAllEtablissements();
        return ResponseEntity.ok(etablissements);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un établissement par son ID")
    public ResponseEntity<EtablissementDTO> getEtablissementById(@PathVariable Long id) {
        EtablissementDTO etablissement = etablissementService.getEtablissementById(id);
        return ResponseEntity.ok(etablissement);
    }
    
    @PostMapping
    @Operation(summary = "Créer un nouvel établissement")
    public ResponseEntity<EtablissementDTO> createEtablissement(@Valid @RequestBody EtablissementDTO etablissementDTO) {
        EtablissementDTO createdEtablissement = etablissementService.createEtablissement(etablissementDTO);
        return new ResponseEntity<>(createdEtablissement, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un établissement")
    public ResponseEntity<EtablissementDTO> updateEtablissement(
            @PathVariable Long id, 
            @Valid @RequestBody EtablissementDTO etablissementDTO) {
        EtablissementDTO updatedEtablissement = etablissementService.updateEtablissement(id, etablissementDTO);
        return ResponseEntity.ok(updatedEtablissement);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un établissement")
    public ResponseEntity<Void> deleteEtablissement(@PathVariable Long id) {
        etablissementService.deleteEtablissement(id);
        return ResponseEntity.noContent().build();
    }
}