package com.moscepa.service;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.moscepa.dto.FonctionnaliteDTO;
import com.moscepa.entity.Fonctionnalite;
import com.moscepa.entity.SousFonctionnalite;
import com.moscepa.repository.FonctionnaliteRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class FonctionnaliteService {

    @Autowired
    private FonctionnaliteRepository fonctionnaliteRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<FonctionnaliteDTO> getAllFonctionnalites() {
        return fonctionnaliteRepository.findAll().stream()
                .map(entity -> modelMapper.map(entity, FonctionnaliteDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public FonctionnaliteDTO createFonctionnalite(FonctionnaliteDTO dto) {
        Fonctionnalite entity = modelMapper.map(dto, Fonctionnalite.class);
        Fonctionnalite savedEntity = fonctionnaliteRepository.save(entity);
        return modelMapper.map(savedEntity, FonctionnaliteDTO.class);
    }

    @Transactional
    public FonctionnaliteDTO updateFonctionnalite(Long id, FonctionnaliteDTO dto) {
        Fonctionnalite existingEntity = fonctionnaliteRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Fonctionnalité non trouvée avec l'id: " + id));

        // Mettre à jour les champs simples
        existingEntity.setNom(dto.getNom());
        existingEntity.setFeatureKey(dto.getFeatureKey());
        existingEntity.setIcon(dto.getIcon());

        // --- CORRECTION DE LA LOGIQUE DE MISE À JOUR ---
        // 1. Vider la liste existante des sous-fonctionnalités
        existingEntity.getSousFonctionnalites().clear();

        // 2. Mapper les DTOs des sous-fonctionnalités en entités
        if (dto.getSousFonctionnalites() != null) {
            List<SousFonctionnalite> updatedSubFeatures = dto.getSousFonctionnalites().stream()
                    .map(subDto -> modelMapper.map(subDto, SousFonctionnalite.class))
                    .collect(Collectors.toList());
            // 3. Ajouter les nouvelles sous-fonctionnalités à la liste
            existingEntity.getSousFonctionnalites().addAll(updatedSubFeatures);
        }

        Fonctionnalite savedEntity = fonctionnaliteRepository.save(existingEntity);
        return modelMapper.map(savedEntity, FonctionnaliteDTO.class);
    }

    @Transactional
    public void deleteFonctionnalite(Long id) {
        if (!fonctionnaliteRepository.existsById(id)) {
            throw new EntityNotFoundException("Fonctionnalité non trouvée avec l'id: " + id);
        }
        fonctionnaliteRepository.deleteById(id);
    }
}
