package com.moscepa.controller;

import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // <-- Nouvel import
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // <-- Nouvel import
import org.springframework.security.core.context.SecurityContextHolder; // <-- Nouvel import
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.moscepa.service.PermissionService;

@RestController
@RequestMapping("/api/permissions" )
@CrossOrigin(origins = "http://localhost:4200" )
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    // --- Endpoint pour l'admin (inchangé) ---
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Set<String>>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissionsByRole());
    }

    // --- Endpoint pour l'admin (inchangé) ---
    @PutMapping("/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updatePermissions(@PathVariable String roleName,
            @RequestBody Set<String> allowedFeatures) {
        permissionService.updatePermissionsForRole(roleName.toUpperCase(), allowedFeatures);
        return ResponseEntity.noContent().build();
    }

    // ====================================================================
    // ===                 NOUVEL ENDPOINT POUR LA SIDEBAR              ===
    // ====================================================================

    /**
     * Récupère la liste des 'featureKey' autorisées pour l'utilisateur
     * actuellement authentifié.
     * Cet endpoint est sécurisé par le fait qu'il ne peut lire que les
     * informations de l'utilisateur qui fait l'appel.
     * 
     * @return Un ensemble de chaînes de caractères représentant les featureKeys.
     */
    @GetMapping("/me")
    
    public ResponseEntity<Set<String>> getMyPermissions() {
        // 1. Récupérer le contexte de sécurité de l'utilisateur qui fait la requête
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. Vérifier si l'utilisateur est bien authentifié
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            // Si non authentifié, on retourne une erreur 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 3. Extraire le nom du rôle à partir des autorités de l'utilisateur
        // L'autorité est souvent sous la forme "ROLE_NOMDUROLE" (ex: "ROLE_TUTEUR")
        String userRole = authentication.getAuthorities().stream()
                .findFirst() // On prend la première autorité (en supposant qu'un utilisateur n'a qu'un seul rôle principal)
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", "")) // On retire le préfixe "ROLE_"
                .orElse(null); // Si aucune autorité n'est trouvée

        // 4. Si aucun rôle n'est trouvé, on retourne une liste vide
        if (userRole == null) {
            return ResponseEntity.ok(Set.of()); // Set.of() crée un ensemble immuable vide
        }
        
        // 5. Utiliser le service pour récupérer toutes les permissions
        Map<String, Set<String>> allPermissions = permissionService.getAllPermissionsByRole();
        
        // 6. Chercher les permissions pour le rôle de notre utilisateur et les retourner
        // getOrDefault est une sécurité : si le rôle n'a aucune permission définie, on retourne un ensemble vide
        Set<String> myPermissions = allPermissions.getOrDefault(userRole, Set.of());

        return ResponseEntity.ok(myPermissions);
    }
}
