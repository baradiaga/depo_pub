package com.moscepa.service;

import com.moscepa.dto.CategorieDto;
import com.moscepa.entity.Categorie;
import com.moscepa.entity.EchelleConnaissance;
import com.moscepa.repository.CategorieRepository;
import com.moscepa.repository.EchelleConnaissanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategorieService {

    private final CategorieRepository categorieRepository;
    private final EchelleConnaissanceRepository echelleRepository;

    public CategorieService(CategorieRepository categorieRepository, EchelleConnaissanceRepository echelleRepository) {
        this.categorieRepository = categorieRepository;
        this.echelleRepository = echelleRepository;
    }

    public List<CategorieDto> findAll() {
        return categorieRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public CategorieDto findById(Long id) {
        return categorieRepository.findById(id).map(this::toDto).orElse(null);
    }

    public CategorieDto create(CategorieDto dto) {
        EchelleConnaissance echelle = echelleRepository.findById(dto.getEchelleId())
                .orElseThrow(() -> new RuntimeException("Échelle non trouvée"));

        Categorie categorie = new Categorie();
        categorie.setNom(dto.getNom());
        categorie.setEchelleConnaissance(echelle);

        Categorie saved = categorieRepository.save(categorie);
        return toDto(saved);
    }

    public CategorieDto update(Long id, CategorieDto dto) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));

        EchelleConnaissance echelle = echelleRepository.findById(dto.getEchelleId())
                .orElseThrow(() -> new RuntimeException("Échelle non trouvée"));

        categorie.setNom(dto.getNom());
        categorie.setEchelleConnaissance(echelle);

        Categorie updated = categorieRepository.save(categorie);
        return toDto(updated);
    }

    public void delete(Long id) {
        categorieRepository.deleteById(id);
    }

    private CategorieDto toDto(Categorie categorie) {
        CategorieDto dto = new CategorieDto();
        dto.setId(categorie.getId());
        dto.setNom(categorie.getNom());
        dto.setEchelleId(categorie.getEchelleConnaissance().getId());
        dto.setEchelleIntervalle(categorie.getEchelleConnaissance().getIntervalle());
        return dto;
    }
}
