package com.moscepa.controller;

import com.moscepa.dto.CategorieDto;
import com.moscepa.service.CategorieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategorieController {

    private final CategorieService categorieService;

    public CategorieController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    @GetMapping
    public ResponseEntity<List<CategorieDto>> getAll() {
        return ResponseEntity.ok(categorieService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategorieDto> getById(@PathVariable Long id) {
        CategorieDto dto = categorieService.findById(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<CategorieDto> create(@RequestBody CategorieDto dto) {
        return ResponseEntity.ok(categorieService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategorieDto> update(@PathVariable Long id, @RequestBody CategorieDto dto) {
        return ResponseEntity.ok(categorieService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categorieService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
