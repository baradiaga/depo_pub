// Fichier : src/main/java/com/moscepa/service/ElementConstitutifService.java (AVEC LOGS)

package com.moscepa.service;

// ... (tous vos imports restent les mêmes)
import com.moscepa.dto.ChapitreDto;
import com.moscepa.dto.ElementConstitutifRequestDto;
import com.moscepa.dto.ElementConstitutifResponseDto;
import com.moscepa.dto.EnseignantDto;
import com.moscepa.dto.SectionDto;
import com.moscepa.entity.ElementConstitutif;
import com.moscepa.entity.UniteEnseignement;
import com.moscepa.entity.Utilisateur;
import com.moscepa.repository.ElementConstitutifRepository;
import com.moscepa.repository.UniteEnseignementRepository;
import com.moscepa.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ElementConstitutifService {

    @Autowired
    private ElementConstitutifRepository elementRepository;

    @Autowired
    private UniteEnseignementRepository ueRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // ====================================================================
    // === MÉTHODE AVEC LOGS DE DÉBOGAGE                                ===
    // ====================================================================
    @Transactional(readOnly = true)
    public List<ElementConstitutifResponseDto> findMatieresByEtudiantId(Long etudiantId) {
        System.out.println("\n\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("!!! [SERVICE] ÉTAPE 1: Entrée dans findMatieresByEtudiantId pour l'étudiant ID: " + etudiantId);
        
        List<ElementConstitutif> matieres = elementRepository.findMatieresByEtudiantIdSqlNatif(etudiantId);
        
        System.out.println("!!! [SERVICE] ÉTAPE 2: La requête SQL native a retourné " + matieres.size() + " matière(s).");
        
        if (!matieres.isEmpty()) {
            System.out.println("!!! [SERVICE] Matières trouvées: " + matieres.stream().map(ElementConstitutif::getNom).collect(Collectors.joining(", ")));
        }

        List<ElementConstitutifResponseDto> dtos = matieres.stream()
                       .map(this::convertToResponseDto)
                       .collect(Collectors.toList());

        System.out.println("!!! [SERVICE] ÉTAPE 3: Conversion en DTO terminée. Renvoi de " + dtos.size() + " DTO(s).");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n");

        return dtos;
    }

    // --- TOUTES VOS AUTRES MÉTHODES RESTENT INCHANGÉES ---
    // (create, findByUeId, findAll, findByEnseignantId, etc.)
    @Transactional
    public ElementConstitutifResponseDto create(Long ueId, ElementConstitutifRequestDto dto) {
        UniteEnseignement ue = ueRepository.findById(ueId)
                .orElseThrow(() -> new EntityNotFoundException("Unité d'Enseignement non trouvée avec l'ID: " + ueId));
        Utilisateur enseignant = null;
        if (dto.getEnseignantId() != null) {
            enseignant = utilisateurRepository.findById(dto.getEnseignantId())
                    .orElseThrow(() -> new EntityNotFoundException("Enseignant non trouvé avec l'ID: " + dto.getEnseignantId()));
        }
        ElementConstitutif nouvelElement = new ElementConstitutif();
        nouvelElement.setNom(dto.getNom());
        nouvelElement.setCode(dto.getCode());
        nouvelElement.setDescription(dto.getDescription());
        nouvelElement.setCredit(dto.getCredit());
        nouvelElement.setUniteEnseignement(ue);
        nouvelElement.setEnseignant(enseignant);
        ElementConstitutif savedElement = elementRepository.save(nouvelElement);
        return convertToResponseDto(savedElement);
    }

    @Transactional(readOnly = true)
    public List<ElementConstitutifResponseDto> findByUeId(Long ueId) {
        return elementRepository.findByUniteEnseignementId(ueId).stream()
                       .map(this::convertToResponseDto)
                       .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ElementConstitutifResponseDto> findAll() {
        return elementRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ElementConstitutifResponseDto> findByEnseignantId(Long enseignantId) {
        return elementRepository.findByEnseignantId(enseignantId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ElementConstitutifResponseDto> findAllWithChapitres() {
        List<ElementConstitutif> elements = elementRepository.findAll();
        return elements.stream()
                .map(this::convertToResponseDtoWithChapitres)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> findAllNoms() {
        return elementRepository.findAll().stream()
                                .map(ElementConstitutif::getNom)
                                .collect(Collectors.toList());
    }

    @Transactional
    public ElementConstitutifResponseDto update(Long id, ElementConstitutifRequestDto dto) {
        ElementConstitutif ec = elementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Élément non trouvé avec l'ID: " + id));
        Utilisateur enseignant = null;
        if (dto.getEnseignantId() != null) {
            enseignant = utilisateurRepository.findById(dto.getEnseignantId())
                    .orElseThrow(() -> new EntityNotFoundException("Enseignant non trouvé avec l'ID: " + dto.getEnseignantId()));
        }
        ec.setNom(dto.getNom());
        ec.setCode(dto.getCode());
        ec.setCredit(dto.getCredit());
        ec.setDescription(dto.getDescription());
        ec.setEnseignant(enseignant);
        ElementConstitutif savedEc = elementRepository.save(ec);
        return convertToResponseDto(savedEc);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!elementRepository.existsById(id)) {
            throw new EntityNotFoundException("Impossible de supprimer : Élément non trouvé avec l'ID: " + id);
        }
        elementRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ChapitreDto> findChapitresByEcId(Long ecId) {
        ElementConstitutif ec = elementRepository.findById(ecId)
                .orElseThrow(() -> new EntityNotFoundException("Élément Constitutif non trouvé avec l'ID: " + ecId));
        return ec.getChapitres().stream()
                .map(chapitre -> {
                    ChapitreDto dto = new ChapitreDto();
                    dto.setId(chapitre.getId());
                    dto.setNom(chapitre.getNom());
                    dto.setNiveau(chapitre.getNiveau());
                    dto.setObjectif(chapitre.getObjectif());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private ElementConstitutifResponseDto convertToResponseDto(ElementConstitutif element) {
        ElementConstitutifResponseDto dto = new ElementConstitutifResponseDto();
        dto.setId(element.getId());
        dto.setNom(element.getNom());
        dto.setCode(element.getCode());
        dto.setDescription(element.getDescription());
        dto.setCredit(element.getCredit());
        if (element.getEnseignant() != null) {
            EnseignantDto enseignantDto = new EnseignantDto(
                element.getEnseignant().getId(),
                element.getEnseignant().getNom(),
                element.getEnseignant().getPrenom()
            );
            dto.setEnseignant(enseignantDto);
        }
        return dto;
    }

    private ElementConstitutifResponseDto convertToResponseDtoWithChapitres(ElementConstitutif element) {
        ElementConstitutifResponseDto dto = convertToResponseDto(element);
        if (element.getChapitres() != null) {
            List<ChapitreDto> chapitreDtos = element.getChapitres().stream()
                .map(chapitre -> {
                    ChapitreDto chapitreDto = new ChapitreDto();
                    chapitreDto.setId(chapitre.getId());
                    chapitreDto.setNom(chapitre.getNom());
                    chapitreDto.setObjectif(chapitre.getObjectif());
                    chapitreDto.setNiveau(chapitre.getNiveau());
                    if (chapitre.getSections() != null) {
                        List<SectionDto> sectionDtos = chapitre.getSections().stream()
                            .map(SectionDto::new)
                            .collect(Collectors.toList());
                        chapitreDto.setSections(sectionDtos);
                    } else {
                        chapitreDto.setSections(Collections.emptyList());
                    }
                    return chapitreDto;
                })
                .collect(Collectors.toList());
            dto.setChapitres(chapitreDtos);
        } else {
            dto.setChapitres(Collections.emptyList());
        }
        return dto;
    }
}
