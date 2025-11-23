// Fichier : src/main/java/com/moscepa/service/SyllabusService.java (Version Anti-NullPointerException)

package com.moscepa.service;

import com.moscepa.dto.ChapitreSyllabusDto;
import com.moscepa.dto.MatiereSyllabusDto;
import com.moscepa.entity.Chapitre;
import com.moscepa.entity.ElementConstitutif;
import com.moscepa.entity.ResultatTest;
import com.moscepa.repository.ElementConstitutifRepository;
import com.moscepa.repository.ResultatTestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SyllabusService {

    private final ElementConstitutifRepository ecRepository;
    private final ResultatTestRepository resultatTestRepository;

    @Autowired
    public SyllabusService(ElementConstitutifRepository ecRepository, ResultatTestRepository resultatTestRepository) {
        this.ecRepository = ecRepository;
        this.resultatTestRepository = resultatTestRepository;
    }

    @Transactional(readOnly = true)
    public MatiereSyllabusDto getSyllabusPourEtudiant(Long ecId, Long utilisateurId) {
        
        ElementConstitutif ec = ecRepository.findById(ecId)
            .orElseThrow(() -> new EntityNotFoundException("Matière non trouvée avec l'ID: " + ecId));

        MatiereSyllabusDto matiereDto = new MatiereSyllabusDto();
        matiereDto.setId(ec.getId());
        matiereDto.setNom(ec.getNom());
        matiereDto.setCode(ec.getCode());
        matiereDto.setDescription(ec.getDescription());

        List<ChapitreSyllabusDto> chapitresDto = Optional.ofNullable(ec.getChapitres()).orElseGet(List::of)
            .stream()
            .map(chapitre -> convertToChapitreSyllabusDto(chapitre, utilisateurId))
            .sorted(Comparator.comparing(ChapitreSyllabusDto::getOrdre, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList());

        matiereDto.setChapitres(chapitresDto);
        
        return matiereDto;
    }

    /**
     * Méthode privée corrigée pour gérer le cas où la liste de tests est null.
     */
    private ChapitreSyllabusDto convertToChapitreSyllabusDto(Chapitre chapitre, Long utilisateurId) {
        ChapitreSyllabusDto dto = new ChapitreSyllabusDto();
        dto.setId(chapitre.getId());
        dto.setNom(chapitre.getNom());
        dto.setOrdre(chapitre.getOrdre());

        // ====================================================================
        // === CORRECTION ANTI-NULLPOINTEREXCEPTION                       ===
        // ====================================================================
        // On récupère la liste des tests du chapitre
        List<com.moscepa.entity.Test> testsDuChapitre = chapitre.getTests();

        // On vérifie si la liste n'est pas null ET n'est pas vide
        if (testsDuChapitre != null && !testsDuChapitre.isEmpty()) {
            // On prend le premier test associé au chapitre
            com.moscepa.entity.Test premierTest = testsDuChapitre.get(0);
            Long testId = premierTest.getId();
            dto.setNomTest(premierTest.getTitre());

            // On cherche le dernier résultat de test pour cet étudiant ET ce test précis.
            Optional<ResultatTest> dernierResultatOpt = resultatTestRepository
                .findTopByEtudiantIdAndTestIdOrderByDateTestDesc(utilisateurId, testId);

            if (dernierResultatOpt.isPresent()) {
                ResultatTest dernierResultat = dernierResultatOpt.get();
                double score = dernierResultat.getScore();
                double total = dernierResultat.getScoreTotal();
                double pourcentage = (total > 0) ? (score / total) * 100 : 0;
                dto.setResultatScore(pourcentage);
            } else {
                dto.setResultatScore(null);
            }
        } else {
            // S'il n'y a pas de test, le nom est "Non défini" et le score est null.
            dto.setNomTest("Non défini");
            dto.setResultatScore(null);
        }

        dto.setCategorie("Concepts Clés");

        return dto;
    }
}
