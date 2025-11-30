// Fichier : src/main/java/com/moscepa/controller/RessourcePedagogiqueController.java

package com.moscepa.controller;

import com.moscepa.entity.RessourcePedagogique;
import com.moscepa.service.RessourcePedagogiqueService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/ressources" )
@CrossOrigin(origins = "http://localhost:4200" )
public class RessourcePedagogiqueController {

    private final RessourcePedagogiqueService ressourceService;

    public RessourcePedagogiqueController(RessourcePedagogiqueService ressourceService) {
        this.ressourceService = ressourceService;
    }

    // ====================================================================
    // === TÉLÉVERSEMENT (POST)                                         ===
    // ====================================================================

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<RessourcePedagogique> televerserRessource(
            @RequestPart("fichier") MultipartFile fichier,
            @RequestPart("metadata") String metadataJson) {
        try {
            RessourcePedagogique ressource = ressourceService.televerserEtSauvegarder(fichier, metadataJson);
            return new ResponseEntity<>(ressource, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ====================================================================
    // === LECTURE (GET)                                                ===
    // ====================================================================

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RessourcePedagogique>> getAllRessources() {
        List<RessourcePedagogique> ressources = ressourceService.findAll();
        return ResponseEntity.ok(ressources);
    }

    // ====================================================================
    // === TÉLÉCHARGEMENT (GET)                                         ===
    // ====================================================================

    @GetMapping("/telecharger/{nomFichierStocke}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> telechargerRessource(@PathVariable String nomFichierStocke) {
        Resource file = ressourceService.chargerFichierCommeRessource(nomFichierStocke);
        RessourcePedagogique ressource = ressourceService.findAll().stream()
                .filter(r -> r.getCheminStockage().equals(nomFichierStocke))
                .findFirst()
                .orElse(null);

        String nomFichierOriginal = (ressource != null) ? ressource.getNomFichier() : nomFichierStocke;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(ressource.getTypeMime()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomFichierOriginal + "\"")
                .body(file);
    }

    // ====================================================================
    // === SUPPRESSION (DELETE)                                         ===
    // ====================================================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<Void> supprimerRessource(@PathVariable Long id) {
        ressourceService.supprimerRessource(id);
        return ResponseEntity.noContent().build();
    }
}
