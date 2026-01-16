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
import java.util.LinkedHashMap;
import java.util.List;

import java.util.Optional;
import java.util.Map;


@Service
@Transactional(readOnly = true)
public class ParcoursService {

    private final ParcoursRepository parcoursRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ChapitreRepository chapitreRepository;
    private final ResultatTestRepository resultatTestRepository;

    public ParcoursService(ParcoursRepository parcoursRepository, 
                           UtilisateurRepository utilisateurRepository, 
                           ChapitreRepository chapitreRepository, 
                           ResultatTestRepository resultatTestRepository) {
        this.parcoursRepository = parcoursRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.chapitreRepository = chapitreRepository;
        this.resultatTestRepository = resultatTestRepository;
    }

    /**
     * Récupère les parcours filtrés selon la logique 2026 : 
     * Recommandés (<=33%) et Choisis (>33% et <=66%)
     */
   public ParcoursDto getParcoursPourEtudiant(Long utilisateurId) {
    if (!utilisateurRepository.existsById(utilisateurId)) {
        throw new EntityNotFoundException("Étudiant non trouvé");
    }

    List<Parcours> parcoursList = parcoursRepository.findByUtilisateurIdOrderByDateAjoutDesc(utilisateurId);
    
    // Utilisation de Maps pour éviter TOUT doublon par ID de chapitre
    Map<Long, ParcoursItemDto> recommandesMap = new LinkedHashMap<>();
    Map<Long, ParcoursItemDto> choisisMap = new LinkedHashMap<>();

    for (Parcours parcours : parcoursList) {
        Long capId = parcours.getChapitre().getId();
        
        // On récupère le score
        List<ResultatTest> resultats = resultatTestRepository.findLatestByEtudiantAndChapitre(utilisateurId, capId);
        double score = 0.0;
        if (!resultats.isEmpty() && resultats.get(0).getScoreTotal() > 0) {
            score = (resultats.get(0).getScore() / resultats.get(0).getScoreTotal()) * 100;
        }

        ParcoursItemDto dto = new ParcoursItemDto(
            capId,
            parcours.getChapitre().getNom(),
            parcours.getChapitre().getElementConstitutif().getNom(),
            score
        );

        // LOGIQUE DE RÉPARTITION UNIQUE
        if (score <= 33.0) {
            recommandesMap.put(capId, dto);
        } else if (score > 33.0 && score <= 66.0) {
            choisisMap.put(capId, dto);
        }
    }

    // Conversion des Maps en Listes
    List<ParcoursItemDto> recommandes = new ArrayList<>(recommandesMap.values());
    List<ParcoursItemDto> choisis = new ArrayList<>(choisisMap.values());

    // Création de la liste mixte (Somme unique)
    Map<Long, ParcoursItemDto> mixtesMap = new LinkedHashMap<>();
    mixtesMap.putAll(recommandesMap);
    mixtesMap.putAll(choisisMap);
    List<ParcoursItemDto> mixtes = new ArrayList<>(mixtesMap.values());

    ParcoursDto resultatDto = new ParcoursDto();
    resultatDto.setRecommandes(recommandes);
    resultatDto.setChoisis(choisis);
    resultatDto.setMixtes(mixtes);
    
    return resultatDto;
}



    /**
     * Enregistrement manuel par l'étudiant (Tranche 33% - 66%)
     */
    @Transactional
    public void enregistrerChoixEtudiant(Long utilisateurId, List<Long> chapitreIds) {
        processEnregistrement(utilisateurId, chapitreIds, Parcours.TypeParcours.CHOISI);
    }

    /**
     * Enregistrement automatique par le système (Tranche 0% - 33%)
     */
    @Transactional
    public void enregistrerParcoursRecommande(Long utilisateurId, List<Long> chapitreIds) {
        processEnregistrement(utilisateurId, chapitreIds, Parcours.TypeParcours.RECOMMANDE);
    }

    /**
     * Logique de persistance mutualisée avec vérification d'existence
     */
 private void processEnregistrement(Long utilisateurId, List<Long> chapitreIds, Parcours.TypeParcours type) {
    if (chapitreIds == null || chapitreIds.isEmpty()) return;

    Utilisateur etudiant = utilisateurRepository.findById(utilisateurId)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
    
    List<Chapitre> chapitres = chapitreRepository.findAllById(chapitreIds);
    
    for (Chapitre chapitre : chapitres) {
        // CORRECTION ICI : on utilise chapitre.getId() pour l'id du chapitre en cours de boucle
        Optional<Parcours> parcoursExistant = parcoursRepository.findFirstByUtilisateurIdAndChapitreId(utilisateurId, chapitre.getId());

        if (parcoursExistant.isPresent()) {
            Parcours p = parcoursExistant.get();
            p.setType(type);
            p.setDateAjout(LocalDateTime.now());
            parcoursRepository.save(p);
        } else {
            Parcours nouveau = new Parcours();
            nouveau.setUtilisateur(etudiant);
            nouveau.setChapitre(chapitre);
            nouveau.setType(type);
            nouveau.setDateAjout(LocalDateTime.now());
            parcoursRepository.save(nouveau);
        }
    }
}


}
