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
    private final EtudiantRepository etudiantRepository; // MODIFIÉ : Utilise EtudiantRepository
    private final ElementConstitutifRepository ecRepository;

    public InscriptionService(InscriptionRepository inscriptionRepository, 
                              EtudiantRepository etudiantRepository, 
                              ElementConstitutifRepository ecRepository) {
        this.inscriptionRepository = inscriptionRepository;
        this.etudiantRepository = etudiantRepository;
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
        // MODIFIÉ : On récupère l'entité Etudiant (le dossier académique)
        Etudiant etudiant = etudiantRepository.findById(request.getEtudiantId())
                .orElseThrow(() -> new EntityNotFoundException("Dossier étudiant non trouvé"));

        ElementConstitutif matiere = ecRepository.findById(request.getEcId())
                .orElseThrow(() -> new EntityNotFoundException("Matière non trouvée"));

        if (inscriptionRepository.existsByEtudiantIdAndMatiereId(etudiant.getId(), matiere.getId())) {
            throw new IllegalStateException("Déjà inscrit à cette matière.");
        }

        Inscription inscription = new Inscription();
        inscription.setEtudiant(etudiant); // Accepte maintenant l'objet Etudiant corrigé
        inscription.setMatiere(matiere);
        
        return mapToDto(inscriptionRepository.save(inscription));
    }

    @Transactional
    public InscriptionResponseDto validerInscription(InscriptionValidationRequest request) {
        Inscription ins = inscriptionRepository.findById(request.getInscriptionId())
                .orElseThrow(() -> new EntityNotFoundException("Inscription non trouvée"));

        if (!"EN_ATTENTE".equals(ins.getStatut())) {
            throw new IllegalStateException("Inscription déjà traitée.");
        }

        ins.setStatut(request.getStatut());
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
        
        // Accès aux données via la relation Inscription -> Etudiant -> Utilisateur
        Etudiant etudiant = ins.getEtudiant();
        dto.setEtudiantId(etudiant.getId());
        
        if (etudiant.getUtilisateur() != null) {
            dto.setEtudiantNomComplet(etudiant.getUtilisateur().getPrenom() + " " + etudiant.getUtilisateur().getNom());
        } else {
            dto.setEtudiantNomComplet("Nom inconnu");
        }

        dto.setEcId(ins.getMatiere().getId());
        dto.setEcNom(ins.getMatiere().getNom());
        dto.setDateInscription(ins.getDateInscription().toString());
        return dto;
    }
}
