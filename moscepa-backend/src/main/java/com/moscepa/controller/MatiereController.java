package com.moscepa.controller;

import com.moscepa.entity.Chapitre;
import com.moscepa.entity.Matiere;
import com.moscepa.repository.MatiereRepository;
import com.moscepa.service.MatiereService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; // <-- IMPORT IMPORTANT
import jakarta.validation.constraints.NotBlank; // <-- IMPORT IMPORTANT
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// --- DTOs (Data Transfer Objects ) ---

record ChapitrePourSyllabusDto(
    Long id, String nom, Integer niveau, String objectif,
    Integer numero, String resultat, String categorie
) {}

record MatiereDetailDto(
    Long id, String nom, String ec, Integer ordre,
    Double coefficient, List<ChapitrePourSyllabusDto> chapitres
) {}


@RestController
@RequestMapping("/api/matieres")
@CrossOrigin(origins = "*")
@Tag(name = "Matières", description = "API de gestion des matières et de leurs détails")
public class MatiereController {

    @Autowired
    private MatiereService matiereService;

    @Autowired
    private MatiereRepository matiereRepository;

    // --- ENDPOINTS GET (LECTURE) ---

    @GetMapping({"", "/details"})
    @Operation(summary = "Récupérer les détails de toutes les matières avec leurs chapitres")
    public ResponseEntity<List<MatiereDetailDto>> getAllMatiereDetails() {
        List<MatiereDetailDto> dtos = matiereRepository.findAll().stream()
            .map(this::mapToMatiereDetailDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer les détails complets d'une matière par son ID")
    public ResponseEntity<MatiereDetailDto> getMatiereDetailsById(@PathVariable Long id) {
        return matiereRepository.findById(id)
            .map(this::mapToMatiereDetailDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/noms")
    @Operation(summary = "Récupérer les noms des matières disponibles")
    public ResponseEntity<List<String>> getMatieresDisponibles() {
        List<String> nomsMatieres = matiereService.getMatieresDisponibles();
        return new ResponseEntity<>(nomsMatieres, HttpStatus.OK);
    }

    // ====================================================================
    // --- NOUVELLE FONCTIONNALITÉ : AJOUTER UNE MATIÈRE (POST) ---
    // C'est cette méthode qui manquait.
    // ====================================================================

    /**
     * DTO pour la création d'une nouvelle matière.
     * On ne demande QUE le nom, car c'est tout ce que le formulaire envoie.
     */
    record CreateMatiereDto(
        @NotBlank(message = "Le nom de la matière est obligatoire") String nom
    ) {}

    /**
     * Endpoint pour créer une nouvelle matière.
     * URL: POST /api/matieres
     * @param createMatiereDto Les données de la matière à créer (juste le nom).
     * @return La nouvelle matière créée avec son ID.
     */
    @PostMapping
    @Operation(summary = "Créer une nouvelle matière")
    public ResponseEntity<?> createMatiere(@Valid @RequestBody CreateMatiereDto createMatiereDto) {
        // Vérifier si une matière avec le même nom existe déjà
        if (matiereRepository.existsByNom(createMatiereDto.nom())) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT) // Code 409
                .body("Erreur : Une matière avec le nom '" + createMatiereDto.nom() + "' existe déjà.");
        }

        // Créer une nouvelle entité Matiere à partir du DTO
        Matiere nouvelleMatiere = new Matiere();
        nouvelleMatiere.setNom(createMatiereDto.nom());

        // Sauvegarder la nouvelle matière dans la base de données
        Matiere matiereSauvegardee = matiereRepository.save(nouvelleMatiere);

        // Retourner la nouvelle matière (avec son ID) et un statut 201 Created
        return new ResponseEntity<>(matiereSauvegardee, HttpStatus.CREATED);
    }


    // --- MÉTHODE DE MAPPING PRIVÉE ---

    private MatiereDetailDto mapToMatiereDetailDto(Matiere matiere) {
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
