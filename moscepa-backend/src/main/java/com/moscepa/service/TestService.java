// Fichier : src/main/java/com/moscepa/service/TestService.java (Version Finale et Corrigée)

package com.moscepa.service;

import com.moscepa.dto.*;
import com.moscepa.entity.*;
import com.moscepa.repository.*;
import com.moscepa.security.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional; // Assurez-vous que cet import est présent
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TestService {

    private static final Logger log = LoggerFactory.getLogger(TestService.class);

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final ParcoursService parcoursService;
    private final ResultatTestRepository resultatTestRepository;
    private final UtilisateurRepository utilisateurRepository;
    
    public TestService(TestRepository testRepository, QuestionRepository questionRepository, ParcoursService parcoursService, ResultatTestRepository resultatTestRepository, UtilisateurRepository utilisateurRepository) {
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.parcoursService = parcoursService;
        this.resultatTestRepository = resultatTestRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // --- VOS MÉTHODES EXISTANTES (PRÉSERVÉES) ---
    // ... (calculerEtSauvegarderResultat, getHistoriquePourEtudiant, etc. sont ici)
    public List<QuestionDto> getQuestionsPourChapitre(Long chapitreId) {
        List<Question> questions = questionRepository.findByQuestionnaireChapitreId(chapitreId);
        if (questions.isEmpty()) {
            log.warn("Aucune question trouvée pour le chapitre ID: {}", chapitreId);
        }
        return questions.stream().map(QuestionDto::new).collect(Collectors.toList());
    }

    @Transactional
    public ResultatTestDto calculerEtSauvegarderResultat(Long chapitreId, Long utilisateurId, Map<String, Object> reponsesUtilisateur) {
        Utilisateur etudiant = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Aucun utilisateur (étudiant) trouvé pour l'ID: " + utilisateurId));
        
        List<Question> questionsDuTest = questionRepository.findByQuestionnaireChapitreId(chapitreId);
        if (questionsDuTest.isEmpty()) {
            throw new EntityNotFoundException("Aucune question trouvée pour le chapitre ID : " + chapitreId);
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

        Test test = testRepository.findTopByChapitreId(chapitreId)
                .orElseThrow(() -> new EntityNotFoundException("Aucun test trouvé pour le chapitre ID: " + chapitreId));

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
        Map<Chapitre, List<Question>> questionsParChapitre = toutesLesQuestions.stream()
                .collect(Collectors.groupingBy(q -> q.getQuestionnaire().getChapitre()));
        List<Question> questionsSelectionnees = new ArrayList<>();
        final int QUESTIONS_PAR_CHAPITRE = 2;
        for (List<Question> questionsDuChapitre : questionsParChapitre.values()) {
            Collections.shuffle(questionsDuChapitre);
            List<Question> questionsAChoisir = questionsDuChapitre.stream()
                    .limit(QUESTIONS_PAR_CHAPITRE)
                    .collect(Collectors.toList());
            questionsSelectionnees.addAll(questionsAChoisir);
        }
        Collections.shuffle(questionsSelectionnees);
        log.info("{} questions sélectionnées au total pour le test de connaissance.", questionsSelectionnees.size());
        return questionsSelectionnees.stream()
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
        Collections.shuffle(toutesLesQuestions);
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
        Collections.shuffle(optionsDto);
        dto.setOptions(optionsDto);
        return dto;
    }


    // ====================================================================
    // === MÉTHODE DE VÉRIFICATION FINALE (ANTI-CLASSE ANONYME)         ===
    // ====================================================================
    private boolean verifierReponse(Question question, Object reponseDonnee) {
        if (reponseDonnee == null) {
            return false;
        }
        
        String reponseStr = (reponseDonnee instanceof List) ? null : reponseDonnee.toString();

        try {
            switch (question.getTypeQuestion()) {
                case QCU:
                    if (reponseStr == null) return false;
                    
                    Optional<Reponse> bonneReponseOpt = question.getReponses().stream()
                            .filter(Reponse::isCorrecte)
                            .findFirst();

                    if (bonneReponseOpt.isEmpty()) {
                        return false;
                    }

                    String bonneReponseIdStr = bonneReponseOpt.get().getId().toString();
                    return reponseStr.trim().equals(bonneReponseIdStr);

                case VRAI_FAUX:
                case TEXTE_LIBRE:
                    return reponseStr != null && reponseStr.trim().equalsIgnoreCase(question.getReponseCorrecteTexte().trim());

                case QCM:
                    if (!(reponseDonnee instanceof List)) {
                        return false;
                    }
                    Set<String> reponsesSoumisesIds = ((List<?>) reponseDonnee).stream()
                        .map(Object::toString)
                        .collect(Collectors.toSet());
                    
                    Set<String> bonnesReponsesIds = question.getReponses().stream()
                        .filter(Reponse::isCorrecte)
                        .map(r -> r.getId().toString())
                        .collect(Collectors.toSet());
                    
                    return reponsesSoumisesIds.equals(bonnesReponsesIds);

                default:
                    return false;
            }
        } catch (Exception e) {
            log.error("Erreur de vérification pour la question ID {}: {}", question.getId(), e.getMessage());
            return false;
        }
    }

    // ====================================================================
    // === NOUVELLE LOGIQUE DE CORRECTION (INCHANGÉE)                   ===
    // ====================================================================
    private static final double SEUIL_RECOMMANDATION = 50.0;

    @Transactional
    public ResultatDiagnosticDto corrigerTestDiagnostic(SoumissionTestDto soumission) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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

            if (scoreChapitre < SEUIL_RECOMMANDATION) {
                chapitresAReviser.add(new ChapitreRecommandationDto(chapitre.getId(), chapitre.getNom(), scoreChapitre));
            }
        }

        resultatFinal.setScoreParChapitre(scoreParChapitreMap);
        resultatFinal.setChapitresAReviser(chapitresAReviser);
        resultatFinal.setMessage("Analyse de votre niveau terminée !");

        log.info("Analyse de diagnostic pour l'étudiant {}: {}/{}", etudiant.getEmail(), totalBonnesReponses, questionsMap.size());

        return resultatFinal;
    }

    private double calculatePercentage(long numerator, long denominator) {
        if (denominator == 0) return 0.0;
        return ((double) numerator / denominator) * 100.0;
    }
}
