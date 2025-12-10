package com.moscepa.service;

import com.moscepa.dto.FormationCreationDto;
import com.moscepa.dto.FormationDetailDto;
import com.moscepa.dto.CompetenceDetailDto;
import com.moscepa.dto.UniteEnseignementDto;
import com.moscepa.entity.UniteEnseignement;
import com.moscepa.entity.CompetenceDetail;
import com.moscepa.entity.NiveauAcquisition;
import com.moscepa.entity.Utilisateur;
import com.moscepa.entity.ElementConstitutif;
import com.moscepa.entity.Formation;
import com.moscepa.repository.ElementConstitutifRepository;
import com.moscepa.repository.FormationRepository;
import com.moscepa.repository.UtilisateurRepository;
import com.moscepa.security.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

        // --- Nouveaux champs ---
        formation.setObjectifs(dto.getObjectifs());
        formation.setPrerequis(dto.getPrerequis());
        formation.setDebouches(dto.getDebouches());
        formation.setEvaluationModalites(dto.getEvaluationModalites());
        formation.setModaliteEnseignement(dto.getModaliteEnseignement());
        formation.setLieu(dto.getLieu());
        formation.setDateDebut(dto.getDateDebut() != null ? dto.getDateDebut().atStartOfDay() : null);
        formation.setDateFin(dto.getDateFin() != null ? dto.getDateFin().atStartOfDay() : null);
        formation.setCapacite(dto.getCapacite());
        formation.setTarif(dto.getTarif());
        formation.setCertificationProfessionnelle(dto.getCertificationProfessionnelle());

        // Sauvegarde initiale pour obtenir l'ID (nécessaire pour les relations OneToMany)
        Formation savedFormation = formationRepository.save(formation);

        // 1. Gestion des Compétences
        if (dto.getCompetences() != null) {
            dto.getCompetences().forEach(compDto -> {
                CompetenceDetail competence = new CompetenceDetail();
                competence.setLibelle(compDto.getLibelle());
                competence.setNiveauAcquisition(NiveauAcquisition.valueOf(compDto.getNiveauAcquisition()));
                competence.setIndicateursEvaluation(compDto.getIndicateursEvaluation());
                competence.setFormation(savedFormation);
                savedFormation.getCompetences().add(competence);
            });
        }

        // 2. Gestion des Unités d'Enseignement (UE)
        if (dto.getUnitesEnseignement() != null) {
            dto.getUnitesEnseignement().forEach(ueDto -> {
                UniteEnseignement ue = new UniteEnseignement();
                ue.setNom(ueDto.getNom());
                ue.setCode(ueDto.getCode());
                ue.setDescription(ueDto.getDescription());
                ue.setEcts(ueDto.getEcts());
                ue.setSemestre(ueDto.getSemestre());
                ue.setObjectifs(ueDto.getObjectifs());
                ue.setVolumeHoraireCours(ueDto.getVolumeHoraireCours());
                ue.setVolumeHoraireTD(ueDto.getVolumeHoraireTD());
                ue.setVolumeHoraireTP(ueDto.getVolumeHoraireTP());
                ue.setFormation(savedFormation);

                // Lier le responsable de l'UE
                if (ueDto.getResponsableId() != null) {
                    Utilisateur respUe = utilisateurRepository.findById(ueDto.getResponsableId())
                            .orElseThrow(() -> new EntityNotFoundException("Responsable UE non trouvé."));
                    ue.setResponsable(respUe);
                }

                // Lier les ElementConstitutif à l'UE
                if (ueDto.getElementConstitutifIds() != null && !ueDto.getElementConstitutifIds().isEmpty()) {
                    List<ElementConstitutif> elements = elementConstitutifRepository.findAllById(ueDto.getElementConstitutifIds());
                    ue.setElementsConstitutifs(elements);
                }

                savedFormation.getUnitesEnseignement().add(ue);
            });
        }

        // L'ancienne logique de liaison des ElementConstitutif à la Formation est retirée.
        // Les EC sont maintenant liés aux UEs.

        return new FormationDetailDto(formationRepository.save(savedFormation));
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

        // --- Nouveaux champs ---
        formation.setObjectifs(dto.getObjectifs());
        formation.setPrerequis(dto.getPrerequis());
        formation.setDebouches(dto.getDebouches());
        formation.setEvaluationModalites(dto.getEvaluationModalites());
        formation.setModaliteEnseignement(dto.getModaliteEnseignement());
        formation.setLieu(dto.getLieu());
        formation.setDateDebut(dto.getDateDebut() != null ? dto.getDateDebut().atStartOfDay() : null);
        formation.setDateFin(dto.getDateFin() != null ? dto.getDateFin().atStartOfDay() : null);
        formation.setCapacite(dto.getCapacite());
        formation.setTarif(dto.getTarif());
        formation.setCertificationProfessionnelle(dto.getCertificationProfessionnelle());

        // 1. Gestion des Compétences (orphanRemoval=true gère la suppression des anciennes)
        formation.getCompetences().clear();
        if (dto.getCompetences() != null) {
            dto.getCompetences().forEach(compDto -> {
                CompetenceDetail competence = new CompetenceDetail();
                competence.setLibelle(compDto.getLibelle());
                competence.setNiveauAcquisition(NiveauAcquisition.valueOf(compDto.getNiveauAcquisition()));
                competence.setIndicateursEvaluation(compDto.getIndicateursEvaluation());
                competence.setFormation(formation);
                formation.getCompetences().add(competence);
            });
        }

        // 2. Gestion des Unités d'Enseignement (orphanRemoval=true gère la suppression des anciennes)
        formation.getUnitesEnseignement().clear();
        if (dto.getUnitesEnseignement() != null) {
            dto.getUnitesEnseignement().forEach(ueDto -> {
                UniteEnseignement ue = new UniteEnseignement();
                ue.setNom(ueDto.getNom());
                ue.setCode(ueDto.getCode());
                ue.setDescription(ueDto.getDescription());
                ue.setEcts(ueDto.getEcts());
                ue.setSemestre(ueDto.getSemestre());
                ue.setObjectifs(ueDto.getObjectifs());
                ue.setVolumeHoraireCours(ueDto.getVolumeHoraireCours());
                ue.setVolumeHoraireTD(ueDto.getVolumeHoraireTD());
                ue.setVolumeHoraireTP(ueDto.getVolumeHoraireTP());
                ue.setFormation(formation);

                // Lier le responsable de l'UE
                if (ueDto.getResponsableId() != null) {
                    Utilisateur respUe = utilisateurRepository.findById(ueDto.getResponsableId())
                            .orElseThrow(() -> new EntityNotFoundException("Responsable UE non trouvé."));
                    ue.setResponsable(respUe);
                }

                // Lier les ElementConstitutif à l'UE
                if (ueDto.getElementConstitutifIds() != null && !ueDto.getElementConstitutifIds().isEmpty()) {
                    List<ElementConstitutif> elements = elementConstitutifRepository.findAllById(ueDto.getElementConstitutifIds());
                    ue.setElementsConstitutifs(elements);
                }

                formation.getUnitesEnseignement().add(ue);
            });
        }

	    // ============================================================
	    //   🔥 Ancienne logique EC : RETRAIT
	    // ============================================================
	    // if (dto.getElementsConstitutifsIds() != null) {
	    //     // Charger les nouveaux EC
	    //     List<ElementConstitutif> nouveauxEC =
	    //             elementConstitutifRepository.findAllById(dto.getElementsConstitutifsIds());
        //
	    //     // Nettoyer la relation précédente sans supprimer les EC
	    //     formation.getElementsConstitutifs().clear();
        //
	    //     // Associer les nouveaux EC à la formation
	    //     nouveauxEC.forEach(ec -> ec.setFormation(formation));
        //
	    //     formation.getElementsConstitutifs().addAll(nouveauxEC);
	    // }

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
