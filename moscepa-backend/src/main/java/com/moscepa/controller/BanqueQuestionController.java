// Fichier : src/main/java/com/moscepa/controller/BanqueQuestionController.java

package com.moscepa.controller;

import com.moscepa.dto.BanqueQuestionCreationDto;
import com.moscepa.dto.BanqueQuestionDetailDto;
import com.moscepa.service.BanqueQuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banque-questions" )
@CrossOrigin(origins = "http://localhost:4200" )
public class BanqueQuestionController {

    private final BanqueQuestionService banqueQuestionService;

    public BanqueQuestionController(BanqueQuestionService banqueQuestionService) {
        this.banqueQuestionService = banqueQuestionService;
    }

    // ====================================================================
    // === CRÉATION ET MISE À JOUR                                      ===
    // ====================================================================

    @PostMapping
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<BanqueQuestionDetailDto> creerQuestion(@Valid @RequestBody BanqueQuestionCreationDto dto) {
        BanqueQuestionDetailDto nouvelleQuestion = banqueQuestionService.creerQuestion(dto);
        return new ResponseEntity<>(nouvelleQuestion, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<BanqueQuestionDetailDto> mettreAJourQuestion(@PathVariable Long id, @Valid @RequestBody BanqueQuestionCreationDto dto) {
        BanqueQuestionDetailDto questionMiseAJour = banqueQuestionService.mettreAJourQuestion(id, dto);
        return ResponseEntity.ok(questionMiseAJour);
    }

    // ====================================================================
    // === LECTURE ET RECHERCHE                                         ===
    // ====================================================================

    @GetMapping
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<List<BanqueQuestionDetailDto>> getAllQuestions() {
        List<BanqueQuestionDetailDto> questions = banqueQuestionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<BanqueQuestionDetailDto> getQuestionById(@PathVariable Long id) {
        BanqueQuestionDetailDto question = banqueQuestionService.getQuestionById(id);
        return ResponseEntity.ok(question);
    }

    
    // ====================================================================
    // === SUPPRESSION                                                  ===
    // ====================================================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMIN')")
    public ResponseEntity<Void> supprimerQuestion(@PathVariable Long id) {
        banqueQuestionService.supprimerQuestion(id);
        return ResponseEntity.noContent().build();
    }

    
}
