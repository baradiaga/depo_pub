// Fichier : src/main/java/com/moscepa/service/EtudiantService.java (Version Finale Corrigée)

package com.moscepa.service;

import com.moscepa.dto.EtudiantRegistrationDto;
import com.moscepa.dto.MatiereInscriteDto;
import com.moscepa.entity.ElementConstitutif;
import com.moscepa.entity.Role;
import com.moscepa.entity.Utilisateur;
import com.moscepa.repository.ElementConstitutifRepository; // <-- IMPORT IMPORTANT
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

    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    
    // On injecte le REPOSITORY, pas le service, pour avoir accès aux entités
    @Autowired private ElementConstitutifRepository elementConstitutifRepository;

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
            String sql = "INSERT IGNORE INTO moscepa_inscriptions (etudiant_id, ec_id) VALUES (:etudiantId, :ecId)";
            for (Long matiereId : dto.getMatiereIds()) {
                entityManager.createNativeQuery(sql)
                        .setParameter("etudiantId", etudiantSauvegarde.getId())
                        .setParameter("ecId", matiereId)
                        .executeUpdate();
            }
        }
        
        return etudiantSauvegarde;
    }

    /**
     * Récupère les matières inscrites d'un étudiant sous forme de DTO enrichis.
     * CORRECTION : Appelle directement le repository pour obtenir les entités.
     */
    @Transactional(readOnly = true)
    public List<MatiereInscriteDto> getMatieresInscrites(Long utilisateurId) {
        // On appelle la méthode du REPOSITORY qui renvoie des ENTITÉS
        return elementConstitutifRepository.findMatieresByEtudiantIdSqlNatif(utilisateurId).stream()
            .map(this::convertToMatiereInscriteDto) // Maintenant, le type correspond !
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
