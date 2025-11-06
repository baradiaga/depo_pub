// Fichier : src/main/java/com/moscepa/controller/AllElementsConstitutifsController.java (Final et Corrigé)

package com.moscepa.controller;

import com.moscepa.dto.ChapitreDto;
import com.moscepa.dto.ElementConstitutifRequestDto;
import com.moscepa.dto.ElementConstitutifResponseDto;
import com.moscepa.service.ElementConstitutifService;
import com.moscepa.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api" ) // On garde /api comme base pour la flexibilité
public class AllElementsConstitutifsController {

    @Autowired
    private ElementConstitutifService elementConstitutifService;

    // --- GET ---
    @GetMapping("/elements-constitutifs")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESPONSABLE_FORMATION', 'ROLE_ENSEIGNANT')")
    public ResponseEntity<List<ElementConstitutifResponseDto>> getAllElementsConstitutifs() {
        List<ElementConstitutifResponseDto> dtos = elementConstitutifService.findAll();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/elements-constitutifs/details")
     @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ElementConstitutifResponseDto>> getElementsConstitutifsAvecDetails() {
        List<ElementConstitutifResponseDto> dtos = elementConstitutifService.findAllWithChapitres();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/elements-constitutifs/mes-matieres")
    @PreAuthorize("hasAuthority('ROLE_ENSEIGNANT')")
    public ResponseEntity<List<ElementConstitutifResponseDto>> getMesMatieres(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long enseignantId = userPrincipal.getId();
        List<ElementConstitutifResponseDto> dtos = elementConstitutifService.findByEnseignantId(enseignantId);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/elements-constitutifs/noms")
    @PreAuthorize("hasAuthority('ROLE_ENSEIGNANT')")
    public ResponseEntity<List<String>> getAllElementNoms() {
        List<String> noms = elementConstitutifService.findAllNoms();
        return ResponseEntity.ok(noms);
    }

    // --- POST ---
    @PostMapping("/unites-enseignement/{ueId}/elements-constitutifs")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ENSEIGNANT', 'ROLE_RESPONSABLE_FORMATION')")
    public ResponseEntity<ElementConstitutifResponseDto> createElementConstitutif(
            @PathVariable Long ueId,
            @Valid @RequestBody ElementConstitutifRequestDto requestDto) {
        ElementConstitutifResponseDto createdDto = elementConstitutifService.create(ueId, requestDto);
        return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
    }

    // ====================================================================
    // === CORRECTION APPLIQUÉE ICI                                     ===
    // ====================================================================
    @PutMapping("/elements-constitutifs/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESPONSABLE_FORMATION', 'ROLE_ENSEIGNANT')")
    public ResponseEntity<ElementConstitutifResponseDto> updateElementConstitutif(
            @PathVariable Long id,
            @Valid @RequestBody ElementConstitutifRequestDto dto) {
        ElementConstitutifResponseDto updatedDto = elementConstitutifService.update(id, dto);
        return ResponseEntity.ok(updatedDto);
    }
    
    // ====================================================================
    // === CORRECTION APPLIQUÉE ICI                                     ===
    // ====================================================================
    @DeleteMapping("/elements-constitutifs/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESPONSABLE_FORMATION', 'ROLE_ENSEIGNANT')")
    public ResponseEntity<Void> deleteElementConstitutif(@PathVariable Long id) {
        elementConstitutifService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/elements-constitutifs/{ecId}/chapitres")
    @PreAuthorize("isAuthenticated()") // Tout utilisateur authentifié peut voir cette liste
    public ResponseEntity<List<ChapitreDto>> getChapitresParElementConstitutif(@PathVariable Long ecId) {
        List<ChapitreDto> chapitres = elementConstitutifService.findChapitresByEcId(ecId);
        return ResponseEntity.ok(chapitres);
    }
}
