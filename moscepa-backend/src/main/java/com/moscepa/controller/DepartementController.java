package com.moscepa.controller;

import com.moscepa.dto.DepartementDTO;
import com.moscepa.entity.Departement;
import com.moscepa.service.DepartementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/departements")
@CrossOrigin(origins = "*")
public class DepartementController {
    
    @Autowired
    private DepartementService departementService;
    
    // ========== CRUD SIMPLE ==========
    
    @GetMapping
    public ResponseEntity<List<DepartementDTO>> getAllDepartements() {
        try {
            List<Departement> departements = departementService.getAllDepartements();
            List<DepartementDTO> dtos = departements.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DepartementDTO> getDepartementById(@PathVariable Long id) {
        try {
            Departement departement = departementService.getDepartementById(id);
            DepartementDTO dto = convertToDTO(departement);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<DepartementDTO> createDepartement(@Valid @RequestBody DepartementDTO departementDTO) {
        try {
            Departement departement = departementService.createDepartement(departementDTO);
            DepartementDTO responseDTO = convertToDTO(departement);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DepartementDTO> updateDepartement(@PathVariable Long id, @Valid @RequestBody DepartementDTO departementDTO) {
        try {
            Departement departement = departementService.updateDepartement(id, departementDTO);
            DepartementDTO responseDTO = convertToDTO(departement);
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartement(@PathVariable Long id) {
        try {
            departementService.deleteDepartement(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    // ========== RECHERCHE PAGINÉE ==========
    
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long uefrId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        
        try {
            // Gestion du tri
            Pageable pageable;
            if (sort != null && sort.contains(",")) {
                String[] sortParams = sort.split(",");
                String sortField = sortParams[0];
                Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? 
                    Sort.Direction.DESC : Sort.Direction.ASC;
                pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
            } else {
                pageable = PageRequest.of(page, size);
            }
            
            Page<Departement> departementsPage = departementService.search(search, uefrId, pageable);
            Page<DepartementDTO> dtosPage = departementsPage.map(this::convertToDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", dtosPage.getContent());
            response.put("totalElements", departementsPage.getTotalElements());
            response.put("totalPages", departementsPage.getTotalPages());
            response.put("size", departementsPage.getSize());
            response.put("number", departementsPage.getNumber());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la recherche: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    // ========== MÉTHODES UTILITAIRES ==========
    
    @GetMapping("/exists/sigle/{sigle}")
    public ResponseEntity<Map<String, Boolean>> checkSigleExists(@PathVariable String sigle) {
        try {
            boolean exists = departementService.existsBySigle(sigle);
            Map<String, Boolean> response = new HashMap<>();
            response.put("exists", exists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/exists/sigle/{sigle}/not/{id}")
    public ResponseEntity<Map<String, Boolean>> checkSigleExistsForOther(
            @PathVariable String sigle, 
            @PathVariable Long id) {
        try {
            boolean exists = departementService.existsBySigleAndIdNot(sigle, id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("exists", exists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ========== MÉTHODE DE CONVERSION ==========
    
    private DepartementDTO convertToDTO(Departement departement) {
        DepartementDTO dto = new DepartementDTO();
        dto.setId(departement.getId());
        dto.setNom(departement.getNom());
        dto.setSigle(departement.getSigle());
        dto.setAdresse(departement.getAdresse());
        dto.setContact(departement.getContact());
        dto.setLogo(departement.getLogo());
        dto.setLien(departement.getLien());
        dto.setUefrId(departement.getUefr().getId());
        
        // Ajout de formationId
        if (departement.getFormation() != null) {
            dto.setFormationId(departement.getFormation().getId());
        }
        
        dto.setCreatedAt(departement.getCreatedAt());
        dto.setUpdatedAt(departement.getUpdatedAt());
        
        return dto;
    }
}