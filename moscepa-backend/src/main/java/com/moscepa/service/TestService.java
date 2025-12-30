package com.moscepa.service;

import com.moscepa.dto.*;
import com.moscepa.entity.*;
import com.moscepa.repository.*;
import com.moscepa.security.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
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

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final ResultatTestRepository resultatTestRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ChapitreRepository chapitreRepository;
    private final QuestionnaireRepository questionnaireRepository;

    public TestService(TestRepository testRepository,
                       QuestionRepository questionRepository,
                       ResultatTestRepository resultatTestRepository,
                       UtilisateurRepository utilisateurRepository,
                       ChapitreRepository chapitreRepository,
                       QuestionnaireRepository questionnaireRepository) {
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.resultatTestRepository = resultatTestRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.chapitreRepository = chapitreRepository;
        this.questionnaireRepository = questionnaireRepository;
    }

    // ===========================================
    // Tests classiques
    // ===========================================

    @Transactional
    public Test creerTestAvecQuestions(Long chapitreId, String titre, List<Long> questionIds) {
        Chapitre chapitre = chapitreRepository.findById(chapitreId)
                .orElseThrow(() -> new EntityNotFoundException("Chapitre non trouvé avec l'ID: " + chapitreId));

        List<Question> questions = questionRepository.findAllById(questionIds);
        if (questions.size() != questionIds.size()) {
            throw new EntityNotFoundException("Une ou plusieurs questions n'ont pas été trouvées.");
        }

        Test test = new Test();
        test.setTitre(titre);
        test.setChapitre(chapitre);
        test.setQuestions(questions);

        return testRepository.save(test);
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
        test.setChapitre(questionnaire.getChapitre());
        test.setQuestionnaire(questionnaire);
        test.setQuestions(new ArrayList<>(questionnaire.getQuestions()));

        questionnaire.addTest(test);

        return testRepository.save(test);
    }

    public List<QuestionDto> getQuestionsPourChapitre(Long chapitreId) {
        Test test = testRepository.findTopByChapitreId(chapitreId)
                .orElseThrow(() -> new EntityNotFoundException("Aucun test trouvé pour le chapitre ID: " + chapitreId));
        return test.getQuestions().stream().map(QuestionDto::new).collect(Collectors.toList());
    }

    @Transactional
    public ResultatTestDto calculerEtSauvegarderResultat(Long chapitreId, Long utilisateurId, Map<String, Object> reponsesUtilisateur) {
        Utilisateur etudiant = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + utilisateurId));

        Test test = testRepository.findTopByChapitreId(chapitreId)
                .orElseThrow(() -> new EntityNotFoundException("Aucun test trouvé pour le chapitre ID: " + chapitreId));

        double scoreObtenu = 0;
        double totalPoints = 0;
        int bonnesReponses = 0;

        for (Question question : test.getQuestions()) {
            totalPoints += question.getPoints();
            Object reponse = reponsesUtilisateur.get(String.valueOf(question.getId()));
            if (verifierReponse(question, reponse)) {
                scoreObtenu += question.getPoints();
                bonnesReponses++;
            }
        }

        ResultatTest resultat = new ResultatTest();
        resultat.setEtudiant(etudiant);
        resultat.setTest(test);
        resultat.setScore(scoreObtenu);
        resultat.setScoreTotal(totalPoints);
        resultat.setBonnesReponses(bonnesReponses);
        resultat.setTotalQuestions(test.getQuestions().size());
        resultat.setDateTest(LocalDateTime.now());

        resultatTestRepository.save(resultat);

        ResultatTestDto dto = new ResultatTestDto();
        dto.setChapitreId(chapitreId);
        dto.setScoreObtenu(scoreObtenu);
        dto.setTotalPointsPossible(totalPoints);
        dto.setDateSoumission(LocalDateTime.now());

        return dto;
    }

    public List<HistoriqueResultatDto> getHistoriquePourEtudiant(Long utilisateurId) {
        List<ResultatTest> resultats = resultatTestRepository.findByEtudiantIdOrderByDateTestDesc(utilisateurId);
        return resultats.stream().map(HistoriqueResultatDto::new).collect(Collectors.toList());
    }

    private boolean verifierReponse(Question question, Object reponseDonnee) {
        if (reponseDonnee == null) return false;

        switch (question.getTypeQuestion()) {
            case QCU:
                return question.getReponses().stream()
                        .filter(Reponse::isCorrecte)
                        .anyMatch(r -> r.getId().toString().equals(reponseDonnee.toString()));
            case VRAI_FAUX:
                return question.getReponseCorrecteTexte() != null &&
                        question.getReponseCorrecteTexte().equalsIgnoreCase(reponseDonnee.toString());
            case QCM:
                if (!(reponseDonnee instanceof List)) return false;
                Set<String> soumises = ((List<?>) reponseDonnee).stream().map(Object::toString).collect(Collectors.toSet());
                Set<String> correctes = question.getReponses().stream()
                        .filter(Reponse::isCorrecte)
                        .map(r -> r.getId().toString())
                        .collect(Collectors.toSet());
                return soumises.equals(correctes);
            case TEXTE_LIBRE:
                return question.getReponseCorrecteTexte() != null &&
                        question.getReponseCorrecteTexte().equalsIgnoreCase(reponseDonnee.toString());
            default:
                return false;
        }
    }

    // ===========================================
    // Tests diagnostics
    // ===========================================
    public List<QuestionDiagnosticDto> genererTestDiagnosticPourMatiere(Long matiereId) {
        List<Question> toutesLesQuestions = questionRepository.findQuestionsByMatiereId(matiereId);
        final int NOMBRE_QUESTIONS_DIAGNOSTIC = 3;
        List<Question> selectionnees = toutesLesQuestions.stream()
                .limit(NOMBRE_QUESTIONS_DIAGNOSTIC)
                .collect(Collectors.toList());

        return selectionnees.stream()
                .map(q -> {
                    QuestionDiagnosticDto dto = new QuestionDiagnosticDto();
                    dto.setId(q.getId());
                    dto.setEnonce(q.getEnonce());
                    dto.setTypeQuestion(q.getTypeQuestion());
                    if (q.getChapitre() != null) dto.setChapitreId(q.getChapitre().getId());
                    List<ReponsePourQuestionDto> options = q.getReponses().stream().map(ReponsePourQuestionDto::new).collect(Collectors.toList());
                    dto.setOptions(options);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public ResultatDiagnosticDto corrigerTestDiagnostic(SoumissionTestDto soumission) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal)) {
            throw new EntityNotFoundException("Utilisateur non authentifié ou principal invalide.");
        }

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Utilisateur etudiant = utilisateurRepository.findById(principal.getId())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé pour la correction."));

        Map<Long, Question> questionsMap = questionRepository.findAllById(
                soumission.getReponses().stream().map(ReponseSoumiseDto::getQuestionId).collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(Question::getId, q -> q));

        int totalBonnesReponses = 0;
        Map<Chapitre, List<Boolean>> resultatsParChapitre = new HashMap<>();

        for (ReponseSoumiseDto rep : soumission.getReponses()) {
            Question q = questionsMap.get(rep.getQuestionId());
            if (q == null || q.getChapitre() == null) continue;

            boolean correct = verifierReponse(q, rep.getReponse());
            if (correct) totalBonnesReponses++;
            resultatsParChapitre.computeIfAbsent(q.getChapitre(), k -> new ArrayList<>()).add(correct);
        }

        ResultatDiagnosticDto dto = new ResultatDiagnosticDto();
        dto.setTotalQuestions(questionsMap.size());
        dto.setBonnesReponses(totalBonnesReponses);
        dto.setScoreGlobal(totalBonnesReponses * 100.0 / questionsMap.size());

        Map<String, Double> scoreParChapitre = new HashMap<>();
        List<ChapitreRecommandationDto> chapitresAReviser = new ArrayList<>();
        for (Map.Entry<Chapitre, List<Boolean>> entry : resultatsParChapitre.entrySet()) {
            Chapitre chapitre = entry.getKey();
            List<Boolean> resultats = entry.getValue();
            long bonnes = resultats.stream().filter(b -> b).count();
            double score = bonnes * 100.0 / resultats.size();
            scoreParChapitre.put(chapitre.getNom(), score);
            if (score < 50) chapitresAReviser.add(new ChapitreRecommandationDto(chapitre.getId(), chapitre.getNom(), score));
        }

        dto.setScoreParChapitre(scoreParChapitre);
        dto.setChapitresAReviser(chapitresAReviser);
        dto.setMessage("Analyse de votre niveau terminée !");

        return dto;
    }

    // ===========================================
    // CRUD tests
    // ===========================================
    public Test save(Test test) {
        return testRepository.save(test);
    }

    public Test update(Long testId, Test testData) {
        Test existing = testRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("Test non trouvé avec l'ID: " + testId));
        existing.setTitre(testData.getTitre());
        existing.setDuree(testData.getDuree());
        existing.setDescription(testData.getDescription());
        return testRepository.save(existing);
    }

    public void deleteById(Long testId) {
        if (!testRepository.existsById(testId)) {
            throw new EntityNotFoundException("Test non trouvé avec l'ID: " + testId);
        }
        testRepository.deleteById(testId);
    }

}
