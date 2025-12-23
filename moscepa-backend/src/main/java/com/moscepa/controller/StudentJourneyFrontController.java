package com.moscepa.controller;

import com.moscepa.dto.StudentJourneyFrontDto;
import com.moscepa.security.UserPrincipal;
import com.moscepa.service.StudentJourneyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/front/student-journey")
@CrossOrigin(origins = "*")
public class StudentJourneyFrontController {

    private final StudentJourneyService studentJourneyService;

    public StudentJourneyFrontController(StudentJourneyService studentJourneyService) {
        this.studentJourneyService = studentJourneyService;
    }

    /**
     * Récupère le parcours complet de l'étudiant connecté (frontend).
     */
    @GetMapping("/me")
    public ResponseEntity<StudentJourneyFrontDto> getParcoursEtudiantConnecteFront(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).build();
        }

        Long utilisateurId = userPrincipal.getId();
        StudentJourneyFrontDto parcours = 
            studentJourneyService.getStudentJourneyForFrontend(utilisateurId);

        return ResponseEntity.ok(parcours);
    }

    /**
     * Récupère le parcours complet d'un étudiant spécifique (frontend).
     */
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentJourneyFrontDto> getParcoursEtudiantFront(@PathVariable String studentId) {
        if ("all".equalsIgnoreCase(studentId)) {
            return ResponseEntity.badRequest().build();
        }

        Long id;
        try {
            id = Long.parseLong(studentId);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }

        StudentJourneyFrontDto parcours = studentJourneyService.getStudentJourneyForFrontend(id);
        return ResponseEntity.ok(parcours);
    }

    /**
     * Récupère les parcours étudiants associés à l'enseignant connecté (frontend).
     */
    @GetMapping("/mes-etudiants")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ResponseEntity<List<StudentJourneyFrontDto>> getStudentsForTeacherFront(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).build();
        }
        
        Long teacherId = userPrincipal.getId();
        List<StudentJourneyFrontDto> journeys = 
            studentJourneyService.getStudentsForTeacherForFrontend(teacherId);
        
        return ResponseEntity.ok(journeys);
    }

    /**
     * Récupère tous les parcours étudiants (admin) avec filtrage optionnel par type (frontend).
     */
    @GetMapping("/all")
    public ResponseEntity<List<StudentJourneyFrontDto>> getAllParcoursFront(
            @RequestParam(required = false) String type) {
        
        List<StudentJourneyFrontDto> journeys = 
            studentJourneyService.getAllJourneysForFrontend(type);
        
        return ResponseEntity.ok(journeys);
    }
}