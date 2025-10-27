package com.moscepa.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.moscepa.dto.FonctionnaliteDTO;
import com.moscepa.service.FonctionnaliteService;

@RestController
@RequestMapping("/api/fonctionnalites")
@CrossOrigin(origins = "http://localhost:4200")
public class FonctionnaliteController {

    @Autowired
    private FonctionnaliteService fonctionnaliteService;

    @GetMapping
    public ResponseEntity<List<FonctionnaliteDTO>> getAll() {
        return ResponseEntity.ok(fonctionnaliteService.getAllFonctionnalites());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FonctionnaliteDTO> create(@RequestBody FonctionnaliteDTO dto) {
        return new ResponseEntity<>(fonctionnaliteService.createFonctionnalite(dto),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FonctionnaliteDTO> update(@PathVariable Long id,
            @RequestBody FonctionnaliteDTO dto) {
        return ResponseEntity.ok(fonctionnaliteService.updateFonctionnalite(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fonctionnaliteService.deleteFonctionnalite(id);
        return ResponseEntity.noContent().build();
    }
}
