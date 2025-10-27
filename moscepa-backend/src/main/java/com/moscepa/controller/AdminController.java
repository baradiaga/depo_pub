package com.moscepa.controller;

import com.moscepa.dto.UserRegistrationDto;
import com.moscepa.dto.UserResponseDto;
import com.moscepa.entity.Role;
import com.moscepa.service.UserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour l'administration des utilisateurs
 * Accessible uniquement aux administrateurs
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@Tag(name = "Administration", description = "API d'administration des utilisateurs (réservée aux administrateurs)")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @Autowired
    private UserManagementService userManagementService;

    /**
     * Crée un nouvel utilisateur
     */
    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un utilisateur", description = "Crée un nouvel utilisateur (réservé aux administrateurs)")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        try {
            UserResponseDto createdUser = userManagementService.createUser(userRegistrationDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Utilisateur créé avec succès");
            response.put("user", createdUser);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("error", "USER_CREATION_FAILED");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Récupère tous les utilisateurs
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister tous les utilisateurs", description = "Récupère la liste de tous les utilisateurs")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userManagementService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Récupère un utilisateur par son ID
     */
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Récupérer un utilisateur", description = "Récupère un utilisateur par son ID")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userManagementService.getUserById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Met à jour un utilisateur
     */
    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour un utilisateur", description = "Met à jour les informations d'un utilisateur")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        try {
            UserResponseDto updatedUser = userManagementService.updateUser(id, userRegistrationDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Utilisateur mis à jour avec succès");
            response.put("user", updatedUser);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("error", "USER_UPDATE_FAILED");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Désactive un utilisateur
     */
    @PatchMapping("/users/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Désactiver un utilisateur", description = "Désactive un utilisateur")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        try {
            userManagementService.deactivateUser(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Utilisateur désactivé avec succès");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("error", "USER_DEACTIVATION_FAILED");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Active un utilisateur
     */
    @PatchMapping("/users/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activer un utilisateur", description = "Active un utilisateur")
    public ResponseEntity<?> activateUser(@PathVariable Long id) {
        try {
            userManagementService.activateUser(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Utilisateur activé avec succès");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("error", "USER_ACTIVATION_FAILED");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Supprime un utilisateur
     */
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un utilisateur", description = "Supprime définitivement un utilisateur")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userManagementService.deleteUser(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Utilisateur supprimé avec succès");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("error", "USER_DELETION_FAILED");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Récupère les utilisateurs par rôle
     */
    @GetMapping("/users/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister les utilisateurs par rôle", description = "Récupère tous les utilisateurs d'un rôle spécifique")
    public ResponseEntity<List<UserResponseDto>> getUsersByRole(@PathVariable Role role) {
        List<UserResponseDto> users = userManagementService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    /**
     * Recherche des utilisateurs
     */
    @GetMapping("/users/search")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Rechercher des utilisateurs", description = "Recherche des utilisateurs par nom ou prénom")
    public ResponseEntity<List<UserResponseDto>> searchUsers(@RequestParam String q) {
        List<UserResponseDto> users = userManagementService.searchUsers(q);
        return ResponseEntity.ok(users);
    }

    /**
     * Statistiques des utilisateurs
     */
    @GetMapping("/users/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Statistiques des utilisateurs", description = "Récupère les statistiques des utilisateurs par rôle")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        
        for (Role role : Role.values()) {
            stats.put(role.name().toLowerCase(), userManagementService.countUsersByRole(role));
        }
        
        stats.put("total", userManagementService.getAllUsers().size());
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Vérifie si un email existe
     */
    @GetMapping("/users/check-email")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Vérifier l'existence d'un email", description = "Vérifie si un email est déjà utilisé")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam String email) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", userManagementService.emailExists(email));
        return ResponseEntity.ok(response);
    }
}

