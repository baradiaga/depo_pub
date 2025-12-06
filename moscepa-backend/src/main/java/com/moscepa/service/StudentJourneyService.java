package com.moscepa.service;

import com.moscepa.dto.CourseProgressDto;
import com.moscepa.dto.StudentJourneyDto;
import com.moscepa.entity.ResultatTest;
import com.moscepa.entity.Test;
import com.moscepa.entity.Utilisateur;
import com.moscepa.entity.Role;
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
                .orElseThrow(() -> new EntityNotFoundException(
                        "Étudiant non trouvé avec l'ID: " + etudiantId));

        StudentJourneyDto journey = new StudentJourneyDto();
        journey.setStudentId(etudiant.getId());
        journey.setNomComplet(etudiant.getPrenom() + " " + etudiant.getNom());
        journey.setEmail(etudiant.getEmail());
        journey.setFormationActuelle(etudiant.getFormationActuelle());
        journey.setNiveauEtude(etudiant.getNiveauEtude());
        journey.setParcoursType(etudiant.getParcoursType());

        // Calcul de la progression par cours
        List<ResultatTest> allResults =
                resultatTestRepository.findByEtudiantIdOrderByDateTestDesc(etudiantId);

        List<CourseProgressDto> progression = new ArrayList<>();

        allResults.stream()
                .map(ResultatTest::getTest)
                .map(Test::getChapitre)
                .distinct()
                .forEach(chapitre -> {
                    List<ResultatTest> testsChapitre =
                            resultatTestRepository.findLatestByEtudiantAndChapitre(etudiantId, chapitre.getId());

                    double moyenne = testsChapitre.stream()
                            .mapToDouble(ResultatTest::getScore)
                            .average()
                            .orElse(0);

                    CourseProgressDto dto = new CourseProgressDto();
                    dto.setCourseId(chapitre.getId());
                    dto.setCourseCode(chapitre.getElementConstitutif().getCode());
                    dto.setCourseName(chapitre.getNom());
                    dto.setScoreMoyen(moyenne);
                    dto.setTestsPasses(testsChapitre.size());

                    if (moyenne < 33) dto.setStatutRecommandation("Faible");
                    else if (moyenne < 66) dto.setStatutRecommandation("Moyen");
                    else dto.setStatutRecommandation("Bonne maîtrise");

                    progression.add(dto);
                });

        journey.setProgressionParCours(progression);

        // Moyenne générale
        double totalScore = allResults.stream().mapToDouble(ResultatTest::getScore).sum();
        int nbTests = allResults.size();
        journey.setMoyenneGeneraleTests(nbTests > 0 ? totalScore / nbTests : 0);
        journey.setTestsPasses(nbTests);

        return journey;
    }

    /**
     * Récupère tous les parcours étudiants (admin) avec filtrage optionnel par type.
     */
    public List<StudentJourneyDto> getAllJourneys(String type) {
        List<Utilisateur> etudiants = utilisateurRepository.findByRole(Role.ETUDIANT);
        List<StudentJourneyDto> journeys = new ArrayList<>();

        for (Utilisateur etudiant : etudiants) {
            StudentJourneyDto dto = getStudentJourney(etudiant.getId());
            if (type == null || type.isEmpty() || dto.getParcoursType().equalsIgnoreCase(type)) {
                journeys.add(dto);
            }
        }

        return journeys;
    }
}
