// Fichier : src/main/java/com/moscepa/service/EtudiantService.java (Version Corrigée)

package com.moscepa.service;

import com.moscepa.dto.EtudiantRegistrationDto;
import com.moscepa.dto.InscriptionRequestDto;  // Nouveau DTO pour l'inscription
import com.moscepa.dto.MatiereInscriteDto;
import com.moscepa.entity.ElementConstitutif;
import com.moscepa.entity.Role;
import com.moscepa.entity.Utilisateur;
import com.moscepa.repository.ElementConstitutifRepository;
import com.moscepa.repository.UtilisateurRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
    private ElementConstitutifRepository elementConstitutifRepository;

    // INJECTION du service d'inscription
    @Autowired 
    private InscriptionService inscriptionService;

    @PersistenceContext
    private EntityManager entityManager;

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
            // REMPLACER la requête native par des appels au service d'inscription
            for (Long matiereId : dto.getMatiereIds()) {
                InscriptionRequestDto inscriptionRequest = new InscriptionRequestDto();
                inscriptionRequest.setEtudiantId(etudiantSauvegarde.getId());
                inscriptionRequest.setEcId(matiereId);
                // Appel du service qui va créer une inscription complète avec statut "EN_ATTENTE"
                inscriptionService.inscrireEtudiant(inscriptionRequest);
            }
        }
        
        return etudiantSauvegarde;
    }

    // ... le reste de la classe (getMatieresInscrites et convertToMatiereInscriteDto) reste inchangé
    @Transactional(readOnly = true)
    public List<MatiereInscriteDto> getMatieresInscrites(Long utilisateurId) {
        return elementConstitutifRepository.findMatieresByEtudiantIdSqlNatif(utilisateurId).stream()
            .map(this::convertToMatiereInscriteDto)
            .collect(Collectors.toList());
    }

    private MatiereInscriteDto convertToMatiereInscriteDto(ElementConstitutif ec) {
        MatiereInscriteDto dto = new MatiereInscriteDto();
        dto.setId(ec.getId());
        dto.setNomEc(ec.getNom());
        dto.setCodeEc(ec.getCode());
        dto.setCoefficient(ec.getCredit());
        dto.setStatut("NV");
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