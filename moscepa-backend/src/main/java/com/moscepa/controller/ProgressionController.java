package com.moscepa.controller;

import com.moscepa.dto.MatiereStatutDto;
import com.moscepa.security.UserPrincipal; // Assurez-vous que le chemin vers UserPrincipal est correct
import com.moscepa.service.ProgressionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/progression" )
@CrossOrigin(origins = "*") // Vous pouvez restreindre à "http://localhost:4200" pour plus de sécurité
public class ProgressionController {

    private final ProgressionService progressionService;

    // Le constructeur injecte le service
    public ProgressionController(ProgressionService progressionService ) {
        this.progressionService = progressionService;
    }

    /**
     * Endpoint pour récupérer les matières de l'étudiant actuellement connecté.
     * L'URL sera : GET /api/progression/mes-matieres
     */
    @GetMapping("/mes-matieres")
    public ResponseEntity<List<MatiereStatutDto>> getMatieresPourEtudiantConnecte(
            // Cette annotation magique injecte les détails de l'utilisateur
            // qui a été authentifié via le token JWT.
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        // Sécurité : on vérifie que l'utilisateur est bien authentifié.
        if (userPrincipal == null) {
            // Renvoie une erreur 401 si personne n'est connecté.
            return ResponseEntity.status(401).build(); 
        }

        // On récupère l'ID de l'utilisateur à partir de l'objet Principal.
        Long utilisateurId = userPrincipal.getId(); 

        // On appelle le service avec l'ID de l'utilisateur connecté.
        List<MatiereStatutDto> matieres = progressionService.findMatieresByEtudiant(utilisateurId);
        
        // On renvoie la liste des matières avec un statut 200 OK.
        return ResponseEntity.ok(matieres);
    }
}
