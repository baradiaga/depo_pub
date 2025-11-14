// Fichier : src/main/java/com/moscepa/controller/UtilisateurController.java (Version Finale et Corrigée)

package com.moscepa.controller;

import com.moscepa.dto.MatiereInscriteDto;
import com.moscepa.dto.UserResponseDto;
import com.moscepa.service.EtudiantService;
import com.moscepa.service.UserManagementService;
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
@RequestMapping("/api/users" )
@CrossOrigin(origins = "*")
public class UtilisateurController {

    @Autowired
    private EtudiantService etudiantService;

    @Autowired
    private UserManagementService userManagementService; // L'injection est bien là

    /**
     * Récupère la liste des matières inscrites pour l'étudiant connecté.
     */
    @GetMapping("/mes-inscriptions")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<List<MatiereInscriteDto>> getMesInscriptions(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long utilisateurId = userPrincipal.getId();
        List<MatiereInscriteDto> matieresInscrites = etudiantService.getMatieresInscrites(utilisateurId);
        return ResponseEntity.ok(matieresInscrites);
    }

    /**
     * Récupère la liste de tous les utilisateurs ayant le rôle ENSEIGNANT.
     */
    @GetMapping("/enseignants")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getEnseignants() {
        
        List<UserResponseDto> enseignants = userManagementService.getAllEnseignantsActifs();
        
        return ResponseEntity.ok(enseignants);
    }
}
