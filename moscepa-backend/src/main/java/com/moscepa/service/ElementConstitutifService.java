// Fichier : src/main/java/com/moscepa/service/ElementConstitutifService.java (Version 2 - Corrigée)

package com.moscepa.service;

import com.moscepa.dto.ChapitreDto;
import com.moscepa.dto.ElementConstitutifRequestDto;
import com.moscepa.dto.ElementConstitutifResponseDto;
import com.moscepa.dto.EnseignantDto;
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

    // ... (Toutes les autres méthodes restent INCHANGÉES) ...
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
        return elementRepository.findAll().stream()
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
        } else {
            dto.setEnseignant(null);
        }
        return dto;
    }

    // ====================================================================
    // === CORRECTION APPLIQUÉE ICI (Version 2)                        ===
    // ====================================================================
    private ElementConstitutifResponseDto convertToResponseDtoWithChapitres(ElementConstitutif element) {
        // 1. On appelle la méthode de base pour remplir les informations de l'EC.
        ElementConstitutifResponseDto dto = convertToResponseDto(element);

        // 2. On récupère la liste des chapitres de l'entité et on la convertit.
        List<ChapitreDto> chapitreDtos = element.getChapitres().stream()
                .map(chapitre -> {
                    // On utilise les setters car il n'y a pas de constructeur complet.
                    ChapitreDto chapitreDto = new ChapitreDto();
                    chapitreDto.setId(chapitre.getId());
                    chapitreDto.setNom(chapitre.getNom());
                    chapitreDto.setObjectif(chapitre.getObjectif());
                    chapitreDto.setNiveau(chapitre.getNiveau());
                    // On ne remplit pas les autres champs (description, tests, etc.)
                    // car ils ne sont pas nécessaires pour cette vue.
                    return chapitreDto;
                })
                .collect(Collectors.toList());

        // 3. On ajoute la liste des DTOs de chapitres au DTO de réponse.
        dto.setChapitres(chapitreDtos);

        return dto;
    }
    @Transactional(readOnly = true)
    public List<ChapitreDto> findChapitresByEcId(Long ecId) {
        // On cherche l'EC par son ID. Si non trouvé, on lève une exception.
        ElementConstitutif ec = elementRepository.findById(ecId)
                .orElseThrow(() -> new EntityNotFoundException("Élément Constitutif non trouvé avec l'ID: " + ecId));

        // On récupère la liste des chapitres de l'entité, on la transforme en stream,
        // on mappe chaque entité Chapitre en ChapitreDto, et on collecte le tout dans une liste.
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
}
