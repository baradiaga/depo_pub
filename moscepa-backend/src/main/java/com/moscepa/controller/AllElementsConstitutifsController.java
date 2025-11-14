// Fichier : AllElementsConstitutifsController.java (VERSION CORRIGÉE AVEC LOGS)

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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api" )
public class AllElementsConstitutifsController {

    @Autowired
    private ElementConstitutifService elementConstitutifService;

    // ====================================================================
    // === MÉTHODE CORRIGÉE AVEC LOGIQUE ÉTUDIANTE ET TRAÇAGE COMPLET     ===
    // ====================================================================
    @GetMapping("/elements-constitutifs/mes-matieres")
    @PreAuthorize("hasAnyAuthority('ROLE_ENSEIGNANT', 'ROLE_ADMIN', 'ROLE_RESPONSABLE_FORMATION','ROLE_ETUDIANT')")
    public ResponseEntity<List<ElementConstitutifResponseDto>> getMesMatieres(Authentication authentication) {
        
        System.out.println("\n\n================== DÉBUT DU TRAÇAGE ==================");
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long utilisateurId = userPrincipal.getId();
        String roles = userPrincipal.getAuthorities().stream()
                                    .map(auth -> auth.getAuthority())
                                    .collect(Collectors.joining(", "));

        System.out.println("[CONTROLLER] Requête reçue pour /mes-matieres.");
        System.out.println("[CONTROLLER] Utilisateur ID: " + utilisateurId);
        System.out.println("[CONTROLLER] Utilisateur Email: " + userPrincipal.getEmail());
        System.out.println("[CONTROLLER] Rôles de l'utilisateur: [" + roles + "]");

        List<ElementConstitutifResponseDto> dtos;

        if (userPrincipal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) ||
            userPrincipal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_RESPONSABLE_FORMATION"))) {
            
            System.out.println("[CONTROLLER] Rôle ADMIN/RESPONSABLE détecté. Appel de findAll().");
            dtos = elementConstitutifService.findAll();
            System.out.println("[CONTROLLER] Le service (findAll) a retourné " + dtos.size() + " matière(s).");

        } else if (userPrincipal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ENSEIGNANT"))) {
            
            System.out.println("[CONTROLLER] Rôle ENSEIGNANT détecté. Appel de findByEnseignantId(" + utilisateurId + ").");
            dtos = elementConstitutifService.findByEnseignantId(utilisateurId);
            System.out.println("[CONTROLLER] Le service (findByEnseignantId) a retourné " + dtos.size() + " matière(s).");

        } else if (userPrincipal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ETUDIANT"))) {
            
            System.out.println("[CONTROLLER] Rôle ETUDIANT détecté. Appel de findMatieresByEtudiantId(" + utilisateurId + ").");
            dtos = elementConstitutifService.findMatieresByEtudiantId(utilisateurId);
            System.out.println("[CONTROLLER] Le service (findMatieresByEtudiantId) a retourné " + dtos.size() + " matière(s).");

        } else {
            System.out.println("[CONTROLLER] Aucun rôle géré trouvé. Renvoi d'une liste vide.");
            dtos = List.of(); // Renvoie une liste vide immuable
        }

        System.out.println("[CONTROLLER] Envoi de la réponse au frontend.");
        System.out.println("================== FIN DU TRAÇAGE ==================\n\n");

        return ResponseEntity.ok(dtos);
    }

    // --- VOS AUTRES MÉTHODES RESTENT ICI ---
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

    @GetMapping("/elements-constitutifs/noms")
    @PreAuthorize("hasAnyAuthority('ROLE_ENSEIGNANT','ROLE_ADMIN')")
    public ResponseEntity<List<String>> getAllElementNoms() {
        List<String> noms = elementConstitutifService.findAllNoms();
        return ResponseEntity.ok(noms);
    }

    @PostMapping("/unites-enseignement/{ueId}/elements-constitutifs")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ENSEIGNANT', 'ROLE_RESPONSABLE_FORMATION')")
    public ResponseEntity<ElementConstitutifResponseDto> createElementConstitutif(
            @PathVariable Long ueId,
            @Valid @RequestBody ElementConstitutifRequestDto requestDto) {
        ElementConstitutifResponseDto createdDto = elementConstitutifService.create(ueId, requestDto);
        return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
    }

    @PutMapping("/elements-constitutifs/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESPONSABLE_FORMATION', 'ROLE_ENSEIGNANT')")
    public ResponseEntity<ElementConstitutifResponseDto> updateElementConstitutif(
            @PathVariable Long id,
            @Valid @RequestBody ElementConstitutifRequestDto dto) {
        ElementConstitutifResponseDto updatedDto = elementConstitutifService.update(id, dto);
        return ResponseEntity.ok(updatedDto);
    }
    
    @DeleteMapping("/elements-constitutifs/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESPONSABLE_FORMATION', 'ROLE_ENSEIGNANT')")
    public ResponseEntity<Void> deleteElementConstitutif(@PathVariable Long id) {
        elementConstitutifService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/elements-constitutifs/{ecId}/chapitres")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChapitreDto>> getChapitresParElementConstitutif(@PathVariable Long ecId) {
        List<ChapitreDto> chapitres = elementConstitutifService.findChapitresByEcId(ecId);
        return ResponseEntity.ok(chapitres);
    }
}
