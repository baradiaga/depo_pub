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
// NOUVEAUX IMPORTS
import com.moscepa.entity.Etablissement;
import com.moscepa.entity.Uefr;
import com.moscepa.entity.Departement;
import com.moscepa.repository.ElementConstitutifRepository;
import com.moscepa.repository.FormationRepository;
import com.moscepa.repository.UtilisateurRepository;
// NOUVEAUX REPOSITORIES
import com.moscepa.repository.EtablissementRepository;
import com.moscepa.repository.UefrRepository;
import com.moscepa.repository.DepartementRepository;
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
    // NOUVEAUX REPOSITORIES
    private final EtablissementRepository etablissementRepository;
    private final UefrRepository uefrRepository;
    private final DepartementRepository departementRepository;

    public FormationService(FormationRepository formationRepository,
                            UtilisateurRepository utilisateurRepository,
                            ElementConstitutifRepository elementConstitutifRepository,
                            // NOUVEAUX REPOSITORIES
                            EtablissementRepository etablissementRepository,
                            UefrRepository uefrRepository,
                            DepartementRepository departementRepository) {
        this.formationRepository = formationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.elementConstitutifRepository = elementConstitutifRepository;
        // NOUVEAUX REPOSITORIES
        this.etablissementRepository = etablissementRepository;
        this.uefrRepository = uefrRepository;
        this.departementRepository = departementRepository;
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

        // ========================
        // NOUVEAUX CHAMPS - Références administratives
        // ========================

        // Vérifier et récupérer l'établissement
        if (dto.getEtablissementId() != null) {
            Etablissement etablissement = etablissementRepository.findById(dto.getEtablissementId())
                .orElseThrow(() -> new EntityNotFoundException("Établissement non trouvé avec l'ID: " + dto.getEtablissementId()));
            formation.setEtablissement(etablissement);
        }

        // Vérifier et récupérer l'UFR
        if (dto.getUefrId() != null) {
            Uefr uefr = uefrRepository.findById(dto.getUefrId())
                .orElseThrow(() -> new EntityNotFoundException("UFR non trouvée avec l'ID: " + dto.getUefrId()));
            formation.setUefr(uefr);
        }

        // Vérifier et récupérer le département
        if (dto.getDepartementId() != null) {
            Departement departement = departementRepository.findById(dto.getDepartementId())
                .orElseThrow(() -> new EntityNotFoundException("Département non trouvé avec l'ID: " + dto.getDepartementId()));
            formation.setDepartement(departement);
        }

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
    
    /**
     * Récupère toutes les formations créées par un utilisateur spécifique.
     * @param createurId L'ID de l'utilisateur (enseignant) créateur.
     * @return La liste des formations créées par cet utilisateur.
     */
    public List<FormationDetailDto> getFormationsByCreateurId(Long createurId) {
        return formationRepository.findAllByCreateurId(createurId).stream()
                .map(FormationDetailDto::new)
                .collect(Collectors.toList());
    }

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

        // ========================
        // NOUVEAUX CHAMPS - Références administratives
        // ========================

        // Vérifier et récupérer l'établissement
        if (dto.getEtablissementId() != null) {
            Etablissement etablissement = etablissementRepository.findById(dto.getEtablissementId())
                .orElseThrow(() -> new EntityNotFoundException("Établissement non trouvé avec l'ID: " + dto.getEtablissementId()));
            formation.setEtablissement(etablissement);
        } else {
            formation.setEtablissement(null);
        }

        // Vérifier et récupérer l'UFR
        if (dto.getUefrId() != null) {
            Uefr uefr = uefrRepository.findById(dto.getUefrId())
                .orElseThrow(() -> new EntityNotFoundException("UFR non trouvée avec l'ID: " + dto.getUefrId()));
            formation.setUefr(uefr);
        } else {
            formation.setUefr(null);
        }

        // Vérifier et récupérer le département
        if (dto.getDepartementId() != null) {
            Departement departement = departementRepository.findById(dto.getDepartementId())
                .orElseThrow(() -> new EntityNotFoundException("Département non trouvé avec l'ID: " + dto.getDepartementId()));
            formation.setDepartement(departement);
        } else {
            formation.setDepartement(null);
        }

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
            formation.getElementsConstitutifs().forEach(ec -> ec.setFormation(null));
            formation.getElementsConstitutifs().clear();
        }

        // Supprimer la formation
        formationRepository.delete(formation);
    }
}