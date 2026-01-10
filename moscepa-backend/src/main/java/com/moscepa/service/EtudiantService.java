package com.moscepa.service;

import com.moscepa.dto.EtudiantRegistrationDto;
import com.moscepa.dto.InscriptionRequestDto;
import com.moscepa.dto.MatiereInscriteDto;
import com.moscepa.entity.*;
import com.moscepa.repository.EtudiantRepository;
import com.moscepa.repository.FormationRepository;
import com.moscepa.repository.InscriptionRepository;
import com.moscepa.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EtudiantService {

    @Autowired 
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired 
    private EtudiantRepository etudiantRepository;
    
    @Autowired 
    private FormationRepository formationRepository;
    
    @Autowired 
    private InscriptionRepository inscriptionRepository;

    @Autowired 
    private InscriptionService inscriptionService;
    
    @Autowired 
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void inscrireEtudiant(EtudiantRegistrationDto dto) {
        // 1. Validation de l'email unique
        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("Un compte avec l'email " + dto.getEmail() + " existe déjà.");
        }

        // 2. Récupération de la formation choisie (Lien dynamique)
        Formation formation = formationRepository.findById(dto.getFormationId())
            .orElseThrow(() -> new RuntimeException("La formation sélectionnée (ID: " + dto.getFormationId() + ") est introuvable."));

        // 3. Création du compte Utilisateur (Authentification)
        Utilisateur compte = new Utilisateur();
        compte.setNom(dto.getNom());
        compte.setPrenom(dto.getPrenom());
        compte.setEmail(dto.getEmail());
        compte.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        compte.setRole(Role.ETUDIANT);
        compte.setActif(true);
        Utilisateur compteSauvegarde = utilisateurRepository.save(compte);

        // 4. Création du dossier Etudiant (Données Académiques)
        Etudiant etudiant = new Etudiant();
        etudiant.setUtilisateur(compteSauvegarde);
        etudiant.setFormation(formation); // Liaison réelle avec l'objet Formation
        
        // Mapping des champs personnels
        etudiant.setDateDeNaissance(LocalDate.parse(dto.getDateDeNaissance()));
        etudiant.setLieuDeNaissance(dto.getLieuDeNaissance());
        etudiant.setNationalite(dto.getNationalite());
        etudiant.setSexe(dto.getSexe());
        etudiant.setAdresse(dto.getAdresse());
        etudiant.setTelephone(dto.getTelephone());
        etudiant.setAnneeAcademique(dto.getAnneeAcademique());
        
        Etudiant etudiantSauvegarde = etudiantRepository.save(etudiant);

        // 5. Inscriptions aux matières (Optionnel selon le DTO)
        if (dto.getMatiereIds() != null && !dto.getMatiereIds().isEmpty()) {
            for (Long matiereId : dto.getMatiereIds()) {
                InscriptionRequestDto inscriptionRequest = new InscriptionRequestDto();
                inscriptionRequest.setEtudiantId(etudiantSauvegarde.getId());
                inscriptionRequest.setEcId(matiereId);
                inscriptionService.inscrireEtudiant(inscriptionRequest);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<MatiereInscriteDto> getMatieresInscrites(Long utilisateurId) {
        List<Inscription> inscriptionsValides = inscriptionRepository.findByEtudiantIdAndStatut(utilisateurId, "VALIDE");

        return inscriptionsValides.stream()
            .map(this::convertToMatiereInscriteDto)
            .collect(Collectors.toList());
    }

    private MatiereInscriteDto convertToMatiereInscriteDto(Inscription inscription) {
        ElementConstitutif ec = inscription.getMatiere();
        MatiereInscriteDto dto = new MatiereInscriteDto();
        dto.setId(ec.getId());
        dto.setNomEc(ec.getNom());
        dto.setCodeEc(ec.getCode());
        dto.setCoefficient(ec.getCredit());
        dto.setStatut("VALIDE");

        if (ec.getUniteEnseignement() != null) {
            dto.setNomUe(ec.getUniteEnseignement().getNom());
            dto.setCodeUe(ec.getUniteEnseignement().getCode());
        }
        return dto;
    }
}
