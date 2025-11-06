// Fichier : src/main/java/com/moscepa/controller/UtilisateurController.java (Corrigé et Final)

package com.moscepa.controller;

import com.moscepa.dto.ElementConstitutifResponseDto;
import com.moscepa.dto.UserResponseDto;
import com.moscepa.entity.Role;
import com.moscepa.entity.Utilisateur;
import com.moscepa.repository.UtilisateurRepository;
import com.moscepa.security.UserPrincipal; // <-- IMPORT MANQUANT AJOUTÉ
import com.moscepa.service.EtudiantService; // <-- IMPORT MANQUANT AJOUTÉ
import com.moscepa.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // <-- IMPORT MANQUANT AJOUTÉ
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users" )
@CrossOrigin(origins = "*")
public class UtilisateurController {

    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private UserManagementService userManagementService;
    
    // ====================================================================
    // === INJECTION MANQUANTE AJOUTÉE ICI                              ===
    // ====================================================================
    @Autowired
    private EtudiantService etudiantService;

    @GetMapping("/enseignants")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESPONSABLE_FORMATION', 'ROLE_ENSEIGNANT')")
    public ResponseEntity<List<UserResponseDto>> getEnseignants() {
        List<UserResponseDto> enseignants = userManagementService.getAllEnseignantsActifs();
        return ResponseEntity.ok(enseignants);
    }

    @GetMapping("/etudiants")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESPONSABLE_FORMATION')")
    public ResponseEntity<List<UserResponseDto>> getEtudiants() {
        List<UserResponseDto> etudiants = userManagementService.getUsersByRole(Role.ETUDIANT);
        return ResponseEntity.ok(etudiants);
    }

    // ====================================================================
    // === NOUVEL ENDPOINT AJOUTÉ CORRECTEMENT                          ===
    // ====================================================================
    @GetMapping("/mes-inscriptions")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<List<ElementConstitutifResponseDto>> getMesInscriptions(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long utilisateurId = userPrincipal.getId();
        
        List<ElementConstitutifResponseDto> matieresInscrites = etudiantService.getMatieresInscrites(utilisateurId);
        
        return ResponseEntity.ok(matieresInscrites);
    }

    @PostMapping("/create-test-user")
    public ResponseEntity<?> createTestUser() {
        try {
            if (utilisateurRepository.existsByEmail("admin@moscepa.com")) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "L'utilisateur admin@moscepa.com existe déjà");
                return ResponseEntity.ok(response);
            }

            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setNom("Admin");
            utilisateur.setPrenom("Test");
            utilisateur.setEmail("admin@moscepa.com");
            utilisateur.setMotDePasse(passwordEncoder.encode("password123"));
            utilisateur.setRole(Role.ADMIN);
            utilisateur.setActif(true);
            utilisateur.setDateCreation(LocalDateTime.now());

            utilisateur = utilisateurRepository.save(utilisateur);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Utilisateur créé avec succès");
            response.put("email", utilisateur.getEmail());
            response.put("id", utilisateur.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la création de l'utilisateur: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
