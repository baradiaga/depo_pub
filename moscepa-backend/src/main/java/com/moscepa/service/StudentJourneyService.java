package com.moscepa.service;

import com.moscepa.dto.CourseProgressDto;
import com.moscepa.dto.StudentJourneyDto;
import com.moscepa.entity.*;
import com.moscepa.repository.ResultatTestRepository;
import com.moscepa.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class StudentJourneyService {

    private final UtilisateurRepository utilisateurRepository;
    private final ResultatTestRepository resultatTestRepository;

    public StudentJourneyService(UtilisateurRepository utilisateurRepository,
                                 ResultatTestRepository resultatTestRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.resultatTestRepository = resultatTestRepository;
    }

    /**
     * Récupère le parcours détaillé d'un étudiant.
     */
    public StudentJourneyDto getStudentJourney(Long etudiantId) {
        Utilisateur etudiant = utilisateurRepository.findById(etudiantId)
                .orElseThrow(() -> new EntityNotFoundException("Étudiant non trouvé avec l'ID: " + etudiantId));

        StudentJourneyDto journey = new StudentJourneyDto();
        journey.setStudentId(etudiant.getId());
        journey.setNomComplet(etudiant.getPrenom() + " " + etudiant.getNom());
        journey.setEmail(etudiant.getEmail());
        journey.setFormationActuelle("Licence Informatique"); // À adapter selon ta logique
        journey.setNiveauEtude("L3"); // À adapter
        journey.setParcoursType("Recommandé"); // Simulation pour l'instant

        // ----------------------
        // Calcul de la progression par chapitre
        // ----------------------
        List<ResultatTest> allResults = resultatTestRepository.findByEtudiantIdOrderByDateTestDesc(etudiantId);
        List<CourseProgressDto> progression = new ArrayList<>();

        // On regroupe les résultats par chapitre
        allResults.stream()
                .map(ResultatTest::getTest)
                .map(Test::getChapitre)
                .distinct()
                .forEach(chapitre -> {
                    List<ResultatTest> testsChapitre = resultatTestRepository.findLatestByEtudiantAndChapitre(etudiantId, chapitre.getId());
                    double moyenne = testsChapitre.stream()
                            .mapToDouble(ResultatTest::getScore)
                            .average()
                            .orElse(0);
                    int testsPasses = testsChapitre.size();

                    CourseProgressDto dto = new CourseProgressDto();
                    dto.setCourseId(chapitre.getId());
                    dto.setCourseCode(chapitre.getElementConstitutif().getCode());
                    dto.setCourseName(chapitre.getNom());
                    dto.setScoreMoyen(moyenne);
                    dto.setTestsPasses(testsPasses);

                    // Statut selon l'échelle de connaissance
                    if (moyenne < 33) dto.setStatutRecommandation("Faible");
                    else if (moyenne < 66) dto.setStatutRecommandation("Moyen");
                    else dto.setStatutRecommandation("Bonne maîtrise");

                    progression.add(dto);
                });

        journey.setProgressionParCours(progression);

        // Calcul de la moyenne générale et nombre de tests
        double totalScore = allResults.stream().mapToDouble(ResultatTest::getScore).sum();
        int nbTests = allResults.size();
        journey.setMoyenneGeneraleTests(nbTests > 0 ? totalScore / nbTests : 0);
        journey.setTestsPasses(nbTests);

        return journey;
    }
}
