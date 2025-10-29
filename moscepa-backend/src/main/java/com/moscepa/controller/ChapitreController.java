package com.moscepa.controller;

import com.moscepa.dto.ChapitreCreateDto;
import com.moscepa.dto.ChapitreDetailDto;
import com.moscepa.dto.QuestionDto;
import com.moscepa.entity.Chapitre;
import com.moscepa.repository.ChapitreRepository;
import com.moscepa.service.ChapitreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jakarta.persistence.EntityNotFoundException;

// On garde ce record local pour la simplicité de la liste
record ChapitreListDto(Long id, String nom, String matiere ) {}

@RestController
@RequestMapping("/api/chapitres")
@CrossOrigin(origins = "*")
public class ChapitreController {

    private final ChapitreRepository chapitreRepository;
    private final ChapitreService chapitreService;

    @Autowired
    public ChapitreController(ChapitreRepository chapitreRepository, ChapitreService chapitreService) {
        this.chapitreRepository = chapitreRepository;
        this.chapitreService = chapitreService;
    }

    /**
     * Endpoint qui retourne une liste de chapitres.
     * NOTE: On utilise la méthode du service pour simplifier.
     */
    @GetMapping
    public List<ChapitreDetailDto> getAllChapitres(@RequestParam(required = false) Long matiereId) {
        // Pour l'instant, on retourne tous les chapitres, le filtrage par matiereId sera ajouté plus tard si nécessaire.
        // La méthode findAll du service retourne les DTOs complets.
        return chapitreService.findAll();
    }

    /**
     * Endpoint pour créer un nouveau chapitre.
     */
    @PostMapping
    public ResponseEntity<ChapitreDetailDto> createChapitre(@Valid @RequestBody ChapitreCreateDto dtoDeCreation) {
        ChapitreDetailDto chapitreDto = chapitreService.creerChapitre(dtoDeCreation);
        return new ResponseEntity<>(chapitreDto, HttpStatus.CREATED);
    }

    /**
     * Recherche un chapitre par matière et niveau.
     */
    @GetMapping("/search")
    public ResponseEntity<ChapitreDetailDto> findChapitreByMatiereAndNiveau(
            @RequestParam String matiere, @RequestParam Integer niveau) {
        
        return chapitreRepository.findByMatiereNomAndNiveau(matiere, niveau)
            .map(chapitre -> ResponseEntity.ok(new ChapitreDetailDto(chapitre)))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupère toutes les questions associées à un chapitre spécifique.
     */
    @GetMapping("/{chapitreId}/questions")
    public ResponseEntity<List<QuestionDto>> getQuestionsPourChapitre(@PathVariable Long chapitreId) {
        return chapitreRepository.findById(chapitreId)
            .map(chapitre -> {
                List<QuestionDto> toutesLesQuestions = chapitre.getQuestionnaires().stream()
                    .flatMap(questionnaire -> questionnaire.getQuestions().stream())
                    .map(QuestionDto::new)
                    .collect(Collectors.toList());
                return ResponseEntity.ok(toutesLesQuestions);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint pour récupérer les détails d'un chapitre par son ID.
     */
    /**
     * Endpoint pour récupérer les détails d'un chapitre par son ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChapitreDetailDto> getChapitreById(@PathVariable Long id) {
        return chapitreService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint pour mettre à jour un chapitre existant.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ChapitreDetailDto> updateChapitre(@PathVariable Long id, @Valid @RequestBody ChapitreCreateDto dto) {
        try {
            ChapitreDetailDto updatedChapitre = chapitreService.updateChapitre(id, dto);
            return ResponseEntity.ok(updatedChapitre);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint pour supprimer un chapitre.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChapitre(@PathVariable Long id) {
        try {
            chapitreService.deleteChapitre(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
