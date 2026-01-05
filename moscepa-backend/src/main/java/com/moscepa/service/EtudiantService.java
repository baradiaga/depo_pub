package com.moscepa.service;

import com.moscepa.dto.EtudiantRegistrationDto;
import com.moscepa.dto.InscriptionRequestDto;
import com.moscepa.dto.MatiereInscriteDto;
import com.moscepa.entity.ElementConstitutif;
import com.moscepa.entity.Inscription;
import com.moscepa.entity.Role;
import com.moscepa.entity.Utilisateur;
import com.moscepa.repository.InscriptionRepository;
import com.moscepa.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EtudiantService {

    @Autowired 
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired 
    private PasswordEncoder passwordEncoder;
    
    @Autowired 
    private InscriptionRepository inscriptionRepository; // CHANGEMENT : Injection du Repository d'inscription

    @Autowired 
    private InscriptionService inscriptionService;

    @Transactional
    public Utilisateur inscrireEtudiant(EtudiantRegistrationDto dto) {
        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("Un utilisateur avec l'email " + dto.getEmail() + " existe déjà.");
        }

        Utilisateur nouvelEtudiant = new Utilisateur();
        nouvelEtudiant.setNom(dto.getNom());
        nouvelEtudiant.setPrenom(dto.getPrenom());
        nouvelEtudiant.setEmail(dto.getEmail());
        nouvelEtudiant.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        nouvelEtudiant.setRole(Role.ETUDIANT);
        nouvelEtudiant.setActif(true);
        nouvelEtudiant.setDateDeNaissance(dto.getDateDeNaissance());
        nouvelEtudiant.setLieuDeNaissance(dto.getLieuDeNaissance());
        nouvelEtudiant.setNationalite(dto.getNationalite());
        nouvelEtudiant.setSexe(dto.getSexe());
        nouvelEtudiant.setAdresse(dto.getAdresse());
        nouvelEtudiant.setTelephone(dto.getTelephone());
        nouvelEtudiant.setAnneeAcademique(dto.getAnneeAcademique());
        nouvelEtudiant.setFiliere(dto.getFiliere());
        
        Utilisateur etudiantSauvegarde = utilisateurRepository.save(nouvelEtudiant);

        if (dto.getMatiereIds() != null && !dto.getMatiereIds().isEmpty()) {
            for (Long matiereId : dto.getMatiereIds()) {
                InscriptionRequestDto inscriptionRequest = new InscriptionRequestDto();
                inscriptionRequest.setEtudiantId(etudiantSauvegarde.getId());
                inscriptionRequest.setEcId(matiereId);
                // Le service d'inscription crée l'entrée avec statut "EN_ATTENTE"
                inscriptionService.inscrireEtudiant(inscriptionRequest);
            }
        }
        return etudiantSauvegarde;
    }

    /**
     * CORRECTION MAJEURE : 
     * On ne récupère plus les matières via une requête SQL native sur les EC,
     * mais via les Inscriptions filtrées par statut "VALIDE".
     */
    @Transactional(readOnly = true)
    public List<MatiereInscriteDto> getMatieresInscrites(Long utilisateurId) {
        // On récupère uniquement les inscriptions validées
        List<Inscription> inscriptionsValides = inscriptionRepository.findByEtudiantIdAndStatut(utilisateurId, "VALIDE");

        return inscriptionsValides.stream()
            .map(this::convertToMatiereInscriteDto)
            .collect(Collectors.toList());
    }

    /**
     * Mapper modifié pour extraire les données de l'entité Inscription
     */
    private MatiereInscriteDto convertToMatiereInscriteDto(Inscription inscription) {
        ElementConstitutif ec = inscription.getMatiere();
        MatiereInscriteDto dto = new MatiereInscriteDto();
        
        dto.setId(ec.getId());
        dto.setNomEc(ec.getNom());
        dto.setCodeEc(ec.getCode());
        dto.setCoefficient(ec.getCredit());
        dto.setStatut("VALIDE"); // Si elle est là, c'est qu'elle est valide

        if (ec.getUniteEnseignement() != null) {
            dto.setNomUe(ec.getUniteEnseignement().getNom());
            dto.setCodeUe(ec.getUniteEnseignement().getCode());
        } else {
            dto.setNomUe("Non applicable");
            dto.setCodeUe("N/A");
        }
        return dto;
    }
}
