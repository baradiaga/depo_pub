// Fichier : src/main/java/com/moscepa/service/SectionService.java

package com.moscepa.service;

import com.moscepa.dto.SectionDto;
import com.moscepa.dto.SectionUpdateRequest;
import com.moscepa.entity.Chapitre;
import com.moscepa.entity.Section;
import com.moscepa.repository.ChapitreRepository;
import com.moscepa.repository.SectionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SectionService {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private ChapitreRepository chapitreRepository;

    // --- MÉTHODE DE CONVERSION (interne) ---
    private SectionDto convertToDto(Section section) {
        return new SectionDto(
                section.getId(),
                section.getTitre(),
                section.getContenu(),
                section.getOrdre(),
                section.getChapitre() != null ? section.getChapitre().getId() : null
        );
    }

    // --- CRÉATION ---
    @Transactional
    public SectionDto createSection(Long chapitreId, SectionDto sectionDto) {
        Chapitre chapitre = chapitreRepository.findById(chapitreId)
                .orElseThrow(() -> new EntityNotFoundException("Chapitre non trouvé avec l'ID : " + chapitreId));

        Section section = new Section();
        section.setTitre(sectionDto.titre());
        section.setContenu(sectionDto.contenu());
        section.setOrdre(sectionDto.ordre());
        section.setChapitre(chapitre);

        Section savedSection = sectionRepository.save(section);
        return convertToDto(savedSection);
    }

    // --- LECTURE ---
    @Transactional(readOnly = true)
    public List<SectionDto> getAllSections() {
        return sectionRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<SectionDto> getSectionById(Long id) {
        return sectionRepository.findById(id).map(this::convertToDto);
    }

    // ====================================================================
    // === MÉTHODE DE MISE À JOUR DU CONTENU (la plus importante ici)   ===
    // ====================================================================
    @Transactional
    public SectionDto updateSectionContent(Long id, SectionUpdateRequest updateRequest) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Section non trouvée avec l'ID : " + id));

        section.setTitre(updateRequest.titre());
        section.setContenu(updateRequest.contenu());

        Section updatedSection = sectionRepository.save(section);
        return convertToDto(updatedSection);
    }

    // --- SUPPRESSION ---
    @Transactional
    public void deleteSection(Long id) {
        if (!sectionRepository.existsById(id)) {
            throw new EntityNotFoundException("Section non trouvée avec l'ID : " + id);
        }
        sectionRepository.deleteById(id);
    }
}
