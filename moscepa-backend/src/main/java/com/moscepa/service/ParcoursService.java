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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class ParcoursService {

    private final ParcoursRepository parcoursRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ChapitreRepository chapitreRepository;
    private final ResultatTestRepository resultatTestRepository;
    private final EtudiantRepository etudiantRepository;

    public ParcoursService(ParcoursRepository parcoursRepository, UtilisateurRepository utilisateurRepository, ChapitreRepository chapitreRepository, ResultatTestRepository resultatTestRepository, EtudiantRepository etudiantRepository) {
        this.parcoursRepository = parcoursRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.chapitreRepository = chapitreRepository;
        this.resultatTestRepository = resultatTestRepository;
        this.etudiantRepository = etudiantRepository;
    }

    public ParcoursDto getParcoursPourEtudiant(Long utilisateurId) {
        Etudiant etudiant = etudiantRepository.findByUtilisateurId(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Aucun profil étudiant trouvé pour l'utilisateur ID: " + utilisateurId));
        Long etudiantId = etudiant.getId();

        List<Parcours> parcoursList = parcoursRepository.findByUtilisateurIdOrderByDateAjoutDesc(utilisateurId);
        List<ParcoursItemDto> recommandes = new ArrayList<>();
        List<ParcoursItemDto> choisis = new ArrayList<>();

        for (Parcours parcours : parcoursList) {
            
            // ====================================================================
            // ===> ADAPTATION À LA NOUVELLE MÉTHODE DU REPOSITORY <===
            // ====================================================================
            // 1. On appelle la nouvelle méthode avec @Query
            List<ResultatTest> resultats = resultatTestRepository.findLatestByEtudiantAndChapitre(etudiantId, parcours.getChapitre().getId());

            double scoreEnPourcentage = 0.0;
            // 2. On prend le premier résultat de la liste (le plus récent)
            if (!resultats.isEmpty()) {
                ResultatTest dernierResultat = resultats.get(0); // Le premier est le plus récent grâce au "ORDER BY DESC"
                if (dernierResultat.getScoreTotal() != null && dernierResultat.getScoreTotal() > 0) {
                    scoreEnPourcentage = (dernierResultat.getScore() / dernierResultat.getScoreTotal()) * 100;
                }
            }
            
            ParcoursItemDto dto = new ParcoursItemDto(
                parcours.getChapitre().getId(),
                parcours.getChapitre().getNom(),
                parcours.getChapitre().getMatiere().getNom(),
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
