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
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import java.util.regex.Pattern;
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
    // On cherche le test le plus récent pour ce chapitre
    return testRepository.findTopByChapitreId(chapitreId)
            .map(test -> test.getQuestions().stream()
                    .map(QuestionDto::new)
                    .collect(Collectors.toList()))
            .orElse(new ArrayList<>()); // Retourne une liste vide au lieu de planter
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
    
    boolean estCorrect = verifierReponse(question, reponse);
    if (estCorrect) {
        scoreObtenu += question.getPoints();
        bonnesReponses++;
    } else {
        // AJOUTEZ CE LOG POUR DEBUGGER
         
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
    private String normaliser(String str) {
    if (str == null) return "";
    // Enlever les accents (ex: "É" -> "E")
    String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    return pattern.matcher(nfdNormalizedString)
                  .replaceAll("")
                  .trim()
                  .toLowerCase();
}

  private boolean verifierReponse(Question question, Object reponseDonnee) {
    if (reponseDonnee == null) return false;

    // 1. Pré-traitement de l'entrée utilisateur
    String inputBrut = reponseDonnee.toString().trim();
    
    // Mapping spécial pour les booléens du Front-end
    if (question.getTypeQuestion() == TypeQuestion.VRAI_FAUX) {
        if (inputBrut.equalsIgnoreCase("true")) inputBrut = "Vrai";
        if (inputBrut.equalsIgnoreCase("false")) inputBrut = "Faux";
    }

    final String inputFinal = inputBrut;
    final String inputNormalise = normaliser(inputBrut);

    // 2. Logique de validation selon le type
    return switch (question.getTypeQuestion()) {
        
        case QCU, VRAI_FAUX, TEXTE_LIBRE -> {
            // A. Vérification par ID (Le plus sûr pour QCU/Vrai-Faux)
            boolean matchId = question.getReponses().stream()
                    .filter(Reponse::isCorrecte)
                    .anyMatch(r -> r.getId().toString().equals(inputFinal));
            
            if (matchId) yield true;

            // B. Vérification par Texte (Flexible pour TEXTE_LIBRE ou Vrai/Faux textuel)
            // On compare les versions normalisées (sans accents, sans casse)
            boolean matchTexte = question.getReponses().stream()
                    .filter(Reponse::isCorrecte)
                    .anyMatch(r -> normaliser(r.getTexte()).equals(inputNormalise));
            
            if (matchTexte) yield true;

            // C. Vérification dans le champ direct de la Question (Fallback)
            yield (question.getReponseCorrecteTexte() != null && 
                   normaliser(question.getReponseCorrecteTexte()).equals(inputNormalise));
        }

        case QCM -> {
            if (!(reponseDonnee instanceof List<?> liste)) yield false;

            Set<String> soumises = liste.stream()
                    .map(Object::toString)
                    .map(String::trim)
                    .collect(Collectors.toSet());

            Set<String> correctes = question.getReponses().stream()
                    .filter(Reponse::isCorrecte)
                    .map(r -> r.getId().toString())
                    .collect(Collectors.toSet());

            yield !correctes.isEmpty() && soumises.equals(correctes);
        }

        default -> false;
    };
}


    // ===========================================
    // Tests diagnostics
    // ===========================================
   public List<QuestionDiagnosticDto> genererTestDiagnosticPourMatiere(Long matiereId) {
    // Correction : Utiliser un repository qui ramène aussi les réponses pour peupler les options
    List<Question> toutesLesQuestions = questionRepository.findQuestionsByMatiereId(matiereId);
    
    return toutesLesQuestions.stream()
            .limit(20)
            .map(q -> {
                QuestionDiagnosticDto dto = new QuestionDiagnosticDto();
                dto.setId(q.getId());
                dto.setEnonce(q.getEnonce());
                dto.setTypeQuestion(q.getTypeQuestion());
                if (q.getChapitre() != null) dto.setChapitreId(q.getChapitre().getId());
                
                // Important : Mapper les options pour que le front puisse afficher les choix
                List<ReponsePourQuestionDto> options = q.getReponses().stream()
                        .map(ReponsePourQuestionDto::new)
                        .collect(Collectors.toList());
                dto.setOptions(options);
                return dto;
            })
            .collect(Collectors.toList());
}

@Transactional
public ResultatDiagnosticDto corrigerTestDiagnostic(SoumissionTestDto soumission) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal)) {
        throw new EntityNotFoundException("Utilisateur non authentifié.");
    }

    UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
    Utilisateur etudiant = utilisateurRepository.findById(principal.getId())
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé."));

    // CORRECTION : Utilisation de findAllWithReponsesByIds pour charger les données nécessaires à verifierReponse
    List<Long> ids = soumission.getReponses().stream()
            .map(ReponseSoumiseDto::getQuestionId)
            .collect(Collectors.toList());

    Map<Long, Question> questionsMap = questionRepository.findAllWithReponsesByIds(ids)
            .stream().collect(Collectors.toMap(Question::getId, q -> q));

    int totalBonnesReponses = 0;
    Map<Chapitre, List<Boolean>> resultatsParChapitre = new HashMap<>();

    for (ReponseSoumiseDto rep : soumission.getReponses()) {
        Question q = questionsMap.get(rep.getQuestionId());
        // Sécurité : on vérifie que la question existe
        if (q == null) continue;

        // Utilisation de votre méthode de référence verifierReponse
        boolean correct = verifierReponse(q, rep.getReponse());
        
        if (correct) totalBonnesReponses++;
        
        // On ne remplit les stats par chapitre que si le chapitre est présent
        if (q.getChapitre() != null) {
            resultatsParChapitre.computeIfAbsent(q.getChapitre(), k -> new ArrayList<>()).add(correct);
        }
    }

    ResultatDiagnosticDto dto = new ResultatDiagnosticDto();
    int nbQuestionsMap = questionsMap.size();
    dto.setTotalQuestions(nbQuestionsMap);
    dto.setBonnesReponses(totalBonnesReponses);
    
    // Calcul sécurisé du score global
    dto.setScoreGlobal(nbQuestionsMap > 0 ? (totalBonnesReponses * 100.0 / nbQuestionsMap) : 0);

    Map<String, Double> scoreParChapitre = new HashMap<>();
    List<ChapitreRecommandationDto> chapitresAReviser = new ArrayList<>();

    for (Map.Entry<Chapitre, List<Boolean>> entry : resultatsParChapitre.entrySet()) {
        Chapitre chapitre = entry.getKey();
        List<Boolean> resultats = entry.getValue();
        long bonnes = resultats.stream().filter(b -> b).count();
        double score = (bonnes * 100.0) / resultats.size();
        
        scoreParChapitre.put(chapitre.getNom(), score);
        if (score < 50) {
            chapitresAReviser.add(new ChapitreRecommandationDto(chapitre.getId(), chapitre.getNom(), score));
        }
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
    @Transactional
public Test assignerQuestionnaireAuTest(Long chapitreId, Long questionnaireId) {
    // 1. Vérifier le chapitre
    Chapitre chapitre = chapitreRepository.findById(chapitreId)
            .orElseThrow(() -> new EntityNotFoundException("Chapitre non trouvé"));

    // 2. Vérifier le questionnaire
    Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId)
            .orElseThrow(() -> new EntityNotFoundException("Questionnaire non trouvé"));

    // 3. Chercher s'il existe déjà un test pour ce chapitre ou en créer un nouveau
    Test test = testRepository.findTopByChapitreId(chapitreId).orElse(new Test());
    
    test.setTitre("Test : " + questionnaire.getTitre());
    test.setChapitre(chapitre);
    test.setQuestionnaire(questionnaire);
    
    // 4. Copier les questions du questionnaire vers le test
    test.setQuestions(new ArrayList<>(questionnaire.getQuestions()));

    return testRepository.save(test);
}
/**
 * CORRECTION POUR LE MODE ENTRAÎNEMENT (Exercices/Quiz)
 * On compare les réponses directement par rapport au Questionnaire
 */
public ResultatTestDto calculerResultatEntrainement(Long questionnaireId, Map<String, Object> reponsesUtilisateur) {
    // On cherche le questionnaire directement (et non le test du chapitre)
    Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId)
            .orElseThrow(() -> new EntityNotFoundException("Questionnaire non trouvé ID: " + questionnaireId));

    double scoreObtenu = 0;
    double totalPoints = 0;
    int bonnesReponses = 0;

    for (Question question : questionnaire.getQuestions()) {
        totalPoints += question.getPoints();
        Object reponse = reponsesUtilisateur.get(String.valueOf(question.getId()));
        
        // On utilise votre moteur de vérification (ID vs ID)
        if (verifierReponse(question, reponse)) {
            scoreObtenu += question.getPoints();
            bonnesReponses++;
        }
    }

    ResultatTestDto dto = new ResultatTestDto();
    dto.setScoreObtenu(scoreObtenu);
    dto.setTotalPointsPossible(totalPoints);
    dto.setDateSoumission(LocalDateTime.now());
    return dto;
}


}
