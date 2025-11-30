// Fichier : src/main/java/com/moscepa/service/EchelleConnaissanceService.java

package com.moscepa.service;

import com.moscepa.dto.EchelleConnaissanceDto;
import com.moscepa.entity.EchelleConnaissance;
import com.moscepa.repository.EchelleConnaissanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class EchelleConnaissanceService {

    private final EchelleConnaissanceRepository repository;

    public EchelleConnaissanceService(EchelleConnaissanceRepository repository) {
        this.repository = repository;
    }

    // READ ALL
    public List<EchelleConnaissanceDto> findAll() {
        return repository.findAll().stream()
                .map(EchelleConnaissanceDto::new)
                .collect(Collectors.toList());
    }

    // CREATE
    @Transactional
    public EchelleConnaissanceDto create(EchelleConnaissanceDto dto) {
        EchelleConnaissance echelle = new EchelleConnaissance();
        echelle.setIntervalle(dto.getIntervalle());
        echelle.setDescription(dto.getDescription());
        echelle.setRecommandation(dto.getRecommandation());

        EchelleConnaissance savedEchelle = repository.save(echelle);
        return new EchelleConnaissanceDto(savedEchelle);
    }

    // UPDATE
    @Transactional
    public EchelleConnaissanceDto update(Long id, EchelleConnaissanceDto dto) {
        EchelleConnaissance echelle = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("EchelleConnaissance non trouvée avec l'ID: " + id));

        echelle.setIntervalle(dto.getIntervalle());
        echelle.setDescription(dto.getDescription());
        echelle.setRecommandation(dto.getRecommandation());

        EchelleConnaissance updatedEchelle = repository.save(echelle);
        return new EchelleConnaissanceDto(updatedEchelle);
    }

    // DELETE
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("EchelleConnaissance non trouvée avec l'ID: " + id);
        }
        repository.deleteById(id);
    }
}
