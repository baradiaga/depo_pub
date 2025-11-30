package com.moscepa.service;

import com.moscepa.dto.MatiereStatutDto;
import com.moscepa.entity.ElementConstitutif;
import com.moscepa.entity.ResultatTest;
import com.moscepa.repository.ElementConstitutifRepository;
import com.moscepa.repository.ResultatTestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProgressionService {

    private final ElementConstitutifRepository elementConstitutifRepository;
    private final ResultatTestRepository resultatTestRepository;

    public ProgressionService(ElementConstitutifRepository elementConstitutifRepository,
                              ResultatTestRepository resultatTestRepository) {
        this.elementConstitutifRepository = elementConstitutifRepository;
        this.resultatTestRepository = resultatTestRepository;
    }

    /**
     * Récupère la liste des matières d'un étudiant avec le statut de connaissance basé sur les tests.
     */
    @Transactional(readOnly = true)
    public List<MatiereStatutDto> findMatieresByEtudiant(Long etudiantId) {

        List<ElementConstitutif> matieres = elementConstitutifRepository.findMatieresByEtudiantIdSqlNatif(etudiantId);

        return matieres.stream().map(matiere -> {

            // Récupérer tous les tests de cette matière pour l'étudiant
            List<ResultatTest> resultats = resultatTestRepository.findByEtudiantIdAndMatiereId(etudiantId, matiere.getId());

            double totalScore = resultats.stream().mapToDouble(ResultatTest::getScore).sum();
            int nbTests = resultats.size();

            double moyenne = nbTests > 0 ? totalScore / nbTests : 0.0;

            // Déterminer le statut selon ton échelle
            String statut;
            if (moyenne <= 33) statut = "Débutant";
            else if (moyenne <= 66) statut = "Intermédiaire";
            else statut = "Maîtrise";

            return new MatiereStatutDto(
                    matiere.getId(),
                    matiere.getNom(),
                    moyenne,
                    matiere.getCode(),
                    matiere.getCredit(),
                    statut
            );

        }).collect(Collectors.toList());
    }
}
