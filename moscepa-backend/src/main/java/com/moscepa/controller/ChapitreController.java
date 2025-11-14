// Fichier : src/main/java/com/moscepa/controller/ChapitreController.java (Version mise à jour)

package com.moscepa.controller;

import com.moscepa.dto.ChapitreAvecSectionsDto;
import com.moscepa.dto.ChapitreDetailDto;
import com.moscepa.dto.ChapitrePayload;
import com.moscepa.dto.QuestionDto;
import com.moscepa.service.ChapitreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chapitres" )
public class ChapitreController {

    @Autowired
    private ChapitreService chapitreService;

    // --- VOS ENDPOINTS EXISTANTS (INCHANGÉS) ---

    @GetMapping("/search/complet")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ENSEIGNANT', 'ROLE_RESPONSABLE_FORMATION')")
    public ResponseEntity<ChapitreAvecSectionsDto> findChapitreComplet(
            @RequestParam String matiere, 
            @RequestParam Integer niveau) {
        return chapitreService.findChapitreCompletByMatiereAndNiveau(matiere, niveau)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ENSEIGNANT', 'ROLE_RESPONSABLE_FORMATION')")
    public ResponseEntity<ChapitreDetailDto> findChapitreSimple(
            @RequestParam String matiere, 
            @RequestParam Integer niveau) {
        return chapitreService.findByMatiereNomAndNiveau(matiere, niveau)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/details")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChapitreDetailDto> getChapitreDetailsPourTest(@PathVariable Long id) {
        ChapitreDetailDto dto = chapitreService.getChapitreDetailsPourTest(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ENSEIGNANT', 'ROLE_RESPONSABLE_FORMATION')")
    public ResponseEntity<ChapitreDetailDto> createChapitreFromPayload(@Valid @RequestBody ChapitrePayload payload) {
        ChapitreDetailDto chapitreCree = chapitreService.creerChapitreAvecNomMatiere(payload);
        return new ResponseEntity<>(chapitreCree, HttpStatus.CREATED);
    }

    @GetMapping("/{chapitreId}/questions")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ENSEIGNANT', 'ROLE_RESPONSABLE_FORMATION')")
    public ResponseEntity<List<QuestionDto>> getQuestionsPourChapitre(@PathVariable Long chapitreId) {
        List<QuestionDto> questions = chapitreService.getQuestionsPourChapitre(chapitreId);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ENSEIGNANT', 'ROLE_RESPONSABLE_FORMATION')")
    public ResponseEntity<ChapitreDetailDto> getChapitreById(@PathVariable Long id) {
        return chapitreService.getChapitreDetailsById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ====================================================================
    // === NOUVEL ENDPOINT AJOUTÉ POUR LA PAGE DE DÉTAIL DE L'ÉTUDIANT   ===
    // ====================================================================
    /**
     * Récupère les détails complets d'un chapitre, y compris le contenu de toutes ses sections.
     * Cet endpoint est destiné à la page où l'étudiant consulte un cours.
     * @param id L'ID du chapitre à récupérer.
     * @return Un DTO contenant le chapitre et la liste de ses sections détaillées.
     */
    @GetMapping("/{id}/details-complets")
    @PreAuthorize("isAuthenticated()") // Tout utilisateur connecté peut voir le contenu d'un cours
    public ResponseEntity<ChapitreAvecSectionsDto> getChapitreCompletById(@PathVariable Long id) {
        return chapitreService.findChapitreCompletById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
