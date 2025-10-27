package com.moscepa.exception; // Ou le package de votre choix

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire d'exceptions global pour toute l'application.
 * Il intercepte les exceptions non capturées dans les contrôleurs
 * et formate une réponse JSON standardisée.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // Utilisation d'un logger pour afficher les erreurs dans la console du serveur. C'est mieux que e.printStackTrace( ).
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * C'est le "try-catch" géant que vous vouliez.
     * Il attrape N'IMPORTE QUELLE exception qui n'a pas été gérée ailleurs.
     * C'est lui qui va nous révéler la VRAIE erreur.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        
        // ====================================================================
        // ===> LA PARTIE LA PLUS IMPORTANTE : AFFICHER LA VRAIE ERREUR <====
        // ====================================================================
        // Ceci affichera la trace complète de l'erreur dans votre console Spring Boot.
        logger.error("UNE ERREUR NON GÉRÉE EST SURVENUE : ", ex);
        // ====================================================================

        // On prépare une réponse JSON propre pour le front-end.
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        // On inclut le message de la VRAIE exception dans la réponse.
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Vous pouvez ajouter d'autres handlers pour des exceptions spécifiques plus tard
    /*
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        logger.warn("Entité non trouvée : {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
    */
}
