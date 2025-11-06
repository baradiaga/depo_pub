// Fichier : src/main/java/com/moscepa/controller/UniteEnseignementController.java (Final et Corrigé)

package com.moscepa.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.moscepa.dto.UniteEnseignementDto;
import com.moscepa.service.UniteEnseignementService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/unites-enseignement" )
@CrossOrigin(origins = "*")
public class UniteEnseignementController {

    @Autowired
    private UniteEnseignementService ueService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UniteEnseignementDto>> getAllUe() {
        return ResponseEntity.ok(ueService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UniteEnseignementDto> getUeById(@PathVariable Long id) {
        return ueService.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESPONSABLE_FORMATION', 'ROLE_ENSEIGNANT')")
    public ResponseEntity<UniteEnseignementDto> createUe(
            @Valid @RequestBody UniteEnseignementDto ueDto) {
        UniteEnseignementDto savedDto = ueService.save(ueDto);
        return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
    }

    // ====================================================================
    // === CORRECTION APPLIQUÉE ICI                                     ===
    // ====================================================================
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESPONSABLE_FORMATION', 'ROLE_ENSEIGNANT')")
    public ResponseEntity<UniteEnseignementDto> updateUe(@PathVariable Long id,
            @Valid @RequestBody UniteEnseignementDto ueDto) {
        try {
            return ResponseEntity.ok(ueService.update(id, ueDto));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ====================================================================
    // === CORRECTION APPLIQUÉE ICI                                     ===
    // ====================================================================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESPONSABLE_FORMATION', 'ROLE_ENSEIGNANT')")
    public ResponseEntity<Void> deleteUe(@PathVariable Long id) {
        try {
            ueService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
