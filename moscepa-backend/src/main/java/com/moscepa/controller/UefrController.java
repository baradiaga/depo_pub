package com.moscepa.controller;

import com.moscepa.dto.UefrDTO;
import com.moscepa.service.UefrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/uefrs")
@Tag(name = "UEFRs", description = "API de gestion des Unités d'Enseignement et de Formation de Recherche")
@CrossOrigin(origins = "http://localhost:4200")
public class UefrController {
    
    private final UefrService uefrService;
    
    @Autowired
    public UefrController(UefrService uefrService) {
        this.uefrService = uefrService;
    }
    
    @GetMapping
    @Operation(summary = "Récupérer toutes les UEFRs")
    public ResponseEntity<List<UefrDTO>> getAllUefrs() {
        List<UefrDTO> uefrs = uefrService.getAllUefrs();
        return ResponseEntity.ok(uefrs);
    }
    
    @GetMapping("/etablissement/{etablissementId}")
    @Operation(summary = "Récupérer les UEFRs par établissement")
    public ResponseEntity<List<UefrDTO>> getUefrsByEtablissementId(@PathVariable Long etablissementId) {
        List<UefrDTO> uefrs = uefrService.getUefrsByEtablissementId(etablissementId);
        return ResponseEntity.ok(uefrs);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une UEFR par son ID")
    public ResponseEntity<UefrDTO> getUefrById(@PathVariable Long id) {
        UefrDTO uefr = uefrService.getUefrById(id);
        return ResponseEntity.ok(uefr);
    }
    
    @PostMapping
    @Operation(summary = "Créer une nouvelle UEFR")
    public ResponseEntity<UefrDTO> createUefr(@Valid @RequestBody UefrDTO uefrDTO) {
        UefrDTO createdUefr = uefrService.createUefr(uefrDTO);
        return new ResponseEntity<>(createdUefr, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une UEFR")
    public ResponseEntity<UefrDTO> updateUefr(
            @PathVariable Long id, 
            @Valid @RequestBody UefrDTO uefrDTO) {
        UefrDTO updatedUefr = uefrService.updateUefr(id, uefrDTO);
        return ResponseEntity.ok(updatedUefr);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une UEFR")
    public ResponseEntity<Void> deleteUefr(@PathVariable Long id) {
        uefrService.deleteUefr(id);
        return ResponseEntity.noContent().build();
    }
}