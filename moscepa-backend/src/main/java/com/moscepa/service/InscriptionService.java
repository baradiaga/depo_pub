package com.moscepa.service;

import com.moscepa.dto.InscriptionRequestDto;
import com.moscepa.dto.InscriptionResponseDto;
import com.moscepa.dto.InscriptionValidationRequest;
import com.moscepa.entity.ElementConstitutif;
import com.moscepa.entity.Inscription;
import com.moscepa.entity.Utilisateur;
import com.moscepa.repository.ElementConstitutifRepository;
import com.moscepa.repository.InscriptionRepository;
import com.moscepa.repository.UtilisateurRepository;
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

    public InscriptionService(
            InscriptionRepository inscriptionRepository,
            UtilisateurRepository utilisateurRepository,
            ElementConstitutifRepository ecRepository
    ) {
        this.inscriptionRepository = inscriptionRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.ecRepository = ecRepository;
    }

    // ===============================================================
    // 🔹 MAPPER — Convertit une entité Inscription en DTO complet
    // ===============================================================
    private InscriptionResponseDto mapToDto(Inscription inscription) {

        InscriptionResponseDto dto = new InscriptionResponseDto();

        dto.setId(inscription.getId());
        dto.setStatut(inscription.getStatut());
        dto.setActif(inscription.isActif());
        dto.setDateInscription(
                inscription.getDateInscription() != null ? inscription.getDateInscription().toString() : null
        );
        dto.setDateValidation(
                inscription.getDateValidation() != null ? inscription.getDateValidation().toString() : null
        );

        // --- Étudiant ---
        Utilisateur etu = inscription.getEtudiant();
        if (etu != null) {
            dto.setEtudiantId(etu.getId());
            dto.setEtudiantNomComplet(etu.getPrenom() + " " + etu.getNom());
            dto.setEtudiantEmail(etu.getEmail());
        }

        // --- Matière ---
        ElementConstitutif ec = inscription.getMatiere();
        if (ec != null) {
            dto.setEcId(ec.getId());
            dto.setEcCode(ec.getCode());
            dto.setEcNom(ec.getNom());
        }

        return dto;
    }

    // ===============================================================
    // 🔹 INSCRIPTION D'UN ÉTUDIANT (VERSION CORRIGÉE)
    // ===============================================================
   @Transactional
public InscriptionResponseDto inscrireEtudiant(InscriptionRequestDto request) {
    Utilisateur etudiant = utilisateurRepository.findById(request.getEtudiantId())
            .orElseThrow(() -> new EntityNotFoundException(
                    "Étudiant non trouvé avec l'ID: " + request.getEtudiantId()));

    ElementConstitutif matiere = ecRepository.findById(request.getEcId())
            .orElseThrow(() -> new EntityNotFoundException(
                    "Matière non trouvée avec l'ID: " + request.getEcId()));

    // Vérifier si déjà inscrit
    if (inscriptionRepository.existsByEtudiantIdAndMatiereId(etudiant.getId(), matiere.getId())) {
        throw new IllegalStateException("L'étudiant est déjà inscrit à cette matière.");
    }

    // Nouvelle inscription - SEULEMENT ces 2 setters
    Inscription inscription = new Inscription();
    inscription.setEtudiant(etudiant);
    inscription.setMatiere(matiere);

    // Log avant sauvegarde
    System.out.println("DEBUG - Avant sauvegarde :");
    System.out.println("  Statut : " + inscription.getStatut());
    System.out.println("  Actif : " + inscription.isActif());
    System.out.println("  DateInscription : " + inscription.getDateInscription());

    Inscription saved = inscriptionRepository.save(inscription);

    // Log après sauvegarde
    System.out.println("DEBUG - Après sauvegarde :");
    System.out.println("  ID : " + saved.getId());
    System.out.println("  Statut : " + saved.getStatut());
    System.out.println("  Actif : " + saved.isActif());
    System.out.println("  DateInscription : " + saved.getDateInscription());

    return mapToDto(saved);
}

    // ===============================================================
    // 🔹 VALIDER / REJETER UNE INSCRIPTION
    // ===============================================================
    @Transactional
    public InscriptionResponseDto validerInscription(InscriptionValidationRequest request) {

        Inscription inscription = inscriptionRepository.findById(request.getInscriptionId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Inscription non trouvée avec l'ID: " + request.getInscriptionId()));

        String nouveauStatut = request.getStatut();

        if (!"VALIDE".equals(nouveauStatut) && !"REJETE".equals(nouveauStatut)) {
            throw new IllegalArgumentException(
                    "Statut invalide. Utiliser VALIDE ou REJETE.");
        }

        if (!"EN_ATTENTE".equals(inscription.getStatut())) {
            throw new IllegalStateException(
                    "Inscription déjà traitée. Statut actuel: " + inscription.getStatut());
        }

        inscription.setStatut(nouveauStatut);
        inscription.setDateValidation(LocalDateTime.now());

        return mapToDto(inscriptionRepository.save(inscription));
    }

    // ===============================================================
    // 🔹 ACTIVER / DÉSACTIVER UNE INSCRIPTION
    // ===============================================================
    @Transactional
    public InscriptionResponseDto changerStatutActif(Long inscriptionId, boolean actif) {

        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Inscription non trouvée avec l'ID: " + inscriptionId));

        inscription.setActif(actif);

        return mapToDto(inscriptionRepository.save(inscription));
    }

    // ===============================================================
    // 🔹 LISTE DES INSCRIPTIONS EN ATTENTE
    // ===============================================================
    public List<InscriptionResponseDto> getInscriptionsEnAttente() {
    List<Inscription> inscriptions = inscriptionRepository.findByStatut("EN_ATTENTE");
    
    System.out.println("DEBUG - getInscriptionsEnAttente :");
    System.out.println("  Nombre d'inscriptions trouvées : " + inscriptions.size());
    for (Inscription ins : inscriptions) {
        System.out.println("    ID : " + ins.getId() + ", Statut : " + ins.getStatut());
    }
    
    return inscriptions.stream()
            .map(this::mapToDto)
            .toList();
}
}