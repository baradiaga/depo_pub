package com.moscepa.service;

import com.moscepa.dto.*;
import com.moscepa.entity.*;
import com.moscepa.repository.*;
import com.moscepa.security.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TestService {

    private static final Logger log = LoggerFactory.getLogger(TestService.class);

    // Dépendances
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final ParcoursService parcoursService;
    private final ResultatTestRepository resultatTestRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ChapitreRepository chapitreRepository;
    private final QuestionnaireRepository questionnaireRepository; // <-- ajouté

    // Constructeur (toutes les dépendances)
    public TestService(TestRepository testRepository,
                       QuestionRepository questionRepository,
                       ParcoursService parcoursService,
                       ResultatTestRepository resultatTestRepository,
                       UtilisateurRepository utilisateurRepository,
                       ChapitreRepository chapitreRepository,
                       QuestionnaireRepository questionnaireRepository) {
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.parcoursService = parcoursService;
        this.resultatTestRepository = resultatTestRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.chapitreRepository = chapitreRepository;
        this.questionnaireRepository = questionnaireRepository;
    }

    @Transactional
    public Test creerTestAvecQuestions(Long chapitreId, String titre, List<Long> questionIds) {
        Chapitre chapitre = chapitreRepository.findById(chapitreId)
                .orElseThrow(() -> new EntityNotFoundException("Chapitre non trouvé avec l'ID: " + chapitreId));

        List<Question> questionsAAssocier = questionRepository.findAllById(questionIds);
        if (questionsAAssocier.size() != questionIds.size()) {
            throw new EntityNotFoundException("Une ou plusieurs questions n'ont pas été trouvées.");
        }

        Test nouveauTest = new Test();
        nouveauTest.setTitre(titre);
        nouveauTest.setChapitre(chapitre);
        nouveauTest.setQuestions(questionsAAssocier);

        Test testSauvegarde = testRepository.save(nouveauTest);
        log.info("Test '{}' créé avec {} questions pour le chapitre '{}'.", testSauvegarde.getTitre(), testSauvegarde.getQuestions().size(), chapitre.getNom());

        return testSauvegarde;
    }

    public List<QuestionDto> getQuestionsPourChapitre(Long chapitreId) {
        Test test = testRepository.findTopByChapitreId(chapitreId)
                .orElseThrow(() -> new EntityNotFoundException("Aucun test trouvé pour le chapitre ID: " + chapitreId));
        return test.getQuestions().stream().map(QuestionDto::new).collect(Collectors.toList());
    }
@Transactional
public Test creerTestDepuisQuestionnaire(Long questionnaireId) {
    Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId)
            .orElseThrow(() -> new EntityNotFoundException("Questionnaire non trouvé avec l'ID: " + questionnaireId));

    if (questionnaire.getChapitre() == null) {
        throw new IllegalStateException("Impossible de créer un test : le questionnaire n'a pas de chapitre.");
    }

    if (questionnaire.getQuestions().isEmpty()) {
        throw new EntityNotFoundException("Le questionnaire ne contient aucune question.");
    }

    Test test = new Test();
    test.setTitre("Test pour " + questionnaire.getTitre());
    test.setChapitre(questionnaire.getChapitre()); // ✅ garanti non-null
    test.setQuestionnaire(questionnaire);
    test.setQuestions(new ArrayList<>(questionnaire.getQuestions()));

    return testRepository.save(test);
}


    @Transactional
    public ResultatTestDto calculerEtSauvegarderResultat(Long chapitreId, Long utilisateurId, Map<String, Object> reponsesUtilisateur) {
        Utilisateur etudiant = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Aucun utilisateur (étudiant) trouvé pour l'ID: " + utilisateurId));

        Test test = testRepository.findTopByChapitreId(chapitreId)
                .orElseThrow(() -> new EntityNotFoundException("Aucun test trouvé pour le chapitre ID: " + chapitreId));

        List<Question> questionsDuTest = test.getQuestions();
        if (questionsDuTest == null || questionsDuTest.isEmpty()) {
            throw new EntityNotFoundException("Aucune question trouvée pour ce test.");
        }

        double scoreObtenu = 0.0;
        double totalPointsPossible = 0.0;
        int nombreDeBonnesReponses = 0;

        for (Question question : questionsDuTest) {
            totalPointsPossible += question.getPoints();
            String questionIdStr = String.valueOf(question.getId());
            Object reponseDonnee = reponsesUtilisateur.get(questionIdStr);

            if (verifierReponse(question, reponseDonnee)) {
                scoreObtenu += question.getPoints();
                nombreDeBonnesReponses++;
            }
        }

        ResultatTest nouveauResultat = new ResultatTest();
        nouveauResultat.setEtudiant(etudiant);
        nouveauResultat.setTest(test);
        nouveauResultat.setScore(scoreObtenu);
        nouveauResultat.setScoreTotal(totalPointsPossible);
        nouveauResultat.setDateTest(LocalDateTime.now());
        nouveauResultat.setBonnesReponses(nombreDeBonnesReponses);
        nouveauResultat.setTotalQuestions(questionsDuTest.size());

        resultatTestRepository.save(nouveauResultat);
        log.info("Résultat du test sauvegardé pour l'étudiant ID {}. Score: {}/{}", etudiant.getId(), scoreObtenu, totalPointsPossible);

        double pourcentage = (totalPointsPossible > 0) ? (scoreObtenu / totalPointsPossible) * 100 : 0;
        if (pourcentage < 33.0) {
            log.info("Score < 33%. Enregistrement du parcours recommandé pour l'étudiant ID {}.", utilisateurId);
            parcoursService.enregistrerParcoursRecommande(utilisateurId, List.of(chapitreId));
        }

        ResultatTestDto resultatDto = new ResultatTestDto();
        resultatDto.setChapitreId(chapitreId);
        resultatDto.setScoreObtenu(scoreObtenu);
        resultatDto.setTotalPointsPossible(totalPointsPossible);
        resultatDto.setDateSoumission(LocalDateTime.now());
        return resultatDto;
    }

    public List<HistoriqueResultatDto> getHistoriquePourEtudiant(Long utilisateurId) {
        List<ResultatTest> resultats = resultatTestRepository.findByEtudiantIdOrderByDateTestDesc(utilisateurId);
        return resultats.stream()
                .map(HistoriqueResultatDto::new)
                .collect(Collectors.toList());
    }

    public List<QuestionDto> getQuestionsPourTestDeConnaissance(Long matiereId) {
        log.info("Construction du test de connaissance pour la matière ID: {}", matiereId);
        List<Question> toutesLesQuestions = questionRepository.findByQuestionnaireChapitreElementConstitutifId(matiereId);
        if (toutesLesQuestions.isEmpty()) {
            log.warn("Aucune question trouvée pour la matière ID: {}. Le test sera vide.", matiereId);
            return Collections.emptyList();
        }
        final int NOMBRE_QUESTIONS_TEST = 20;
        return toutesLesQuestions.stream()
                .limit(NOMBRE_QUESTIONS_TEST)
                .map(QuestionDto::new)
                .collect(Collectors.toList());
    }

    private static final int NOMBRE_QUESTIONS_DIAGNOSTIC = 10;

    public List<QuestionDiagnosticDto> genererTestDiagnosticPourMatiere(Long matiereId) {
        log.info("[DIAGNOSTIC] Génération d'un test de diagnostic pour la matière ID: {}", matiereId);
        List<Question> toutesLesQuestions = questionRepository.findQuestionsByMatiereId(matiereId);
        log.info("[DIAGNOSTIC] {} questions trouvées au total pour cette matière.", toutesLesQuestions.size());
        if (toutesLesQuestions.isEmpty()) {
            return Collections.emptyList();
        }
        List<Question> questionsSelectionnees = toutesLesQuestions.stream()
                .limit(NOMBRE_QUESTIONS_DIAGNOSTIC)
                .collect(Collectors.toList());
        log.info("[DIAGNOSTIC] {} questions sélectionnées pour le test.", questionsSelectionnees.size());
        return questionsSelectionnees.stream()
                .map(this::convertirEnQuestionDiagnosticDto)
                .collect(Collectors.toList());
    }

    private QuestionDiagnosticDto convertirEnQuestionDiagnosticDto(Question question) {
        QuestionDiagnosticDto dto = new QuestionDiagnosticDto();
        dto.setId(question.getId());
        dto.setEnonce(question.getEnonce());
        dto.setTypeQuestion(question.getTypeQuestion());
        if (question.getChapitre() != null) {
            dto.setChapitreId(question.getChapitre().getId());
        }
        List<ReponsePourQuestionDto> optionsDto = question.getReponses().stream()
                .map(ReponsePourQuestionDto::new)
                .collect(Collectors.toList());
        dto.setOptions(optionsDto);
        return dto;
    }

    private boolean verifierReponse(Question question, Object reponseDonnee) {
        if (reponseDonnee == null) return false;

        try {
            switch (question.getTypeQuestion()) {
                case QCU:
                    String reponseQCUStr = reponseDonnee.toString();
                    return question.getReponses().stream()
                            .filter(Reponse::isCorrecte)
                            .anyMatch(bonneReponse -> bonneReponse.getId().toString().equals(reponseQCUStr.trim()));

                case VRAI_FAUX:
                case TEXTE_LIBRE:
                    String expected = question.getReponseCorrecteTexte();
                    if (expected == null) return false;
                    return reponseDonnee.toString().trim().equalsIgnoreCase(expected.trim());

                case QCM:
                    if (!(reponseDonnee instanceof List)) return false;
                    Set<String> reponsesSoumisesIds = ((List<?>) reponseDonnee).stream()
                            .map(Object::toString).collect(Collectors.toSet());
                    Set<String> bonnesReponsesIds = question.getReponses().stream()
                            .filter(Reponse::isCorrecte).map(r -> r.getId().toString()).collect(Collectors.toSet());
                    return reponsesSoumisesIds.equals(bonnesReponsesIds);

                default:
                    return false;
            }
        } catch (Exception e) {
            log.error("Erreur de vérification pour la question ID {}: {}", question.getId(), e.getMessage());
            return false;
        }
    }

    @Transactional
    public ResultatDiagnosticDto corrigerTestDiagnostic(SoumissionTestDto soumission) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal)) {
            throw new EntityNotFoundException("Utilisateur non authentifié ou principal invalide.");
        }
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();

        Utilisateur etudiant = utilisateurRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new EntityNotFoundException("Étudiant non trouvé pour la correction."));

        List<Long> questionIds = soumission.getReponses().stream()
                .map(ReponseSoumiseDto::getQuestionId)
                .collect(Collectors.toList());

        Map<Long, Question> questionsMap = questionRepository.findAllById(questionIds).stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        Map<Chapitre, List<Boolean>> resultatsParChapitre = new HashMap<>();
        int totalBonnesReponses = 0;

        for (ReponseSoumiseDto reponseSoumise : soumission.getReponses()) {
            Question question = questionsMap.get(reponseSoumise.getQuestionId());
            if (question == null || question.getChapitre() == null) continue;

            boolean estCorrect = verifierReponse(question, reponseSoumise.getReponse());
            if (estCorrect) {
                totalBonnesReponses++;
            }
            resultatsParChapitre.computeIfAbsent(question.getChapitre(), k -> new ArrayList<>()).add(estCorrect);
        }

        ResultatDiagnosticDto resultatFinal = new ResultatDiagnosticDto();
        resultatFinal.setTotalQuestions(questionsMap.size());
        resultatFinal.setBonnesReponses(totalBonnesReponses);
        resultatFinal.setScoreGlobal(calculatePercentage(totalBonnesReponses, questionsMap.size()));

        Map<String, Double> scoreParChapitreMap = new HashMap<>();
        List<ChapitreRecommandationDto> chapitresAReviser = new ArrayList<>();

        for (Map.Entry<Chapitre, List<Boolean>> entry : resultatsParChapitre.entrySet()) {
            Chapitre chapitre = entry.getKey();
            List<Boolean> resultats = entry.getValue();
            long bonnesReponsesChapitre = resultats.stream().filter(b -> b).count();
            double scoreChapitre = calculatePercentage(bonnesReponsesChapitre, resultats.size());

            scoreParChapitreMap.put(chapitre.getNom(), scoreChapitre);

            if (scoreChapitre < 50.0) {
                chapitresAReviser.add(new ChapitreRecommandationDto(chapitre.getId(), chapitre.getNom(), scoreChapitre));
            }
        }

        resultatFinal.setScoreParChapitre(scoreParChapitreMap);
        resultatFinal.setChapitresAReviser(chapitresAReviser);
        resultatFinal.setMessage("Analyse de votre niveau terminée !");

        log.info("Analyse de diagnostic pour l'étudiant {}: {}/{}", etudiant.getEmail(), totalBonnesReponses, questionsMap.size());

        return resultatFinal;
    }

    private double calculatePercentage(long num, long den) {
        if (den == 0) return 0.0;
        return ((double) num / den) * 100.0;
    }
}
