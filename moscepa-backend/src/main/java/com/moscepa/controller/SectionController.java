package com.moscepa.controller;

import com.moscepa.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sections" )
@CrossOrigin(origins = "*")
public class SectionController {

    @Autowired
    private SectionRepository sectionRepository;

    /**
     * Met à jour le contenu d'une section spécifique.
     * Le frontend appellera PUT /api/sections/{id} avec un corps JSON comme : { "contenu": "..." }
     *
     * @param id L'ID de la section à mettre à jour.
     * @param payload Le corps de la requête contenant le nouveau contenu.
     * @return Une réponse HTTP 200 (OK) si la mise à jour réussit, ou 404 (Not Found).
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateSectionContent(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String contenu = payload.get("contenu");
        if (contenu == null) {
            return ResponseEntity.badRequest().build(); // Le contenu est manquant
        }

        return sectionRepository.findById(id)
            .map(section -> {
                section.setContenu(contenu); // Mettre à jour le champ contenu
                sectionRepository.save(section); // Sauvegarder les changements
                return ResponseEntity.ok().<Void>build(); // Retourner une réponse 200 OK
            })
            .orElse(ResponseEntity.notFound().build()); // Retourner 404 si la section n'existe pas
    }
}
