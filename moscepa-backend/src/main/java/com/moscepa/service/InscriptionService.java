// Fichier : src/main/java/com/moscepa/service/InscriptionService.java (Version Correcte)

package com.moscepa.service;

import com.moscepa.dto.InscriptionRequest;
import com.moscepa.entity.ElementConstitutif;
import com.moscepa.entity.Utilisateur;
import com.moscepa.repository.ElementConstitutifRepository;
import com.moscepa.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InscriptionService {

    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private ElementConstitutifRepository ecRepository;

    @Transactional
    public void inscrire(InscriptionRequest request) {
        Utilisateur etudiant = utilisateurRepository.findById(request.getEtudiantId())
                .orElseThrow(() -> new EntityNotFoundException("Étudiant non trouvé avec l'ID: " + request.getEtudiantId()));

        ElementConstitutif ec = ecRepository.findById(request.getEcId())
                .orElseThrow(() -> new EntityNotFoundException("Matière non trouvée avec l'ID: " + request.getEcId()));

        etudiant.getMatieresInscrites().add(ec);

        utilisateurRepository.save(etudiant);
    }
}
