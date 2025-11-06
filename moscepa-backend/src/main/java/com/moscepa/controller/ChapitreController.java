package com.moscepa.controller;

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

    /**
     * Crée un chapitre à partir d'un payload contenant le nom de la matière.
     * C'est cette méthode que votre composant va appeler.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ENSEIGNANT')")
    public ResponseEntity<ChapitreDetailDto> createChapitreFromPayload(@Valid @RequestBody ChapitrePayload payload) {
        ChapitreDetailDto chapitreCree = chapitreService.creerChapitreAvecNomMatiere(payload);
        return new ResponseEntity<>(chapitreCree, HttpStatus.CREATED);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ROLE_ENSEIGNANT')")
    public ResponseEntity<ChapitreDetailDto> findChapitreByMatiereAndNiveau(
            @RequestParam String matiere, 
            @RequestParam Integer niveau) {
        return chapitreService.findByMatiereNomAndNiveau(matiere, niveau)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{chapitreId}/questions")
    @PreAuthorize("hasAuthority('ROLE_ENSEIGNANT')")
    public ResponseEntity<List<QuestionDto>> getQuestionsPourChapitre(@PathVariable Long chapitreId) {
        List<QuestionDto> questions = chapitreService.getQuestionsPourChapitre(chapitreId);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ENSEIGNANT')")
    public ResponseEntity<ChapitreDetailDto> getChapitreById(@PathVariable Long id) {
        return chapitreService.getChapitreDetailsById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
