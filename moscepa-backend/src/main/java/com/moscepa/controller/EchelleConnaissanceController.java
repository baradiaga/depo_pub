// Fichier : src/main/java/com/moscepa/controller/EchelleConnaissanceController.java

package com.moscepa.controller;

import com.moscepa.dto.EchelleConnaissanceDto;
import com.moscepa.service.EchelleConnaissanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/echelles-connaissance" )
@CrossOrigin(origins = "http://localhost:4200" )
public class EchelleConnaissanceController {

    private final EchelleConnaissanceService service;

    public EchelleConnaissanceController(EchelleConnaissanceService service) {
        this.service = service;
    }

    // GET: Récupérer toutes les échelles
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENSEIGNANT')") // Seuls les admins/enseignants peuvent gérer les échelles
    public ResponseEntity<List<EchelleConnaissanceDto>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // POST: Créer une nouvelle échelle
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Seuls les admins peuvent créer/modifier les échelles
    public ResponseEntity<EchelleConnaissanceDto> create(@Valid @RequestBody EchelleConnaissanceDto dto) {
        EchelleConnaissanceDto newEchelle = service.create(dto);
        return new ResponseEntity<>(newEchelle, HttpStatus.CREATED);
    }

    // PUT: Mettre à jour une échelle existante
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EchelleConnaissanceDto> update(@PathVariable Long id, @Valid @RequestBody EchelleConnaissanceDto dto) {
        EchelleConnaissanceDto updatedEchelle = service.update(id, dto);
        return ResponseEntity.ok(updatedEchelle);
    }

    // DELETE: Supprimer une échelle
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
