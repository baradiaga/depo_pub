package com.moscepa.security;

import com.moscepa.entity.Utilisateur;
import com.moscepa.repository.UtilisateurRepository;

// --- IMPORTS POUR LES LOGS ---
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // --- AJOUT DU LOGGER ---
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        // --- PIÈGE N°1 : Est-ce que cette méthode est bien appelée ? ---
        logger.info("[PIÈGE - UserDetailsService] Entrée dans loadUserByUsername pour l'email : '{}'", email);

        try {
            // --- PIÈGE N°2 : La recherche en base de données ---
            logger.info("[PIÈGE - UserDetailsService] Étape 1: Recherche de l'utilisateur en base de données...");
            Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> {
                    // Cette exception est levée si l'utilisateur n'est pas trouvé
                    logger.error("[PIÈGE - UserDetailsService] ❌ ERREUR : Utilisateur NON TROUVÉ en BDD pour l'email : '{}'", email);
                    return new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email);
                });
            
            logger.info("[PIÈGE - UserDetailsService] Étape 2: Utilisateur trouvé ! ID: {}, Email: {}", utilisateur.getId(), utilisateur.getEmail());

            // --- PIÈGE N°3 : La vérification du statut "actif" ---
            if (!utilisateur.getActif()) {
                logger.error("[PIÈGE - UserDetailsService] ❌ ERREUR : Le compte de l'utilisateur '{}' est DÉSACTIVÉ.", email);
                // Cette exception va aussi interrompre le processus
                throw new UsernameNotFoundException("Compte utilisateur désactivé: " + email);
            }
            
            logger.info("[PIÈGE - UserDetailsService] Étape 3: Le compte est bien ACTIF.");

            // --- PIÈGE N°4 : La construction de l'objet UserPrincipal ---
            logger.info("[PIÈGE - UserDetailsService] Étape 4: Tentative de construction de l'objet UserPrincipal...");
            UserDetails userPrincipal = UserPrincipal.create(utilisateur);
            logger.info("[PIÈGE - UserDetailsService] ✅ SUCCÈS : UserPrincipal construit. L'utilisateur a les rôles : {}", userPrincipal.getAuthorities());

            return userPrincipal;

        } catch (UsernameNotFoundException e) {
            // On attrape les exceptions attendues pour les logger, puis on les relance
            logger.error("[PIÈGE - UserDetailsService] Exception de type UsernameNotFoundException attrapée et relancée.", e);
            throw e;
        } catch (Exception e) {
            // --- PIÈGE N°5 : L'exception cachée ! ---
            // Ce bloc attrapera toute autre erreur inattendue (ex: NullPointerException dans UserPrincipal.create)
            logger.error("[PIÈGE - UserDetailsService] ❌ ERREUR INATTENDUE (EXCEPTION CACHÉE ?) lors du traitement de l'email '{}' :", email, e);
            
            // On encapsule et on relance pour que Spring Security soit notifié de l'échec
            throw new UsernameNotFoundException("Erreur interne inattendue lors de la construction des détails de l'utilisateur.", e);
        }
    }
}
