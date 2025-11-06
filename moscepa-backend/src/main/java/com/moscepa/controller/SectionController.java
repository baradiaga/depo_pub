// Fichier : src/main/java/com/moscepa/controller/SectionController.java (Avec Try-Catch de Débogage)

package com.moscepa.controller;

import com.moscepa.dto.SectionDto;
import com.moscepa.dto.SectionUpdateRequest;
import com.moscepa.service.SectionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api" )
@CrossOrigin(origins = "*")
public class SectionController {

    @Autowired
    private SectionService sectionService;

    // --- CRÉATION ---
    @PostMapping("/chapitres/{chapitreId}/sections")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ENSEIGNANT', 'ROLE_RESPONSABLE_FORMATION')")
    public ResponseEntity<SectionDto> createSection(@PathVariable Long chapitreId, @Valid @RequestBody SectionDto sectionDto) {
        SectionDto createdSection = sectionService.createSection(chapitreId, sectionDto);
        return new ResponseEntity<>(createdSection, HttpStatus.CREATED);
    }

    // --- LECTURE ---
    @GetMapping("/sections")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SectionDto>> getAllSections() {
        List<SectionDto> sections = sectionService.getAllSections();
        return ResponseEntity.ok(sections);
    }

    @GetMapping("/sections/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SectionDto> getSectionById(@PathVariable Long id) {
        return sectionService.getSectionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ====================================================================
    // === MÉTHODE DE MISE À JOUR AVEC BLOC TRY-CATCH DE DÉBOGAGE       ===
    // ====================================================================
    /**
     * Met à jour le contenu (titre et contenu HTML) d'une section existante.
     * Un bloc try-catch a été ajouté pour capturer et afficher toute exception cachée.
     */
    @PutMapping("/sections/{id}/contenu")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ENSEIGNANT', 'ROLE_RESPONSABLE_FORMATION')")
    public ResponseEntity<?> updateSectionContenu(@PathVariable Long id, @Valid @RequestBody SectionUpdateRequest updateRequest) {
        
        try {
            // Message de débogage pour voir si on entre dans la méthode
            System.out.println(">>> [DÉBOGAGE] Entrée dans updateSectionContenu pour l'ID de section: " + id);
            
            SectionDto updatedSection = sectionService.updateSectionContent(id, updateRequest);
            
            // Message de débogage pour voir si le service a terminé avec succès
            System.out.println(">>> [DÉBOGAGE] Service 'updateSectionContent' exécuté avec succès. Section ID: " + updatedSection.id());
            
            return ResponseEntity.ok(updatedSection);

        } catch (Exception e) {
            // Si une exception se produit dans le bloc try, elle sera capturée ici.
            
            // On affiche un message clair dans la console du serveur.
            System.err.println("!!! [ERREUR CAPTURÉE DANS SectionController] Une exception s'est produite !!!");
            
            // On affiche la trace complète de l'erreur. C'est la partie la plus importante.
            e.printStackTrace(); 
            
            // On renvoie une réponse d'erreur 500 claire au frontend
            // pour qu'il sache que le serveur a eu un problème.
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur interne est survenue. Message : " + e.getMessage());
        }
    }

    // --- SUPPRESSION ---
    @DeleteMapping("/sections/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ENSEIGNANT', 'ROLE_RESPONSABLE_FORMATION')")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id) {
        sectionService.deleteSection(id);
        return ResponseEntity.noContent().build();
    }
}
