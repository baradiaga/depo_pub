// Fichier : src/main/java/com/moscepa/controller/FileUploadController.java (Nouveau Fichier)

package com.moscepa.controller;

import com.moscepa.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("/api" )
public class FileUploadController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);

        // On construit l'URL de téléchargement du fichier
        // Pour l'instant, on retourne juste le chemin qui sera stocké en BDD
        // Exemple de retour : { "filePath": "/uploads/fichier-unique.pdf" }
        String filePath = "/uploads/" + fileName; // Ce chemin sera stocké en BDD

        return ResponseEntity.ok(Map.of("filePath", filePath));
    }
}
