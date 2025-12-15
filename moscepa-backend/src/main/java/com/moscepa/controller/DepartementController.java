package com.moscepa.controller;

import com.moscepa.dto.DepartementDTO;
import com.moscepa.service.DepartementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departements")
@Tag(name = "Départements", description = "API de gestion des départements")
@CrossOrigin(origins = "http://localhost:4200")
public class DepartementController {
    
    private final DepartementService departementService;
    
    @Autowired
    public DepartementController(DepartementService departementService) {
        this.departementService = departementService;
    }
    
    @GetMapping
    @Operation(summary = "Récupérer tous les départements")
    public ResponseEntity<List<DepartementDTO>> getAllDepartements() {
        List<DepartementDTO> departements = departementService.getAllDepartements();
        return ResponseEntity.ok(departements);
    }
    
    @GetMapping("/uefr/{uefrId}")
    @Operation(summary = "Récupérer les départements par UEFR")
    public ResponseEntity<List<DepartementDTO>> getDepartementsByUefrId(@PathVariable Long uefrId) {
        List<DepartementDTO> departements = departementService.getDepartementsByUefrId(uefrId);
        return ResponseEntity.ok(departements);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un département par son ID")
    public ResponseEntity<DepartementDTO> getDepartementById(@PathVariable Long id) {
        DepartementDTO departement = departementService.getDepartementById(id);
        return ResponseEntity.ok(departement);
    }
    
    @PostMapping
    @Operation(summary = "Créer un nouveau département")
    public ResponseEntity<DepartementDTO> createDepartement(@Valid @RequestBody DepartementDTO departementDTO) {
        DepartementDTO createdDepartement = departementService.createDepartement(departementDTO);
        return new ResponseEntity<>(createdDepartement, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un département")
    public ResponseEntity<DepartementDTO> updateDepartement(
            @PathVariable Long id, 
            @Valid @RequestBody DepartementDTO departementDTO) {
        DepartementDTO updatedDepartement = departementService.updateDepartement(id, departementDTO);
        return ResponseEntity.ok(updatedDepartement);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un département")
    public ResponseEntity<Void> deleteDepartement(@PathVariable Long id) {
        departementService.deleteDepartement(id);
        return ResponseEntity.noContent().build();
    }
}