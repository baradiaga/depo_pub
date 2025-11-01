package com.moscepa.service;

import com.moscepa.dto.EtudiantDto;
import com.moscepa.dto.EtudiantTestsDashboardDto;
import com.moscepa.dto.EtudiantTestsDashboardDto.MatiereDashboardDto;
import com.moscepa.dto.EtudiantTestsDashboardDto.ChapitreDashboardDto;
import com.moscepa.dto.EtudiantTestsDashboardDto.TestDashboardDto;
import com.moscepa.entity.*;
import com.moscepa.repository.*;
import com.moscepa.entity.Test;
import com.moscepa.entity.Chapitre;
import com.moscepa.entity.ResultatTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EtudiantService {

    @Autowired private InscriptionRepository inscriptionRepository;
    @Autowired private MatiereRepository matiereRepository;
    @Autowired private EtudiantRepository etudiantRepository;
    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ResultatTestRepository resultatTestRepository;
    @Autowired private ChapitreRepository chapitreRepository;

    public EtudiantDto inscrireEtudiant(EtudiantDto etudiantDto) {
        if (utilisateurRepository.existsByEmail(etudiantDto.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(etudiantDto.getNom());
        utilisateur.setPrenom(etudiantDto.getPrenom());
        utilisateur.setEmail(etudiantDto.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode("motdepasse123"));
        utilisateur.setRole(Role.ETUDIANT);
        utilisateur.setActif(true);
        utilisateur = utilisateurRepository.save(utilisateur);

        Etudiant etudiant = new Etudiant();
        etudiant.setUtilisateur(utilisateur);
        
        // Copie de tous les champs du DTO vers l'entité
        mapDtoToEntity(etudiantDto, etudiant);
        
        etudiant = etudiantRepository.save(etudiant);

        if (etudiantDto.getMatiereIds() != null && !etudiantDto.getMatiereIds().isEmpty()) {
            assignerInscriptions(etudiant, etudiantDto.getMatiereIds());
        }

        return convertToDto(etudiant);
    }

    @Transactional(readOnly = true)
    public List<EtudiantDto> getEtudiants() {
        return etudiantRepository.findAllActifs().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public EtudiantDto updateEtudiant(Long id, EtudiantDto etudiantDto) {
        Etudiant etudiant = etudiantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l'ID: " + id));

        Utilisateur utilisateur = etudiant.getUtilisateur();
        utilisateur.setNom(etudiantDto.getNom());
        utilisateur.setPrenom(etudiantDto.getPrenom());
        // ... (logique de mise à jour de l'email si nécessaire)

        mapDtoToEntity(etudiantDto, etudiant);

        inscriptionRepository.deleteAll(etudiant.getInscriptions());
        etudiant.getInscriptions().clear();
        
        if (etudiantDto.getMatiereIds() != null && !etudiantDto.getMatiereIds().isEmpty()) {
            assignerInscriptions(etudiant, etudiantDto.getMatiereIds());
        }
        
        etudiant = etudiantRepository.save(etudiant);
        return convertToDto(etudiant);
    }
     @Transactional(readOnly = true)
    public Optional<EtudiantDto> getEtudiantById(Long id) {
        return etudiantRepository.findById(id)
                .map(this::convertToDto);
    }

    /**
     * Récupère les données structurées pour le tableau de bord des tests de l'étudiant.
     */
    @Transactional(readOnly = true)
    public EtudiantTestsDashboardDto getEtudiantTestsDashboard(Long etudiantId) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l'ID: " + etudiantId));

        String nomComplet = etudiant.getUtilisateur().getNomComplet();

        List<MatiereDashboardDto> matieresDto = etudiant.getInscriptions().stream()
                .map(inscription -> {
                    Matiere matiere = inscription.getMatiere();
                    List<ChapitreDashboardDto> chapitresDto = matiere.getChapitres().stream()
                            .map(chapitre -> {
                                List<TestDashboardDto> testsDto = chapitre.getTests().stream()
                                        .map(test -> {
                                            Double meilleurScore = resultatTestRepository.findMaxScoreByEtudiantAndTest(etudiantId, test.getId())
                                                    .orElse(0.0); // 0.0 si aucun résultat

                                            return new TestDashboardDto(test.getId(), test.getTitre(), meilleurScore);
                                        })
                                        .collect(Collectors.toList());

                                return new ChapitreDashboardDto(chapitre.getId(), chapitre.getNom(), chapitre.getNiveau(), testsDto);
                            })
                            .collect(Collectors.toList());

                    return new MatiereDashboardDto(matiere.getId(), matiere.getNom(), inscription.getStatut(), chapitresDto);
                })
                .collect(Collectors.toList());

        return new EtudiantTestsDashboardDto(etudiantId, nomComplet, matieresDto);
    }

    // --- MÉTHODE MANQUANTE 2 ---
    public void deleteEtudiant(Long id) {
        Etudiant etudiant = etudiantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l'ID: " + id));
        
        // On ne supprime pas vraiment, on désactive l'utilisateur associé.
        // C'est une bonne pratique pour conserver l'historique.
        etudiant.getUtilisateur().setActif(false);
        utilisateurRepository.save(etudiant.getUtilisateur());
    }
    

    private void mapDtoToEntity(EtudiantDto dto, Etudiant entity) {
        entity.setDateDeNaissance(dto.getDateDeNaissance());
        entity.setLieuDeNaissance(dto.getLieuDeNaissance());
        entity.setNationalite(dto.getNationalite());
        entity.setSexe(dto.getSexe());
        entity.setAdresse(dto.getAdresse());
        entity.setTelephone(dto.getTelephone());
        entity.setAnneeAcademique(dto.getAnneeAcademique());
        entity.setFiliere(dto.getFiliere());
        entity.setNumeroMatricule("MAT-" + System.currentTimeMillis()); // Logique de génération simple

        if (dto.getEnseignantId() != null) {
            utilisateurRepository.findById(dto.getEnseignantId()).ifPresent(entity::setEnseignant);
        } else {
            entity.setEnseignant(null);
        }
    }

    private void assignerInscriptions(Etudiant etudiant, List<Long> matiereIds) {
        for (Long matiereId : matiereIds) {
            Matiere matiere = matiereRepository.findById(matiereId)
                    .orElseThrow(() -> new RuntimeException("Matière non trouvée avec l'ID: " + matiereId));
            
            Inscription inscription = new Inscription();
            inscription.setEtudiant(etudiant);
            inscription.setMatiere(matiere);
            inscription.setStatut("À reprendre");
            etudiant.getInscriptions().add(inscription);
        }
    }

    private EtudiantDto convertToDto(Etudiant etudiant) {
        EtudiantDto dto = new EtudiantDto();
        dto.setId(etudiant.getId());
        dto.setNom(etudiant.getNom());
        dto.setPrenom(etudiant.getPrenom());
        dto.setEmail(etudiant.getEmail());
        dto.setDateDeNaissance(etudiant.getDateDeNaissance());
        dto.setLieuDeNaissance(etudiant.getLieuDeNaissance());
        dto.setNationalite(etudiant.getNationalite());
        dto.setSexe(etudiant.getSexe());
        dto.setAdresse(etudiant.getAdresse());
        dto.setTelephone(etudiant.getTelephone());
        dto.setAnneeAcademique(etudiant.getAnneeAcademique());
        dto.setFiliere(etudiant.getFiliere());
        dto.setNumeroMatricule(etudiant.getNumeroMatricule());
        dto.setDateInscription(etudiant.getDateInscription());

        List<Long> matiereIds = etudiant.getInscriptions().stream()
                .map(inscription -> inscription.getMatiere().getId())
                .collect(Collectors.toList());
        dto.setMatiereIds(matiereIds);

        if (etudiant.getEnseignant() != null) {
            dto.setEnseignantId(etudiant.getEnseignant().getId());
            dto.setEnseignantNom(etudiant.getEnseignant().getNomComplet());
        }
        
        return dto;
    }
}
