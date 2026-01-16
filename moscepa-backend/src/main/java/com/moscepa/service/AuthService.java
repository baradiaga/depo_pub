package com.moscepa.service;
import com.moscepa.dto.ProfileUpdateRequest;

import com.moscepa.dto.LoginRequest;
import com.moscepa.dto.LoginResponse;
import com.moscepa.entity.Utilisateur;
import com.moscepa.repository.UtilisateurRepository;
import com.moscepa.security.JwtUtils;
import com.moscepa.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service pour la gestion de l'authentification
 */
@Service
@Transactional
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Authentifie un utilisateur et retourne un token JWT
     */
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        // Authentification avec Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getMotDePasse()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Génération du token JWT
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Récupération des informations utilisateur
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return new LoginResponse(
                jwt,
                utilisateur.getId(),
                utilisateur.getEmail(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getRole()
        );
    }

    /**
     * Vérifie si un email existe déjà
     */
    public boolean existsByEmail(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    /**
     * Encode un mot de passe
     */
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Récupère l'utilisateur actuellement connecté
     */
    public Utilisateur getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return utilisateurRepository.findByEmail(userPrincipal.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        }
        throw new RuntimeException("Aucun utilisateur connecté");
    }

    /**
     * Vérifie si l'utilisateur actuel a une permission spécifique
     */
    public boolean hasPermission(String permissionName) {
        try {
            Utilisateur currentUser = getCurrentUser();
            return currentUser.getPermissions().stream()
                    .anyMatch(permission -> permission.getNom().equals(permissionName));
        } catch (Exception e) {
            return false;
        }
    }

    public Utilisateur updateProfile(ProfileUpdateRequest request) {

    Utilisateur currentUser = getCurrentUser();

    if (!currentUser.getEmail().equals(request.getEmail()) && 
        utilisateurRepository.existsByEmail(request.getEmail())) {
        throw new RuntimeException("Cet email est déjà utilisé par un autre compte.");
    }

    currentUser.setNom(request.getNom());
    currentUser.setPrenom(request.getPrenom());
    currentUser.setEmail(request.getEmail());

    if (request.getNouveauMotDePasse() != null && !request.getNouveauMotDePasse().isEmpty()) {
        if (request.getMotDePasseActuel() != null && !request.getMotDePasseActuel().isEmpty()) {
            if (!passwordEncoder.matches(request.getMotDePasseActuel(), currentUser.getMotDePasse())) {
                throw new RuntimeException("Le mot de passe actuel est incorrect.");
            }
        }
        currentUser.setMotDePasse(passwordEncoder.encode(request.getNouveauMotDePasse()));
    }

    return utilisateurRepository.save(currentUser);
}

}

