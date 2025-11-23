// Fichier : src/main/java/com/moscepa/service/SectionService.java (Mis à Jour)

package com.moscepa.service;

import com.moscepa.dto.SectionCreateDto; // <-- Nouvel import
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
        return new SectionDto(section);
    }

    // ====================================================================
    // === MÉTHODE DE CRÉATION MISE À JOUR                            ===
    // ====================================================================
    /**
     * Crée une nouvelle section dans un chapitre donné.
     * La signature a été modifiée pour utiliser SectionCreateDto, qui est plus spécifique.
     * @param chapitreId L'ID du chapitre parent.
     * @param dto Les données de la nouvelle section (titre, type, etc.).
     * @return Le DTO de la section qui vient d'être créée.
     */
    @Transactional
    public SectionDto createSection(Long chapitreId, SectionCreateDto dto) {
        Chapitre chapitre = chapitreRepository.findById(chapitreId)
                .orElseThrow(() -> new EntityNotFoundException("Chapitre non trouvé avec l'ID : " + chapitreId));

        // On calcule le prochain numéro d'ordre pour la nouvelle section
        // Note : .getSections() peut déclencher une requête si le fetch type est LAZY.
        // C'est acceptable ici car on a besoin de la taille.
        int nouvelOrdre = chapitre.getSections().size() + 1;

        Section nouvelleSection = new Section();
        nouvelleSection.setTitre(dto.getTitre());
        nouvelleSection.setContenu(dto.getContenu() != null ? dto.getContenu() : ""); // Contenu vide par défaut
        nouvelleSection.setTypeSection(dto.getTypeSection());
        nouvelleSection.setOrdre(nouvelOrdre);
        nouvelleSection.setChapitre(chapitre);

        Section sectionSauvegardee = sectionRepository.save(nouvelleSection);

        return convertToDto(sectionSauvegardee);
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

    // --- MISE À JOUR ---
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
