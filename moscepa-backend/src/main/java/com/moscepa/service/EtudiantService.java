// Fichier : src/main/java/com/moscepa/service/EtudiantService.java (Corrigé et Final)

package com.moscepa.service;

import com.moscepa.dto.ElementConstitutifResponseDto;
import com.moscepa.dto.EtudiantRegistrationDto;
import com.moscepa.entity.ElementConstitutif;
import com.moscepa.entity.Role;
import com.moscepa.entity.Utilisateur;
import com.moscepa.repository.ElementConstitutifRepository;
import com.moscepa.repository.UtilisateurRepository;
import org.modelmapper.ModelMapper; // <-- IMPORT MANQUANT AJOUTÉ
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors; // <-- IMPORT MANQUANT AJOUTÉ

@Service
public class EtudiantService {

    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private ElementConstitutifRepository ecRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    
    // ====================================================================
    // === INJECTION MANQUANTE AJOUTÉE ICI                              ===
    // ====================================================================
    @Autowired
    private ModelMapper modelMapper;

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
        
        List<ElementConstitutif> matieres = ecRepository.findAllById(dto.getMatiereIds());
        if (matieres.size() != dto.getMatiereIds().size()) {
            throw new IllegalStateException("Une ou plusieurs matières sélectionnées sont invalides.");
        }

        nouvelEtudiant.setMatieresInscrites(new HashSet<>(matieres));

        return utilisateurRepository.save(nouvelEtudiant);
    }

    // ====================================================================
    // === MÉTHODE DÉPLACÉE ET CORRIGÉE ICI                             ===
    // ====================================================================
    @Transactional(readOnly = true)
    public List<ElementConstitutifResponseDto> getMatieresInscrites(Long utilisateurId) {
        Utilisateur etudiant = utilisateurRepository.findById(utilisateurId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return etudiant.getMatieresInscrites().stream()
            .map(ec -> modelMapper.map(ec, ElementConstitutifResponseDto.class))
            .collect(Collectors.toList());
    }
}
