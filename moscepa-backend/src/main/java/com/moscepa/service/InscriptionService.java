package com.moscepa.service;

import com.moscepa.dto.*;
import com.moscepa.entity.*;
import com.moscepa.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ElementConstitutifRepository ecRepository;

    public InscriptionService(InscriptionRepository inscriptionRepository, 
                              UtilisateurRepository utilisateurRepository, 
                              ElementConstitutifRepository ecRepository) {
        this.inscriptionRepository = inscriptionRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.ecRepository = ecRepository;
    }

    // --- Récupérer uniquement les inscriptions VALIDÉES pour l'étudiant ---
    public List<InscriptionResponseDto> getMesInscriptionsValidees(Long etudiantId) {
        return inscriptionRepository.findByEtudiantIdAndStatut(etudiantId, "VALIDE")
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional
    public InscriptionResponseDto inscrireEtudiant(InscriptionRequestDto request) {
        Utilisateur etudiant = utilisateurRepository.findById(request.getEtudiantId())
                .orElseThrow(() -> new EntityNotFoundException("Étudiant non trouvé"));

        ElementConstitutif matiere = ecRepository.findById(request.getEcId())
                .orElseThrow(() -> new EntityNotFoundException("Matière non trouvée"));

        if (inscriptionRepository.existsByEtudiantIdAndMatiereId(etudiant.getId(), matiere.getId())) {
            throw new IllegalStateException("Déjà inscrit à cette matière.");
        }

        Inscription inscription = new Inscription();
        inscription.setEtudiant(etudiant);
        inscription.setMatiere(matiere);
        // Le statut "EN_ATTENTE" est géré par @PrePersist dans l'entité
        
        return mapToDto(inscriptionRepository.save(inscription));
    }

    @Transactional
    public InscriptionResponseDto validerInscription(InscriptionValidationRequest request) {
        Inscription ins = inscriptionRepository.findById(request.getInscriptionId())
                .orElseThrow(() -> new EntityNotFoundException("Inscription non trouvée"));

        if (!"EN_ATTENTE".equals(ins.getStatut())) {
            throw new IllegalStateException("Inscription déjà traitée.");
        }

        ins.setStatut(request.getStatut()); // "VALIDE" ou "REJETE"
        ins.setDateValidation(LocalDateTime.now());
        return mapToDto(inscriptionRepository.save(ins));
    }

    public List<InscriptionResponseDto> getInscriptionsEnAttente() {
        return inscriptionRepository.findByStatut("EN_ATTENTE").stream()
                .map(this::mapToDto).toList();
    }

    private InscriptionResponseDto mapToDto(Inscription ins) {
        InscriptionResponseDto dto = new InscriptionResponseDto();
        dto.setId(ins.getId());
        dto.setStatut(ins.getStatut());
        dto.setActif(ins.isActif());
        dto.setEtudiantId(ins.getEtudiant().getId());
        dto.setEtudiantNomComplet(ins.getEtudiant().getPrenom() + " " + ins.getEtudiant().getNom());
        dto.setEcId(ins.getMatiere().getId());
        dto.setEcNom(ins.getMatiere().getNom());
        dto.setDateInscription(ins.getDateInscription().toString());
        return dto;
    }
}
