package com.moscepa.service;

import com.moscepa.dto.FormationCreationDto;
import com.moscepa.dto.FormationDetailDto;
import com.moscepa.entity.Chapitre;
import com.moscepa.entity.ElementConstitutif;
import com.moscepa.entity.Formation;
import com.moscepa.entity.Utilisateur;
import com.moscepa.repository.ElementConstitutifRepository;
import com.moscepa.repository.FormationRepository;
import com.moscepa.repository.UtilisateurRepository;
import com.moscepa.security.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FormationService {

    private final FormationRepository formationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ElementConstitutifRepository elementConstitutifRepository;

    public FormationService(FormationRepository formationRepository,
                            UtilisateurRepository utilisateurRepository,
                            ElementConstitutifRepository elementConstitutifRepository) {
        this.formationRepository = formationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.elementConstitutifRepository = elementConstitutifRepository;
    }

    // ====================================================================
    // === CRÉATION
    // ====================================================================
    @Transactional
    public FormationDetailDto creerFormation(FormationCreationDto dto) {
        // Vérification unicité code et nom
        formationRepository.findByCode(dto.getCode())
                .ifPresent(f -> { throw new IllegalArgumentException("Code déjà utilisé."); });

        formationRepository.findByNom(dto.getNom())
                .ifPresent(f -> { throw new IllegalArgumentException("Nom déjà utilisé."); });

        // Créateur connecté
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Utilisateur createur = utilisateurRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new EntityNotFoundException("Créateur non trouvé."));

        Formation formation = new Formation();
        formation.setNom(dto.getNom());
        formation.setCode(dto.getCode());
        formation.setDescription(dto.getDescription());
        formation.setStatut(dto.getStatut());
        formation.setNiveauEtude(dto.getNiveauEtude());
        formation.setDuree(dto.getDuree());
        formation.setCreateur(createur);

        // Responsable pédagogique
        if (dto.getResponsableId() != null) {
            Utilisateur responsable = utilisateurRepository.findById(dto.getResponsableId())
                    .orElseThrow(() -> new EntityNotFoundException("Responsable pédagogique non trouvé."));
            formation.setResponsablePedagogique(responsable);
        }

        Formation savedFormation = formationRepository.save(formation);

        // Lier les éléments constitutifs correctement (orphanRemoval)
        if (dto.getElementsConstitutifsIds() != null && !dto.getElementsConstitutifsIds().isEmpty()) {
            List<ElementConstitutif> elements = elementConstitutifRepository.findAllById(dto.getElementsConstitutifsIds());

            // Dissocier les anciens (si existants)
            savedFormation.getElementsConstitutifs().forEach(e -> e.setFormation(null));
            savedFormation.getElementsConstitutifs().clear();

            // Ajouter les nouveaux via helper
            elements.forEach(savedFormation::addElementConstitutif);
            elementConstitutifRepository.saveAll(elements);
        }

        return new FormationDetailDto(savedFormation);
    }

    // ====================================================================
    // === LECTURE
    // ====================================================================
    public List<FormationDetailDto> getAllFormations() {
        return formationRepository.findAll().stream()
                .map(FormationDetailDto::new)
                .collect(Collectors.toList());
    }

    public FormationDetailDto getFormationById(Long id) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Formation non trouvée avec l'ID: " + id));
        return new FormationDetailDto(formation);
    }

    // ====================================================================
    // === MODIFICATION
    // ====================================================================
    

       @Transactional
public FormationDetailDto modifierFormation(Long id, FormationCreationDto dto) {

    Formation formation = formationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Formation non trouvée."));

    // Vérification code / nom
    if (!formation.getCode().equals(dto.getCode())) {
        formationRepository.findByCode(dto.getCode())
                .ifPresent(f -> { throw new IllegalArgumentException("Code déjà utilisé."); });
    }

    if (!formation.getNom().equals(dto.getNom())) {
        formationRepository.findByNom(dto.getNom())
                .ifPresent(f -> { throw new IllegalArgumentException("Nom déjà utilisé."); });
    }

    // Update basiques
    formation.setNom(dto.getNom());
    formation.setCode(dto.getCode());
    formation.setDescription(dto.getDescription());
    formation.setStatut(dto.getStatut());
    formation.setNiveauEtude(dto.getNiveauEtude());
    formation.setDuree(dto.getDuree());

    // Responsable
    if (dto.getResponsableId() != null) {
        Utilisateur responsable = utilisateurRepository.findById(dto.getResponsableId())
                .orElseThrow(() -> new EntityNotFoundException("Responsable pédagogique non trouvé."));
        formation.setResponsablePedagogique(responsable);
    } else {
        formation.setResponsablePedagogique(null);
    }

    // ============================================================
    //   🔥 Partie importante : mettre à jour les EC SANS supprimer
    // ============================================================
    if (dto.getElementsConstitutifsIds() != null) {
        // Charger les nouveaux EC
        List<ElementConstitutif> nouveauxEC =
                elementConstitutifRepository.findAllById(dto.getElementsConstitutifsIds());

        // Nettoyer la relation précédente sans supprimer les EC
        formation.getElementsConstitutifs().clear();

        // Associer les nouveaux EC à la formation
        nouveauxEC.forEach(ec -> ec.setFormation(formation));

        formation.getElementsConstitutifs().addAll(nouveauxEC);
    }

    Formation updated = formationRepository.save(formation);
    return new FormationDetailDto(updated);
}


    // ====================================================================
    // === SUPPRESSION
    // ====================================================================
@Transactional
public void supprimerFormation(Long id) {
    Formation formation = formationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Formation non trouvée."));

    // Détacher les éléments constitutifs de la formation
    if (formation.getElementsConstitutifs() != null) {
        for (ElementConstitutif ec : formation.getElementsConstitutifs()) {
            ec.setFormation(null);  // détache l'EC de la formation
            // NE PAS supprimer les chapitres pour éviter les contraintes FK
        }
        formation.getElementsConstitutifs().clear();
    }

    // Supprimer la formation bon
    formationRepository.delete(formation);
}


}
