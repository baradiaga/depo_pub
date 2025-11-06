// Fichier : src/main/java/com/moscepa/controller/SyllabusController.java (Nouveau Fichier)

package com.moscepa.controller;

import com.moscepa.dto.MatiereSyllabusDto;
import com.moscepa.security.UserPrincipal;
import com.moscepa.service.SyllabusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/syllabus" ) // L'URL de base pour ce contrôleur
@CrossOrigin(origins = "*")
public class SyllabusController {

    @Autowired
    private SyllabusService syllabusService;

    /**
     * Endpoint pour récupérer les détails du syllabus d'une matière pour l'étudiant connecté.
     * @param ecId L'ID de l'ElementConstitutif (matière) passé dans l'URL.
     * @param authentication L'objet d'authentification fourni par Spring Security.
     * @return Un ResponseEntity contenant le DTO du syllabus.
     */
    @GetMapping("/matiere/{ecId}")
    @PreAuthorize("hasRole('ETUDIANT')") // Seuls les étudiants peuvent accéder à cet endpoint
    public ResponseEntity<MatiereSyllabusDto> getSyllabusPourMatiere(
            @PathVariable Long ecId,
            Authentication authentication) {
        
        // 1. Récupérer l'ID de l'utilisateur à partir du token de sécurité
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long utilisateurId = userPrincipal.getId();
        
        // 2. Appeler le service pour construire le DTO du syllabus
        MatiereSyllabusDto syllabusDto = syllabusService.getSyllabusPourEtudiant(ecId, utilisateurId);
        
        // 3. Renvoyer la réponse avec un statut 200 OK
        return ResponseEntity.ok(syllabusDto);
    }
}
