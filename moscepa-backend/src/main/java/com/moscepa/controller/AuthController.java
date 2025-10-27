package com.moscepa.controller;

import com.moscepa.dto.LoginRequest;
import com.moscepa.dto.LoginResponse;
import com.moscepa.entity.Utilisateur;
import com.moscepa.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur REST pour l'authentification
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentification", description = "API d'authentification et de gestion des sessions")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Connexion utilisateur
     */
    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur", description = "Authentifie un utilisateur avec email et mot de passe")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Email ou mot de passe incorrect");
            error.put("error", "INVALID_CREDENTIALS");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * Déconnexion utilisateur
     */
    @PostMapping("/logout")
    @Operation(summary = "Déconnexion utilisateur", description = "Déconnecte l'utilisateur actuel")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Déconnexion réussie");
        return ResponseEntity.ok(response);
    }

    /**
     * Récupère les informations de l'utilisateur connecté
     */
    @GetMapping("/me")
    @Operation(summary = "Profil utilisateur", description = "Récupère les informations de l'utilisateur connecté")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Utilisateur currentUser = authService.getCurrentUser();
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", currentUser.getId());
            userInfo.put("email", currentUser.getEmail());
            userInfo.put("nom", currentUser.getNom());
            userInfo.put("prenom", currentUser.getPrenom());
            userInfo.put("role", currentUser.getRole());
            userInfo.put("actif", currentUser.getActif());
            
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Utilisateur non authentifié");
            error.put("error", "NOT_AUTHENTICATED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * Vérifie si l'utilisateur est connecté
     */
    @GetMapping("/check")
    @Operation(summary = "Vérifier l'authentification", description = "Vérifie si l'utilisateur est connecté")
    public ResponseEntity<?> checkAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> response = new HashMap<>();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            response.put("authenticated", true);
            response.put("username", authentication.getName());
        } else {
            response.put("authenticated", false);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Vérifie si un email existe
     */
    @GetMapping("/check-email")
    @Operation(summary = "Vérifier l'existence d'un email", description = "Vérifie si un email est déjà utilisé")
    public ResponseEntity<?> checkEmailExists(@RequestParam String email) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", authService.existsByEmail(email));
        return ResponseEntity.ok(response);
    }
}

