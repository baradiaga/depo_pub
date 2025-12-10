package com.moscepa.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moscepa.dto.UniteEnseignementDto;
import com.moscepa.entity.UniteEnseignement;
import com.moscepa.entity.Utilisateur;
import com.moscepa.entity.Formation;
import com.moscepa.entity.ElementConstitutif;
import com.moscepa.repository.ElementConstitutifRepository;
import com.moscepa.repository.UniteEnseignementRepository;
import com.moscepa.repository.UtilisateurRepository;
import com.moscepa.repository.FormationRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UniteEnseignementService {

    @Autowired
    private UniteEnseignementRepository ueRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ElementConstitutifRepository elementConstitutifRepository;

    @Autowired
    private FormationRepository formationRepository;

    // ============================
    // FIND ALL
    // ============================
    @Transactional(readOnly = true)
    public List<UniteEnseignementDto> findAll() {
        return ueRepository.findAll().stream()
                .map(UniteEnseignementDto::new)
                .collect(Collectors.toList());
    }

    // ============================
    // FIND BY ID
    // ============================
    @Transactional(readOnly = true)
    public Optional<UniteEnseignementDto> findById(Long id) {
        return ueRepository.findById(id).map(UniteEnseignementDto::new);
    }

    // ============================
    // SAVE
    // ============================
    @Transactional
    public UniteEnseignementDto save(UniteEnseignementDto ueDto) {

        ueRepository.findByCode(ueDto.getCode().trim()).ifPresent(existingUe -> {
            throw new IllegalStateException(
                "Une unité d'enseignement avec le code '" + ueDto.getCode() + "' existe déjà.");
        });

        UniteEnseignement ue = convertToEntity(ueDto);
        UniteEnseignement savedUe = ueRepository.save(ue);
        return new UniteEnseignementDto(savedUe);
    }

    // ============================
    // UPDATE
    // ============================
    @Transactional
    public UniteEnseignementDto update(Long id, UniteEnseignementDto ueDto) {

        ueRepository.findByCode(ueDto.getCode().trim()).ifPresent(existingUe -> {
            if (!existingUe.getId().equals(id)) {
                throw new IllegalStateException(
                    "Une autre unité d'enseignement avec le code '" + ueDto.getCode() + "' existe déjà.");
            }
        });

        UniteEnseignement existingUe = ueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Unité d'enseignement non trouvée avec l'id: " + id));

        existingUe.setNom(ueDto.getNom());
        existingUe.setCode(ueDto.getCode());
        existingUe.setDescription(ueDto.getDescription());
        existingUe.setEcts(ueDto.getEcts());
        existingUe.setSemestre(ueDto.getSemestre());
        existingUe.setObjectifs(ueDto.getObjectifs());

        // ⚠️ Formation obligatoire
        if (ueDto.getFormationId() != null) {
            Formation formation = formationRepository.findById(ueDto.getFormationId())
                .orElseThrow(() -> new EntityNotFoundException(
                    "Formation non trouvée avec l'id: " + ueDto.getFormationId()));
            existingUe.setFormation(formation);
        } else {
            throw new IllegalStateException("Formation obligatoire pour l'unité d'enseignement");
        }

        // ⚠️ Responsable optionnel
        if (ueDto.getResponsableId() != null) {
            Utilisateur responsable = utilisateurRepository.findById(ueDto.getResponsableId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Responsable non trouvé avec l'id: " + ueDto.getResponsableId()));
            existingUe.setResponsable(responsable);
        } else {
            existingUe.setResponsable(null);
        }

        // ⚠️ Éléments constitutifs
        if (ueDto.getElementConstitutifIds() != null) {
            List<ElementConstitutif> ecs = elementConstitutifRepository.findAllById(ueDto.getElementConstitutifIds());
            existingUe.setElementsConstitutifs(ecs);
        }

        UniteEnseignement updatedUe = ueRepository.save(existingUe);
        return new UniteEnseignementDto(updatedUe);
    }

    // ============================
    // DELETE
    // ============================
    @Transactional
    public void deleteById(Long id) {

        if (!ueRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Impossible de supprimer, Unité d'enseignement non trouvée avec l'id: " + id);
        }

        if (elementConstitutifRepository.countByUniteEnseignementId(id) > 0) {
            throw new IllegalStateException(
                "Cette unité ne peut pas être supprimée car des éléments constitutifs y sont rattachés.");
        }

        ueRepository.deleteById(id);
    }

    // ============================
    // CONVERSION DTO → ENTITY
    // ============================
    private UniteEnseignement convertToEntity(UniteEnseignementDto ueDto) {
        UniteEnseignement ue = new UniteEnseignement();

        ue.setId(ueDto.getId());
        ue.setNom(ueDto.getNom());
        ue.setCode(ueDto.getCode());
        ue.setDescription(ueDto.getDescription());
        ue.setEcts(ueDto.getEcts());
        ue.setSemestre(ueDto.getSemestre());
        ue.setObjectifs(ueDto.getObjectifs());
        ue.setVolumeHoraireCours(ueDto.getVolumeHoraireCours());
        ue.setVolumeHoraireTD(ueDto.getVolumeHoraireTD());
        ue.setVolumeHoraireTP(ueDto.getVolumeHoraireTP());

        // ⚠️ Formation obligatoire
        if (ueDto.getFormationId() != null) {
            Formation formation = formationRepository.findById(ueDto.getFormationId())
                .orElseThrow(() -> new EntityNotFoundException(
                    "Formation non trouvée avec l'id: " + ueDto.getFormationId()));
            ue.setFormation(formation);
        } else {
            throw new IllegalStateException("Formation obligatoire pour l'unité d'enseignement");
        }

        // ⚠️ Responsable optionnel
        if (ueDto.getResponsableId() != null) {
            Utilisateur responsable = utilisateurRepository.findById(ueDto.getResponsableId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Responsable non trouvé avec l'id: " + ueDto.getResponsableId()));
            ue.setResponsable(responsable);
        }

        // ⚠️ Éléments constitutifs
        if (ueDto.getElementConstitutifIds() != null) {
            List<ElementConstitutif> ecs = elementConstitutifRepository.findAllById(ueDto.getElementConstitutifIds());
            ue.setElementsConstitutifs(ecs);
        }

        return ue;
    }
}
