package com.moscepa.service;

import com.moscepa.dto.ChapitreSyllabusDto;
import com.moscepa.dto.MatiereSyllabusDto;
import com.moscepa.entity.Chapitre;
import com.moscepa.entity.ElementConstitutif;
import com.moscepa.entity.ResultatTest;
import com.moscepa.entity.Test;
import com.moscepa.repository.ElementConstitutifRepository;
import com.moscepa.repository.ResultatTestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    /**
     * Récupère le syllabus complet d'une matière pour un étudiant.
     */
    @Transactional(readOnly = true)
    public MatiereSyllabusDto getSyllabusPourEtudiant(Long ecId, Long utilisateurId) {
        ElementConstitutif ec = ecRepository.findById(ecId)
            .orElseThrow(() -> new EntityNotFoundException("Matière non trouvée avec l'ID: " + ecId));

        MatiereSyllabusDto matiereDto = new MatiereSyllabusDto();
        matiereDto.setId(ec.getId());
        matiereDto.setNom(ec.getNom());
        matiereDto.setCode(ec.getCode());
        matiereDto.setDescription(ec.getDescription());

        // Construction de la liste des chapitres
        List<ChapitreSyllabusDto> chapitresDto = Optional.ofNullable(ec.getChapitres()).orElseGet(ArrayList::new)
            .stream()
            .map(chapitre -> convertToChapitreSyllabusDto(chapitre, utilisateurId))
            .sorted(Comparator.comparing(ChapitreSyllabusDto::getOrdre, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList());

        matiereDto.setChapitres(chapitresDto);
        return matiereDto;
    }

    /**
     * Convertit un Chapitre en DTO et y injecte le dernier résultat de l'étudiant
     * avec sa catégorie (Échelle de connaissance).
     */
       private ChapitreSyllabusDto convertToChapitreSyllabusDto(Chapitre chapitre, Long utilisateurId) {
        ChapitreSyllabusDto dto = new ChapitreSyllabusDto();
        dto.setId(chapitre.getId());
        dto.setNom(chapitre.getNom());
        dto.setOrdre(chapitre.getOrdre());

        // Correction du typage de la liste pour éviter les erreurs de compilation
        List testsDuChapitre = chapitre.getTests();

        if (testsDuChapitre != null && !testsDuChapitre.isEmpty()) {
            // Le cast (Test) n'est plus nécessaire si la liste est typée au-dessus
            // On utilise le chemin complet de la classe directement dans le cast
com.moscepa.entity.Test premierTest = (com.moscepa.entity.Test) testsDuChapitre.get(0);

            dto.setNomTest(premierTest.getTitre());

            Optional<ResultatTest> dernierResultatOpt = resultatTestRepository
                .findTopByEtudiantIdAndTestIdOrderByDateTestDesc(utilisateurId, premierTest.getId());

            if (dernierResultatOpt.isPresent()) {
                ResultatTest res = dernierResultatOpt.get();
                
                double score = (res.getScore() != null) ? res.getScore() : 0;
                double total = (res.getScoreTotal() != null) ? res.getScoreTotal() : 0;
                dto.setResultatScore(total > 0 ? (score / total) * 100 : 0);

                if (res.getEchelleConnaissance() != null) {
                    dto.setCategorie(res.getEchelleConnaissance().getDescription());
                    dto.setCouleurCategorie(res.getEchelleConnaissance().getCouleur());
                } else {
                    dto.setCategorie("Non classé");
                    dto.setCouleurCategorie("#6c757d");
                }
            } else {
                dto.setResultatScore(null);
                dto.setCategorie(null);
            }
        } else {
            dto.setNomTest("Aucun test défini");
            dto.setResultatScore(null);
            dto.setCategorie(null);
        }
        return dto;
    }

}
