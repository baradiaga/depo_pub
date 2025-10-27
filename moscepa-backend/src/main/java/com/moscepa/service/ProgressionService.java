package com.moscepa.service;

import com.moscepa.dto.MatiereStatutDto;
import com.moscepa.entity.Etudiant;
import com.moscepa.entity.Inscription;
import com.moscepa.entity.Matiere;
import com.moscepa.repository.EtudiantRepository; // <-- IMPORT NÉCESSAIRE
import com.moscepa.repository.InscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProgressionService {

    private final InscriptionRepository inscriptionRepository;
    private final EtudiantRepository etudiantRepository; // <-- AJOUT DE L'INJECTION

    // Mettez à jour le constructeur pour inclure EtudiantRepository
    public ProgressionService(InscriptionRepository inscriptionRepository, EtudiantRepository etudiantRepository) {
        this.inscriptionRepository = inscriptionRepository;
        this.etudiantRepository = etudiantRepository; // <-- AJOUT
    }

    /**
     * Trouve les matières pour l'utilisateur connecté.
     * @param utilisateurId L'ID de l'entité Utilisateur (venant du token JWT).
     * @return Une liste de DTOs des matières de l'étudiant.
     */
    @Transactional(readOnly = true)
    public List<MatiereStatutDto> findMatieresByEtudiant(Long utilisateurId) {
        
        // --- CORRECTION DE LA LOGIQUE ---
        // 1. À partir de l'ID de l'utilisateur, on trouve le profil Etudiant correspondant.
        //    On utilise la méthode que vous aviez déjà dans votre EtudiantRepository.
        Etudiant etudiant = etudiantRepository.findByUtilisateurId(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Aucun profil étudiant trouvé pour l'utilisateur avec l'ID: " + utilisateurId));

        // 2. On utilise maintenant l'ID de l'entité Etudiant pour trouver ses inscriptions.
        List<Inscription> inscriptions = inscriptionRepository.findByEtudiantId(etudiant.getId());

        // 3. La transformation en DTO reste la même.
        return inscriptions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Méthode privée pour convertir une Inscription en MatiereStatutDto.
     */
    private MatiereStatutDto convertToDto(Inscription inscription) {
        Matiere matiere = inscription.getMatiere();
        return new MatiereStatutDto(
                matiere.getId(),
                matiere.getNom(),
                matiere.getOrdre(),
                matiere.getEc(),
                matiere.getCoefficient(),
                inscription.getStatut()
        );
    }
}
