package com.moscepa.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moscepa.dto.ResponsableDto;
import com.moscepa.dto.UniteEnseignementDto;
import com.moscepa.entity.UniteEnseignement;
import com.moscepa.entity.Utilisateur;
import com.moscepa.repository.ElementConstitutifRepository; // <-- IMPORT AJOUTÉ
import com.moscepa.repository.UniteEnseignementRepository;
import com.moscepa.repository.UtilisateurRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UniteEnseignementService {

    @Autowired
    private UniteEnseignementRepository ueRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // =================================================================
    // === NOUVELLE INJECTION DE DÉPENDANCE                          ===
    // =================================================================
    @Autowired
    private ElementConstitutifRepository elementConstitutifRepository;


    @Transactional(readOnly = true)
    public List<UniteEnseignementDto> findAll() {
        return ueRepository.findAll().stream().map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<UniteEnseignementDto> findById(Long id) {
        return ueRepository.findById(id).map(this::convertToDto);
    }

    // =================================================================
    // === MÉTHODE 'save' AVEC VÉRIFICATION DE DOUBLON               ===
    // =================================================================
    @Transactional
    public UniteEnseignementDto save(UniteEnseignementDto ueDto) {
        // Vérification du doublon par code (insensible à la casse et sans espaces)
        ueRepository.findByCode(ueDto.code().trim()).ifPresent(existingUe -> {
            throw new IllegalStateException(
                "Une unité d'enseignement avec le code '" + ueDto.code() + "' existe déjà.");
        });

        UniteEnseignement ue = convertToEntity(ueDto);
        UniteEnseignement savedUe = ueRepository.save(ue);
        return convertToDto(savedUe);
    }

    // =================================================================
    // === MÉTHODE 'update' AVEC VÉRIFICATION DE DOUBLON AMÉLIORÉE    ===
    // =================================================================
    @Transactional
    public UniteEnseignementDto update(Long id, UniteEnseignementDto ueDto) {
        // Vérification du doublon qui n'est pas l'entité actuelle
        ueRepository.findByCode(ueDto.code().trim()).ifPresent(existingUe -> {
            if (!existingUe.getId().equals(id)) {
                throw new IllegalStateException(
                    "Une autre unité d'enseignement avec le code '" + ueDto.code() + "' existe déjà.");
            }
        });

        UniteEnseignement existingUe =
                ueRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                        "Unité d'enseignement non trouvée avec l'id: " + id));

        // Mise à jour manuelle des champs
        existingUe.setNom(ueDto.nom());
        existingUe.setCode(ueDto.code());
        existingUe.setDescription(ueDto.description());
        existingUe.setCredit(ueDto.credit());
        existingUe.setSemestre(ueDto.semestre());
        existingUe.setObjectifs(ueDto.objectifs());

        if (ueDto.responsable() != null && ueDto.responsable().id() != null) {
            Utilisateur responsable = utilisateurRepository.findById(ueDto.responsable().id())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Responsable non trouvé avec l'id: " + ueDto.responsable().id()));
            existingUe.setResponsable(responsable);
        } else {
            existingUe.setResponsable(null);
        }

        UniteEnseignement updatedUe = ueRepository.save(existingUe);
        return convertToDto(updatedUe);
    }

    // =================================================================
    // === MÉTHODE 'deleteById' AVEC VÉRIFICATION DES DÉPENDANCES    ===
    // =================================================================
    @Transactional
    public void deleteById(Long id) {
        if (!ueRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Impossible de supprimer, Unité d'enseignement non trouvée avec l'id: " + id);
        }

        // Vérification de l'existence d'éléments constitutifs liés
          if (elementConstitutifRepository.countByUniteEnseignementId(id) > 0) {
            throw new IllegalStateException(
                "Cette unité ne peut pas être supprimée car des éléments constitutifs y sont rattachés.");
        }

        ueRepository.deleteById(id);
    }

    // --- MÉTHODES DE CONVERSION MANUELLE (INCHANGÉES) ---

    private UniteEnseignementDto convertToDto(UniteEnseignement ue) {
        ResponsableDto responsableDto = null;
        if (ue.getResponsable() != null) {
            responsableDto = new ResponsableDto(ue.getResponsable().getId(),
                    ue.getResponsable().getNom(), ue.getResponsable().getPrenom());
        }

        return new UniteEnseignementDto(ue.getId(), ue.getNom(), ue.getCode(), ue.getDescription(),
                ue.getCredit(), ue.getSemestre(), ue.getObjectifs(), responsableDto);
    }

    private UniteEnseignement convertToEntity(UniteEnseignementDto ueDto) {
        UniteEnseignement ue = new UniteEnseignement();

        ue.setId(ueDto.id());
        ue.setNom(ueDto.nom());
        ue.setCode(ueDto.code());
        ue.setDescription(ueDto.description());
        ue.setCredit(ueDto.credit());
        ue.setSemestre(ueDto.semestre());
        ue.setObjectifs(ueDto.objectifs());

        if (ueDto.responsable() != null && ueDto.responsable().id() != null) {
            Utilisateur responsable = utilisateurRepository.findById(ueDto.responsable().id())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Impossible de créer/mettre à jour l'UE car le responsable avec l'id: "
                                    + ueDto.responsable().id() + " n'a pas été trouvé."));
            ue.setResponsable(responsable);
        }

        return ue;
    }
}
