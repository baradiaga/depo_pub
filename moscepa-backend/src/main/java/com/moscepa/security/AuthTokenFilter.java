package com.moscepa.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Importez les exceptions spécifiques de votre bibliothèque JWT (ex: io.jsonwebtoken )
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Log initial pour suivre la requête
        logger.info("AuthTokenFilter: Traitement de la requête pour l'URL : {}", request.getRequestURI());

        try {
            String jwt = parseJwt(request);

            // Le token est-il présent ?
            if (jwt == null) {
                logger.warn("AuthTokenFilter: Aucun jeton JWT trouvé dans l'en-tête 'Authorization' pour l'URL : {}", request.getRequestURI());
            } else {
                // Si le token est présent, on tente de le valider
                if (jwtUtils.validateJwtToken(jwt)) {
                    String email = jwtUtils.getEmailFromJwtToken(jwt);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("AuthTokenFilter: Utilisateur '{}' authentifié avec succès pour l'URL : {}", email, request.getRequestURI());
                } else {
                    // Cette condition est souvent redondante si validateJwtToken lève des exceptions,
                    // mais nous la gardons pour la clarté.
                    logger.warn("AuthTokenFilter: La validation du jeton JWT a échoué (validateJwtToken a retourné false).");
                }
            }
        // --- BLOC DE CAPTURE D'EXCEPTIONS DÉTAILLÉ ---
        } catch (SignatureException e) {
            logger.error("ERREUR CACHÉE DÉTECTÉE : Signature JWT invalide ! Détails : {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("ERREUR CACHÉE DÉTECTÉE : Token JWT malformé ! Le token reçu n'est pas un JWT valide. Détails : {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("ERREUR CACHÉE DÉTECTÉE : Le token JWT a expiré ! Détails : {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("ERREUR CACHÉE DÉTECTÉE : Token JWT non supporté ! Détails : {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("ERREUR CACHÉE DÉTECTÉE : Argument invalide passé à une méthode JWT ! Détails : {}", e.getMessage());
        } catch (Exception e) {
            // Un catch-all pour toute autre erreur inattendue
            logger.error("ERREUR CRITIQUE INATTENDUE lors du traitement du jeton JWT.", e);
        }

        // Très important : la requête doit continuer sa route dans tous les cas
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
