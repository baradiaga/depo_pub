package com.moscepa.controller;

import com.moscepa.dto.ParcoursDto;
import com.moscepa.dto.ParcoursRequestDto;
import com.moscepa.security.UserPrincipal;
import com.moscepa.service.ParcoursService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parcours" )
@CrossOrigin(origins = "http://localhost:4200" )
public class ParcoursController {

    private final ParcoursService parcoursService;

    public ParcoursController(ParcoursService parcoursService) {
        this.parcoursService = parcoursService;
    }

    @GetMapping("/etudiant/{etudiantId}")
    public ResponseEntity<ParcoursDto> getParcoursPourEtudiant(@PathVariable Long etudiantId) {
        ParcoursDto parcours = parcoursService.getParcoursPourEtudiant(etudiantId);
        return ResponseEntity.ok(parcours);
    }

    @PostMapping("/etudiant")
    public ResponseEntity<Void> enregistrerParcours(Authentication authentication, @RequestBody ParcoursRequestDto request) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long utilisateurId = userPrincipal.getId();
        List<Long> chapitreIds = request.getChapitresChoisisIds();
        parcoursService.enregistrerChoixEtudiant(utilisateurId, chapitreIds);
        return ResponseEntity.ok().build();
    }
}
