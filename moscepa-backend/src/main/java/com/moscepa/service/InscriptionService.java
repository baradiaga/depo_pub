// Fichier : src/main/java/com/moscepa/service/InscriptionService.java

package com.moscepa.service;

import com.moscepa.dto.InscriptionRequestDto; // On va créer ce DTO
import com.moscepa.entity.ElementConstitutif;
import com.moscepa.entity.Inscription;
import com.moscepa.entity.Utilisateur;
import com.moscepa.repository.ElementConstitutifRepository;
import com.moscepa.repository.InscriptionRepository;
import com.moscepa.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ElementConstitutifRepository ecRepository;

    public InscriptionService(InscriptionRepository inscriptionRepository, UtilisateurRepository utilisateurRepository, ElementConstitutifRepository ecRepository) {
        this.inscriptionRepository = inscriptionRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.ecRepository = ecRepository;
    }

    @Transactional
    public Inscription inscrireEtudiant(InscriptionRequestDto request) {
        Utilisateur etudiant = utilisateurRepository.findById(request.getEtudiantId())
                .orElseThrow(() -> new EntityNotFoundException("Étudiant non trouvé avec l'ID: " + request.getEtudiantId()));

        ElementConstitutif matiere = ecRepository.findById(request.getEcId())
                .orElseThrow(() -> new EntityNotFoundException("Matière non trouvée avec l'ID: " + request.getEcId()));

        
         if (inscriptionRepository.existsByEtudiantIdAndMatiereId(etudiant.getId(), matiere.getId())) {
            throw new IllegalStateException("L'étudiant est déjà inscrit à cette matière.");
        }

        Inscription nouvelleInscription = new Inscription();
        nouvelleInscription.setEtudiant(etudiant);
        nouvelleInscription.setMatiere(matiere);

        return inscriptionRepository.save(nouvelleInscription);
    }
}
