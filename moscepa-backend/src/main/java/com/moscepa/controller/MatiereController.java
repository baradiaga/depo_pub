// Fichier : com/moscepa/controller/MatiereController.java (Version Temporaire)

package com.moscepa.controller;

import com.moscepa.entity.Matiere;
import com.moscepa.repository.MatiereRepository;
import com.moscepa.service.MatiereService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList; // <-- IMPORT AJOUTÉ
import java.util.List;
import java.util.stream.Collectors;

record ChapitrePourSyllabusDto(Long id, String nom, Integer niveau, String objectif, Integer numero, String resultat, String categorie ) {}
record MatiereDetailDto(Long id, String nom, String ec, Integer ordre, Double coefficient, List<ChapitrePourSyllabusDto> chapitres) {}

@RestController
@RequestMapping("/api/matieres")
@CrossOrigin(origins = "*")
@Tag(name = "Matières", description = "API de gestion des matières et de leurs détails")
public class MatiereController {

    @Autowired
    private MatiereService matiereService;

    @Autowired
    private MatiereRepository matiereRepository;

    // --- Les endpoints GET, POST, etc. restent inchangés ---
    @GetMapping({"", "/details"})
    @Operation(summary = "Récupérer les détails de toutes les matières avec leurs chapitres")
    public ResponseEntity<List<MatiereDetailDto>> getAllMatiereDetails() {
        List<MatiereDetailDto> dtos = matiereRepository.findAll().stream().map(this::mapToMatiereDetailDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer les détails complets d'une matière par son ID")
    public ResponseEntity<MatiereDetailDto> getMatiereDetailsById(@PathVariable Long id) {
        return matiereRepository.findById(id).map(this::mapToMatiereDetailDto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/noms")
    @Operation(summary = "Récupérer les noms des matières disponibles")
    public ResponseEntity<List<String>> getMatieresDisponibles() {
        List<String> nomsMatieres = matiereService.getMatieresDisponibles();
        return new ResponseEntity<>(nomsMatieres, HttpStatus.OK);
    }

    record CreateMatiereDto(@NotBlank(message = "Le nom de la matière est obligatoire") String nom) {}

    @PostMapping
    @Operation(summary = "Créer une nouvelle matière")
    public ResponseEntity<?> createMatiere(@Valid @RequestBody CreateMatiereDto createMatiereDto) {
        if (matiereRepository.existsByNom(createMatiereDto.nom())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erreur : Une matière avec le nom '" + createMatiereDto.nom() + "' existe déjà.");
        }
        Matiere nouvelleMatiere = new Matiere();
        nouvelleMatiere.setNom(createMatiereDto.nom());
        Matiere matiereSauvegardee = matiereRepository.save(nouvelleMatiere);
        return new ResponseEntity<>(matiereSauvegardee, HttpStatus.CREATED);
    }

    // --- MÉTHODE DE MAPPING PRIVÉE ---
    private MatiereDetailDto mapToMatiereDetailDto(Matiere matiere) {
        // ====================================================================
        // === CORRECTION TEMPORAIRE APPLIQUÉE ICI                          ===
        // ====================================================================
        // On commente l'ancien code qui ne compile plus.
        /*
        List<ChapitrePourSyllabusDto> chapitreDtos = matiere.getChapitres().stream()
            .map(chapitre -> new ChapitrePourSyllabusDto(
                chapitre.getId(),
                chapitre.getNom(),
                chapitre.getNiveau(),
                chapitre.getObjectif(),
                chapitre.getNumero(),
                null,
                "Général"
            ))
            .collect(Collectors.toList());
        */
        
        // On crée une liste vide pour que le constructeur soit valide.
        List<ChapitrePourSyllabusDto> chapitreDtos = new ArrayList<>();
        // ====================================================================

        return new MatiereDetailDto(
            matiere.getId(),
            matiere.getNom(),
            matiere.getEc(),
            matiere.getOrdre(),
            matiere.getCoefficient(),
            chapitreDtos
        );
    }
}
