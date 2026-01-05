package com.moscepa.service;

import com.moscepa.dto.*;
import com.moscepa.entity.*;
import com.moscepa.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StudentJourneyService {

    private final UtilisateurRepository utilisateurRepository;
    private final ResultatTestRepository resultatTestRepository;
    private final FrontendMapperService mapperService;

    public StudentJourneyService(UtilisateurRepository utilisateurRepository,
                                 ResultatTestRepository resultatTestRepository,
                                 FrontendMapperService mapperService) {
        this.utilisateurRepository = utilisateurRepository;
        this.resultatTestRepository = resultatTestRepository;
        this.mapperService = mapperService;
    }

    /**
     * DÉFINIR LE PARCOURS : Méthode à appeler après un test de diagnostic
     */
    @Transactional
    public void definirTypeParcours(Long etudiantId, String nouveauType) {
        Utilisateur etudiant = utilisateurRepository.findById(etudiantId)
                .orElseThrow(() -> new EntityNotFoundException("Étudiant non trouvé"));
        
        // On définit le type (ex: RECOMMANDE, CHOISI, MIXTE)
        etudiant.setParcoursType(nouveauType);
        utilisateurRepository.save(etudiant);
    }

    /**
     * RÉCUPÉRER LE PARCOURS (Détail pour l'Admin et l'Étudiant)
     */
    public StudentJourneyDto getStudentJourney(Long etudiantId) {
        Utilisateur etudiant = utilisateurRepository.findById(etudiantId)
                .orElseThrow(() -> new EntityNotFoundException("Étudiant non trouvé"));

        // Utilisation de la requête optimisée FETCH JOIN pour éviter le score 0
        List<ResultatTest> allResults = resultatTestRepository.findFullResultsByEtudiant(etudiantId);

        StudentJourneyDto journey = new StudentJourneyDto();
        journey.setStudentId(etudiant.getId());
        journey.setNomComplet(etudiant.getPrenom() + " " + etudiant.getNom());
        journey.setParcoursType(etudiant.getParcoursType() != null ? etudiant.getParcoursType() : "NON DEFINI");

        // Groupement par chapitre pour l'historique des notes
        Map<Long, List<ResultatTest>> resultsByChapitre = allResults.stream()
                .collect(Collectors.groupingBy(r -> r.getTest().getChapitre().getId()));

        List<CourseProgressDto> progression = resultsByChapitre.entrySet().stream()
                .map(entry -> {
                    List<ResultatTest> tests = entry.getValue();
                    var chapitre = tests.get(0).getTest().getChapitre();

                    // HISTORIQUE : On récupère chaque note individuellement
                    List<Double> notes = tests.stream()
                            .map(ResultatTest::getScore)
                            .collect(Collectors.toList());

                    CourseProgressDto dto = new CourseProgressDto();
                    dto.setCourseId(chapitre.getId());
                    dto.setCourseName(chapitre.getNom());
                    dto.setNotesDetaillees(notes); // Liste des notes au lieu de la moyenne
                    dto.setTestsPasses(tests.size());

                    // Statut basé sur la DERNIÈRE note (index 0 car trié par date DESC)
                    double derniereNote = notes.get(0);
                    if (derniereNote < 33) dto.setStatutRecommandation("Faible");
                    else if (derniereNote < 66) dto.setStatutRecommandation("Moyen");
                    else dto.setStatutRecommandation("Bonne maîtrise");

                    return dto;
                }).collect(Collectors.toList());

        journey.setProgressionParCours(progression);
        return journey;
    }

    /**
     * VÉRIFIER LE PARCOURS (Vue globale Admin)
     */
    public List<StudentJourneyDto> getAllJourneys(String type) {
        List<Utilisateur> etudiants = utilisateurRepository.findByRole(Role.ETUDIANT);

        return etudiants.stream()
                .map(etudiant -> getStudentJourney(etudiant.getId()))
                .filter(dto -> {
                    // Si type est "ALL", on voit tout (y compris "NON DEFINI")
                    if (type == null || type.isEmpty() || type.equalsIgnoreCase("ALL")) return true;
                    return dto.getParcoursType().equalsIgnoreCase(type);
                })
                .collect(Collectors.toList());
    }

    // Méthodes de pont Frontend
    public StudentJourneyFrontDto getStudentJourneyForFrontend(Long etudiantId) {
        return mapperService.toFrontDto(getStudentJourney(etudiantId));
    }

    public List<StudentJourneyFrontDto> getAllJourneysForFrontend(String type) {
        return mapperService.toFrontDtoList(getAllJourneys(type));
    }
    /**
 * Récupère les parcours des étudiants inscrits aux formations de l'enseignant.
 */
public List<StudentJourneyDto> getStudentsForTeacher(Long teacherId) {
    // 1. On récupère l'enseignant et ses formations
    Utilisateur enseignant = utilisateurRepository.findById(teacherId)
            .orElseThrow(() -> new EntityNotFoundException("Enseignant non trouvé."));

    List<Formation> formations = enseignant.getFormationsCrees();

    // 2. On récupère les étudiants de ces formations (Rôle ETUDIANT)
    List<Utilisateur> etudiants = formations.stream()
            .flatMap(f -> utilisateurRepository.findAllByFormationActuelle(f.getNom()).stream()
                    .filter(u -> u.getRoles().stream().anyMatch(r -> r.equals("ETUDIANT")))
            )
            .distinct()
            .collect(Collectors.toList());

    // 3. On génère le DTO détaillé (avec historique des notes) pour chaque étudiant
    return etudiants.stream()
            .map(etudiant -> getStudentJourney(etudiant.getId()))
            .collect(Collectors.toList());
}

/**
 * Version Frontend pour l'enseignant
 */
public List<StudentJourneyFrontDto> getStudentsForTeacherForFrontend(Long teacherId) {
    List<StudentJourneyDto> backendList = getStudentsForTeacher(teacherId);
    return mapperService.toFrontDtoList(backendList);
}

}
