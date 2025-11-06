// Fichier : src/main/java/com/moscepa/service/ParcoursService.java (Version Finale Corrigée)

package com.moscepa.service;

import com.moscepa.dto.ParcoursDto;
import com.moscepa.dto.ParcoursItemDto;
import com.moscepa.entity.*;
import com.moscepa.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class ParcoursService {

    private final ParcoursRepository parcoursRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ChapitreRepository chapitreRepository;
    private final ResultatTestRepository resultatTestRepository;
    // ====================================================================
    // === CORRECTION APPLIQUÉE ICI : On supprime EtudiantRepository    ===
    // ====================================================================

    // On met à jour le constructeur pour ne plus injecter EtudiantRepository
    public ParcoursService(ParcoursRepository parcoursRepository, UtilisateurRepository utilisateurRepository, ChapitreRepository chapitreRepository, ResultatTestRepository resultatTestRepository) {
        this.parcoursRepository = parcoursRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.chapitreRepository = chapitreRepository;
        this.resultatTestRepository = resultatTestRepository;
    }

    public ParcoursDto getParcoursPourEtudiant(Long utilisateurId) {
        // ====================================================================
        // === CORRECTION APPLIQUÉE ICI : Logique simplifiée                ===
        // ====================================================================
        // On n'a plus besoin de chercher un "profil étudiant" séparé.
        // L'ID de l'utilisateur EST l'ID de l'étudiant.
        if (!utilisateurRepository.existsById(utilisateurId)) {
            throw new EntityNotFoundException("Aucun étudiant trouvé pour l'utilisateur ID: " + utilisateurId);
        }

        List<Parcours> parcoursList = parcoursRepository.findByUtilisateurIdOrderByDateAjoutDesc(utilisateurId);
        List<ParcoursItemDto> recommandes = new ArrayList<>();
        List<ParcoursItemDto> choisis = new ArrayList<>();

        for (Parcours parcours : parcoursList) {
            
            // On utilise directement l'ID de l'utilisateur
            List<ResultatTest> resultats = resultatTestRepository.findLatestByEtudiantAndChapitre(utilisateurId, parcours.getChapitre().getId());

            double scoreEnPourcentage = 0.0;
            if (!resultats.isEmpty()) {
                ResultatTest dernierResultat = resultats.get(0);
                if (dernierResultat.getScoreTotal() != null && dernierResultat.getScoreTotal() > 0) {
                    scoreEnPourcentage = (dernierResultat.getScore() / dernierResultat.getScoreTotal()) * 100;
                }
            }
            
            ParcoursItemDto dto = new ParcoursItemDto(
                parcours.getChapitre().getId(),
                parcours.getChapitre().getNom(),
                parcours.getChapitre().getElementConstitutif().getNom(),
                scoreEnPourcentage
            );

            if (parcours.getType() == Parcours.TypeParcours.RECOMMANDE) {
                recommandes.add(dto);
            }
            if (parcours.getType() == Parcours.TypeParcours.CHOISI) {
                choisis.add(dto);
            }
        }

        List<ParcoursItemDto> mixtes = Stream.concat(recommandes.stream(), choisis.stream()).distinct().collect(Collectors.toList());
        ParcoursDto resultatDto = new ParcoursDto();
        resultatDto.setRecommandes(recommandes);
        resultatDto.setChoisis(choisis);
        resultatDto.setMixtes(mixtes);
        return resultatDto;
    }

    @Transactional
    public void enregistrerChoixEtudiant(Long utilisateurId, List<Long> chapitreIds) {
        Utilisateur etudiant = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + utilisateurId));
        List<Chapitre> chapitres = chapitreRepository.findAllById(chapitreIds);
        List<Parcours> nouveauxParcours = new ArrayList<>();
        for (Chapitre chapitre : chapitres) {
            Parcours nouveauParcours = new Parcours();
            nouveauParcours.setUtilisateur(etudiant);
            nouveauParcours.setChapitre(chapitre);
            nouveauParcours.setType(Parcours.TypeParcours.CHOISI);
            nouveauParcours.setDateAjout(LocalDateTime.now());
            nouveauxParcours.add(nouveauParcours);
        }
        if (!nouveauxParcours.isEmpty()) {
            parcoursRepository.saveAll(nouveauxParcours);
        }
    }

    @Transactional
    public void enregistrerParcoursRecommande(Long utilisateurId, List<Long> chapitreIds) {
        Utilisateur etudiant = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + utilisateurId));
        List<Chapitre> chapitres = chapitreRepository.findAllById(chapitreIds);
        for (Chapitre chapitre : chapitres) {
            boolean existeDeja = parcoursRepository.existsByUtilisateurIdAndChapitreIdAndType(utilisateurId, chapitre.getId(), Parcours.TypeParcours.RECOMMANDE);
            if (!existeDeja) {
                Parcours nouveauParcours = new Parcours();
                nouveauParcours.setUtilisateur(etudiant);
                nouveauParcours.setChapitre(chapitre);
                nouveauParcours.setType(Parcours.TypeParcours.RECOMMANDE);
                nouveauParcours.setDateAjout(LocalDateTime.now());
                parcoursRepository.save(nouveauParcours);
            }
        }
    }
}
