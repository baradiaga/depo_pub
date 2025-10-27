package com.moscepa.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority; // <-- IMPORT AJOUTÉ
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List; // <-- IMPORT AJOUTÉ
import java.util.stream.Collectors; // <-- IMPORT AJOUTÉ

/**
 * Utilitaire pour la gestion des tokens JWT
 */
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${moscepa.app.jwtSecret:mOsCePaSecretKeyForJWTTokenGenerationAndValidation2024}")
    private String jwtSecret;

    @Value("${moscepa.app.jwtExpirationMs:86400000}")
    private int jwtExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Génère un token JWT à partir de l'authentification.
     * CETTE MÉTHODE A ÉTÉ MODIFIÉE POUR INCLURE LES RÔLES.
     */
    public String generateJwtToken(Authentication authentication) {
        // On récupère l'objet principal de l'utilisateur qui contient ses détails (email, mdp, rôles)
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // 1. On extrait les rôles (authorities) de l'objet principal
        List<String> authorities = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // 2. On construit le token en y ajoutant les rôles comme une "claim" personnalisée
        return Jwts.builder()
                .setSubject(userPrincipal.getEmail()) // Le sujet du token (qui il représente)
                .claim("authorities", authorities) // <-- MODIFICATION PRINCIPALE : On ajoute les rôles ici
                .setIssuedAt(new Date()) // Date de création
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Date d'expiration
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Signature du token
                .compact();
    }

    /**
     * Génère un token JWT à partir de l'email.
     * Note : Cette méthode ne peut pas inclure les rôles car elle n'a pas accès à l'objet Authentication.
     * Elle est utile pour des cas comme la réinitialisation de mot de passe, mais pas pour la session utilisateur principale.
     */
    public String generateTokenFromEmail(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrait l'email du token JWT.
     */
    public String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Valide le token JWT.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Token JWT invalide: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expiré: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT non supporté: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string est vide: {}", e.getMessage());
        }

        return false;
    }
}