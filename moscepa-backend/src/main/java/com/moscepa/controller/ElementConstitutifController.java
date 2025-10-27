package com.moscepa.controller;

import com.moscepa.dto.ElementConstitutifRequestDto;
import com.moscepa.dto.ElementConstitutifResponseDto; // <-- NOUVEL IMPORT
import com.moscepa.service.ElementConstitutifService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unites-enseignement/{ueId}/elements-constitutifs" )
@CrossOrigin(origins = "http://localhost:4200" )
public class ElementConstitutifController {

    @Autowired
    private ElementConstitutifService elementConstitutifService;

    /**
     * MODIFIÉ : Retourne un DTO de réponse complet.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ElementConstitutifResponseDto> createElementConstitutif(
            @PathVariable Long ueId,
            @Valid @RequestBody ElementConstitutifRequestDto elementDto) {

        // Le service retourne maintenant directement le bon DTO de réponse
        ElementConstitutifResponseDto responseDto = elementConstitutifService.creerPourUe(ueId, elementDto);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * MODIFIÉ : Retourne une liste de DTOs de réponse.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENSEIGNANT', 'ETUDIANT')")
    public ResponseEntity<List<ElementConstitutifResponseDto>> getElementsConstitutifsParUe(@PathVariable Long ueId) {
        // Le service retourne maintenant directement la liste des bons DTOs
        List<ElementConstitutifResponseDto> dtos = elementConstitutifService.findByUeId(ueId);
        return ResponseEntity.ok(dtos);
    }

    // La méthode de mapping manuelle est supprimée car cette logique est maintenant dans le service.
}
