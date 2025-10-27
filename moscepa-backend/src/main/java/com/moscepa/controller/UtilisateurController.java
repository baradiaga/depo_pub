// Fichier : src/main/java/com/moscepa/controller/UserController.java

package com.moscepa.controller;

import com.moscepa.entity.Role;
import com.moscepa.entity.Utilisateur;
import com.moscepa.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List; // <-- IMPORT AJOUTÉ
import java.util.Map;

/**
 * Contrôleur pour les opérations sur les utilisateurs.
 */
@RestController
@RequestMapping("/api/users" ) // La base de l'URL est /api/users
@CrossOrigin(origins = "*")
public class UtilisateurController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ==========================================================
    // === NOUVELLE MÉTHODE AJOUTÉE ICI ===
    // ==========================================================
    /**
     * Récupère tous les utilisateurs ayant le rôle 'ENSEIGNANT'.
     * L'URL finale sera GET /api/users/enseignants
     */
    @GetMapping("/enseignants")
    public ResponseEntity<List<Utilisateur>> getEnseignants() {
        // On suppose que votre entité Role est un Enum.
        List<Utilisateur> enseignants = utilisateurRepository.findByRole(Role.ENSEIGNANT);
        return ResponseEntity.ok(enseignants);
    }


    /**
     * Crée un utilisateur de test (votre méthode existante).
     */
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
