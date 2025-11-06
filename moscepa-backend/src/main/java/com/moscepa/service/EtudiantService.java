// Fichier : src/main/java/com/moscepa/service/EtudiantService.java (Version Complète et Mise à Jour)

package com.moscepa.service;

import com.moscepa.dto.EtudiantRegistrationDto;
import com.moscepa.dto.MatiereInscriteDto; // <-- IMPORT DU NOUVEAU DTO
import com.moscepa.entity.ElementConstitutif;
import com.moscepa.entity.Role;
import com.moscepa.entity.Utilisateur;
import com.moscepa.repository.ElementConstitutifRepository;
import com.moscepa.repository.UtilisateurRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EtudiantService {

    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private ElementConstitutifRepository ecRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ModelMapper modelMapper;

    /**
     * Inscrit un nouvel étudiant et l'associe aux matières sélectionnées.
     */
    @Transactional
    public Utilisateur inscrireEtudiant(EtudiantRegistrationDto dto) {
        // Ligne de débogage pour voir ce qui arrive du frontend
        System.out.println(">>> [SERVICE] Début de l'inscription pour l'email : " + dto.getEmail());
        System.out.println(">>> [SERVICE] IDs des matières reçues : " + dto.getMatiereIds());

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
        
        if (dto.getMatiereIds() != null && !dto.getMatiereIds().isEmpty()) {
            System.out.println(">>> [SERVICE] Recherche des matières en base de données...");
            List<ElementConstitutif> matieres = ecRepository.findAllById(dto.getMatiereIds());
            
            System.out.println(">>> [SERVICE] Assignation de " + matieres.size() + " matières à l'étudiant.");
            nouvelEtudiant.setMatieresInscrites(new HashSet<>(matieres));
        } else {
            System.out.println(">>> [SERVICE] Aucune matière à assigner.");
        }

        System.out.println(">>> [SERVICE] Sauvegarde de l'étudiant en base de données...");
        Utilisateur etudiantSauvegarde = utilisateurRepository.save(nouvelEtudiant);
        System.out.println(">>> [SERVICE] Étudiant sauvegardé avec l'ID : " + etudiantSauvegarde.getId());
        
        return etudiantSauvegarde;
    }

    // ====================================================================
    // === MÉTHODE MISE À JOUR POUR RENVOYER LES DONNÉES ENRICHIES       ===
    // ====================================================================
    /**
     * Récupère les matières inscrites d'un étudiant sous forme de DTO enrichis pour le tableau.
     */
    @Transactional(readOnly = true)
    public List<MatiereInscriteDto> getMatieresInscrites(Long utilisateurId) {
        // La méthode findById a été surchargée dans le repository pour utiliser @EntityGraph
        Utilisateur etudiant = utilisateurRepository.findById(utilisateurId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + utilisateurId));

        // On transforme chaque ElementConstitutif en MatiereInscriteDto
        return etudiant.getMatieresInscrites().stream()
            .map(this::convertToMatiereInscriteDto)
            .collect(Collectors.toList());
    }

    // ====================================================================
    // === NOUVELLE MÉTHODE PRIVÉE POUR LA CONVERSION                   ===
    // ====================================================================
    /**
     * Convertit une entité ElementConstitutif en son DTO pour l'affichage dans le tableau.
     */
    private MatiereInscriteDto convertToMatiereInscriteDto(ElementConstitutif ec) {
        MatiereInscriteDto dto = new MatiereInscriteDto();
        
        // Informations de l'EC
        dto.setId(ec.getId());
        dto.setNomEc(ec.getNom());
        dto.setCodeEc(ec.getCode());
        dto.setCoefficient(ec.getCredit()); // On utilise les crédits comme coefficient
        dto.setStatut("NV"); // Statut factice pour le moment, à remplacer par la vraie logique plus tard

        // Informations de l'UE parente (avec une vérification pour éviter les erreurs)
        if (ec.getUniteEnseignement() != null) {
            dto.setNomUe(ec.getUniteEnseignement().getNom());
            dto.setCodeUe(ec.getUniteEnseignement().getCode());
        } else {
            // S'il n'y a pas d'UE, on met des valeurs par défaut pour éviter les erreurs "null"
            dto.setNomUe("Non applicable");
            dto.setCodeUe("N/A");
        }

        return dto;
    }
}
