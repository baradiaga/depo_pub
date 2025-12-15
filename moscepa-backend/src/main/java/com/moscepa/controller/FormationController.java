// Fichier : src/main/java/com/moscepa/controller/FormationController.java (Complet)

package com.moscepa.controller;

import com.moscepa.dto.FormationCreationDto;
import com.moscepa.dto.FormationDetailDto;
import com.moscepa.service.FormationService;
import com.moscepa.security.UserPrincipal; // AJOUTÉ
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // AJOUTÉ
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formations" )
@CrossOrigin(origins = "http://localhost:4200" )
public class FormationController {

    private final FormationService formationService;

    public FormationController(FormationService formationService) {
        this.formationService = formationService;
    }

    // ====================================================================
    // === CRÉATION (Accessible aux Enseignants et Admins)              ===
    // ====================================================================

    @PostMapping
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<FormationDetailDto> creerFormation(@Valid @RequestBody FormationCreationDto dto) {
        FormationDetailDto nouvelleFormation = formationService.creerFormation(dto);
        return new ResponseEntity<>(nouvelleFormation, HttpStatus.CREATED);
    }

    // ====================================================================
    // === LECTURE (Accessible à tous les utilisateurs authentifiés)    ===
    // ====================================================================
    
    // AJOUTÉ : Endpoint pour récupérer les formations créées par l'enseignant connecté
    @GetMapping("/mes-formations")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<List<FormationDetailDto>> getMesFormations(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long createurId = userPrincipal.getId();
        List<FormationDetailDto> formations = formationService.getFormationsByCreateurId(createurId);
        return ResponseEntity.ok(formations);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FormationDetailDto>> getAllFormations() {
        List<FormationDetailDto> formations = formationService.getAllFormations();
        return ResponseEntity.ok(formations);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FormationDetailDto> getFormationById(@PathVariable Long id) {
        FormationDetailDto formation = formationService.getFormationById(id);
        return ResponseEntity.ok(formation);
    }

    // ====================================================================
    // === SUPPRESSION (Accessible aux Enseignants et Admins)           ===
    // ====================================================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<Void> supprimerFormation(@PathVariable Long id) {
        formationService.supprimerFormation(id);
        return ResponseEntity.noContent().build();
    }

    // Mise à jour d'une formation
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<FormationDetailDto> modifierFormation(
            @PathVariable Long id,
            @Valid @RequestBody FormationCreationDto dto) {
        FormationDetailDto formationModifiee = formationService.modifierFormation(id, dto);
        return ResponseEntity.ok(formationModifiee);
    }

}
