package com.moscepa.service;

import com.moscepa.entity.Inscription;
import com.moscepa.entity.Utilisateur;
import com.moscepa.repository.InscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TuteurService {

    @Autowired
    private InscriptionRepository inscriptionRepository;

    /**
     * Récupère la liste unique des étudiants pour un tuteur spécifique.
     * Utilise la requête personnalisée de l'InscriptionRepository.
     */
    public List<Utilisateur> getMesEtudiants(Long tuteurId) {
        return inscriptionRepository.findEtudiantsByTuteur(tuteurId);
    }

    /**
     * Valide une inscription en changeant son statut.
     * Utilise @Transactional pour garantir la sécurité des données en base.
     */
    @Transactional
    public void validerEtudiant(Long inscriptionId) {
        Optional<Inscription> inscriptionOpt = inscriptionRepository.findById(inscriptionId);
        
        if (inscriptionOpt.isPresent()) {
            Inscription ins = inscriptionOpt.get();
            ins.setStatut("VALIDE");
            ins.setDateValidation(LocalDateTime.now());
            // Le save est automatique à la fin de la méthode grâce à @Transactional
            inscriptionRepository.save(ins);
        } else {
            throw new RuntimeException("Inscription avec l'ID " + inscriptionId + " introuvable.");
        }
    }
}
