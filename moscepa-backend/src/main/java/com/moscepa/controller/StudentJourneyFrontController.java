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

    @GetMapping("/me")
    public ResponseEntity<StudentJourneyFrontDto> getParcoursEtudiantConnecteFront(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(studentJourneyService.getStudentJourneyForFrontend(userPrincipal.getId()));
    }

    @GetMapping("/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENSEIGNANT')")
    public ResponseEntity<StudentJourneyFrontDto> getParcoursEtudiantFront(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentJourneyService.getStudentJourneyForFrontend(studentId));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StudentJourneyFrontDto>> getAllParcoursFront(
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(studentJourneyService.getAllJourneysForFrontend(type));
    }
    

@GetMapping("/teacher-students")
@PreAuthorize("hasAnyRole('ADMIN', 'TUTEUR', 'ENSEIGNANT')")
public ResponseEntity<List<StudentJourneyFrontDto>> getMyStudents(
        @AuthenticationPrincipal UserPrincipal userPrincipal) {
    // On utilise l'ID du tuteur connecté via le token JWT
    return ResponseEntity.ok(studentJourneyService.getStudentsForTeacherForFrontend(userPrincipal.getId()));
}

}
