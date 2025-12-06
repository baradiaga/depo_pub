package com.moscepa.controller;

import com.moscepa.dto.StudentJourneyDto;
import com.moscepa.security.UserPrincipal;
import com.moscepa.service.StudentJourneyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-journey")
@CrossOrigin(origins = "*")
public class StudentJourneyController {

    private final StudentJourneyService studentJourneyService;

    public StudentJourneyController(StudentJourneyService studentJourneyService) {
        this.studentJourneyService = studentJourneyService;
    }

    /**
     * Récupère le parcours complet de l'étudiant connecté.
     */
    @GetMapping("/me")
    public ResponseEntity<StudentJourneyDto> getParcoursEtudiantConnecte(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).build();
        }

        Long utilisateurId = userPrincipal.getId();
        StudentJourneyDto parcours = studentJourneyService.getStudentJourney(utilisateurId);

        return ResponseEntity.ok(parcours);
    }

    /**
     * Récupère le parcours complet d'un étudiant spécifique par ID (pour admin ou enseignant).
     */
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentJourneyDto> getParcoursEtudiant(@PathVariable String studentId) {
        if ("all".equalsIgnoreCase(studentId)) {
            return ResponseEntity.badRequest().build(); // utiliser /all pour tout récupérer
        }

        Long id;
        try {
            id = Long.parseLong(studentId);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }

        StudentJourneyDto parcours = studentJourneyService.getStudentJourney(id);
        return ResponseEntity.ok(parcours);
    }

    /**
     * Récupère tous les parcours étudiants (admin) avec filtrage optionnel par type.
     */
    @GetMapping("/all")
    public ResponseEntity<List<StudentJourneyDto>> getAllParcours(
            @RequestParam(required = false) String type
    ) {
        List<StudentJourneyDto> journeys = studentJourneyService.getAllJourneys(type);
        return ResponseEntity.ok(journeys);
    }
}
