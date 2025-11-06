// Fichier : src/main/java/com/moscepa/controller/UtilisateurController.java (Avec la mise à jour)

package com.moscepa.controller;

import com.moscepa.dto.MatiereInscriteDto; // <-- IMPORT IMPORTANT
import com.moscepa.service.EtudiantService;
import com.moscepa.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users" ) // Assurez-vous que ce chemin est correct
@CrossOrigin(origins = "*")
public class UtilisateurController {

    @Autowired
    private EtudiantService etudiantService;

    // ... (gardez vos autres injections et méthodes de contrôleur si vous en avez)

    // ====================================================================
    // === ENDPOINT MIS À JOUR POUR LE CURRICULUM DE L'ÉTUDIANT          ===
    // ====================================================================
    /**
     * Récupère la liste des matières inscrites pour l'utilisateur (étudiant) actuellement connecté.
     * Renvoie des données enrichies pour l'affichage dans le tableau du curriculum.
     */
    @GetMapping("/mes-inscriptions")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<List<MatiereInscriteDto>> getMesInscriptions(Authentication authentication) {
        // 1. Récupérer l'ID de l'utilisateur connecté à partir du token
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long utilisateurId = userPrincipal.getId();
        
        // 2. Appeler le service qui renvoie maintenant la liste des DTO enrichis
        List<MatiereInscriteDto> matieresInscrites = etudiantService.getMatieresInscrites(utilisateurId);
        
        // 3. Renvoyer la liste (même si elle est vide) avec un statut 200 OK
        return ResponseEntity.ok(matieresInscrites);
    }
}
