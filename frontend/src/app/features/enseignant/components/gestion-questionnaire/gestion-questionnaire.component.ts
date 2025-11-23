// Fichier : src/app/features/enseignant/components/gestion-questionnaire/gestion-questionnaire.component.ts (Version Finale et Complète)

import { Component, OnInit } from '@angular/core';
import { switchMap } from 'rxjs/operators';
import { of } from 'rxjs';

// --- IMPORTS DE SERVICES ---
import { QuestionnaireService, QuestionnairePayload, ParametresGeneration, QuestionnaireDetail } from '../../../../services/questionnaire.service';
import { ElementConstitutifService } from '../../../../services/element-constitutif.service';
import { ChapitreService } from '../../../../services/chapitre.service';
import { TestService } from '../../../../services/test.service';

// --- IMPORTS DE MODÈLES ---
import { Chapitre, ElementConstitutifResponse, QuestionnaireManuel, QuestionPourCreation, CreateTestRequest } from '../../../../models/models';

@Component({
  selector: 'app-gestion-questionnaire',
  templateUrl: './gestion-questionnaire.component.html',
  styleUrls: ['./gestion-questionnaire.component.css']
})
export class GestionQuestionnaireComponent implements OnInit {

  // --- Vos propriétés existantes (inchangées) ---
  listeQuestionnaires: QuestionnaireDetail[] = [];
  chargementListe = false;
  modeCreation: 'manuel' | 'automatique' = 'manuel';
  mesMatieres: ElementConstitutifResponse[] = [];
  chapitresDisponibles: Chapitre[] = [];
  isLoadingMatieres = false;
  isLoadingChapitres = false;
  sauvegardeEnCours = false;
  questionnaire: QuestionnaireManuel = {
    titre: '', matiereId: null, chapitreId: null, duree: 0, description: '', questions: []
  };
  parametresGeneration: ParametresGeneration = {
    titre: '', nombreQuestions: 10, duree: 0, chapitresIds: []
  };
  matiereAutoId: number | null = null;
  generationEnCours = false;
  questionnaireGenere: QuestionnaireDetail | null = null;

  constructor(
    private questionnaireService: QuestionnaireService,
    private ecService: ElementConstitutifService,
    private chapitreService: ChapitreService,
    private testService: TestService
  ) {}

  ngOnInit(): void {
    this.chargerMesMatieres();
    this.chargerQuestionnaires();
  }

  chargerMesMatieres(): void {
    this.isLoadingMatieres = true;
    this.ecService.getMesMatieres().subscribe({
      next: (data) => {
        this.mesMatieres = data;
        this.isLoadingMatieres = false;
      },
      error: (err) => {
        console.error("Erreur lors du chargement des matières de l'enseignant", err);
        this.isLoadingMatieres = false;
      }
    });
  }

  onMatiereChange(): void {
    const matiereId = this.modeCreation === 'manuel' ? this.questionnaire.matiereId : this.matiereAutoId;
    this.chapitresDisponibles = [];
    if (this.modeCreation === 'manuel') { this.questionnaire.chapitreId = null; }
    else { this.parametresGeneration.chapitresIds = []; }
    if (!matiereId) return;
    this.isLoadingChapitres = true;
    this.chapitreService.getChapitresParMatiere(matiereId).subscribe({
      next: (data) => {
        this.chapitresDisponibles = data;
        this.isLoadingChapitres = false;
      },
      error: (err) => {
        console.error(`Erreur lors du chargement des chapitres pour la matière ${matiereId}`, err);
        this.isLoadingChapitres = false;
      }
    });
  }

  chargerQuestionnaires(): void {
    this.chargementListe = true;
    this.questionnaireService.getQuestionnaires().subscribe({
      next: (data) => { this.listeQuestionnaires = data; this.chargementListe = false; },
      error: (err) => { console.error("Erreur", err); alert("Erreur chargement."); this.chargementListe = false; }
    });
  }

  changerMode(event: Event): void {
    this.modeCreation = (event.target as HTMLSelectElement).value as 'manuel' | 'automatique';
    this.chapitresDisponibles = [];
  }

  reinitialiserFormulaireManuel(): void {
    this.questionnaire = { titre: '', matiereId: null, chapitreId: null, duree: 0, description: '', questions: [] };
    this.chapitresDisponibles = [];
  }

  questionnaireValide(): boolean {
    return !!(this.questionnaire.titre && this.questionnaire.matiereId && this.questionnaire.chapitreId && this.questionnaire.questions.length > 0);
  }

  parametresGenerationValides(): boolean {
    return !!(this.parametresGeneration.titre && this.matiereAutoId && this.parametresGeneration.chapitresIds.length > 0 && this.parametresGeneration.nombreQuestions > 0);
  }

  ajouterQuestion(): void {
    this.questionnaire.questions.push({ type: 'qcm', enonce: '', points: 1, difficulte: 'moyen', reponses: [{ texte: '', correcte: false }, { texte: '', correcte: false }] });
  }

  supprimerQuestion(index: number): void {
    this.questionnaire.questions.splice(index, 1);
  }

  changerTypeQuestion(i: number): void {
    const q = this.questionnaire.questions[i];
    if (!q) return;
    q.reponseVraiFaux = undefined;
    if (q.type === 'qcm' || q.type === 'qcu') {
      q.reponses = [{ texte: '', correcte: false }, { texte: '', correcte: false }];
    } else if (q.type === 'texte_libre') {
      q.reponses = [{ texte: '', correcte: true }];
    } else {
      q.reponses = [];
    }
  }

  ajouterReponse(i: number): void {
    this.questionnaire.questions[i]?.reponses?.push({ texte: '', correcte: false });
  }

  supprimerReponse(i: number, j: number): void {
    const reponses = this.questionnaire.questions[i]?.reponses;
    if (reponses && reponses.length > 2) {
      reponses.splice(j, 1);
    }
  }

  marquerReponseCorrecte(question: QuestionPourCreation, reponseIndex: number): void {
    if (!question.reponses) { return; }
    if (question.type === 'qcu') {
      question.reponses.forEach((rep: { texte: string, correcte: boolean }, idx: number) => rep.correcte = (idx === reponseIndex));
    } else if (question.type === 'qcm') {
      const reponse = question.reponses[reponseIndex];
      if (reponse) { reponse.correcte = !reponse.correcte; }
    }
  }

  toggleChapitre(id: number): void {
    const idx = this.parametresGeneration.chapitresIds.indexOf(id);
    if (idx > -1) {
      this.parametresGeneration.chapitresIds.splice(idx, 1);
    } else {
      this.parametresGeneration.chapitresIds.push(id);
    }
  }

  supprimerQuestionnaire(id: number, titre: string): void {
    if (confirm(`Supprimer "${titre}" ?`)) {
      this.questionnaireService.supprimerQuestionnaire(id).subscribe({
        next: () => { alert("Supprimé !"); this.chargerQuestionnaires(); },
        error: (err) => { console.error("Erreur suppression :", err); alert("Erreur."); }
      });
    }
  }

  genererQuestionnaire(): void {
    if (!this.parametresGenerationValides()) { alert("Champs obligatoires."); return; }
    this.generationEnCours = true;
    this.questionnaireGenere = null;
    this.questionnaireService.genererQuestionnaireAutomatique(this.parametresGeneration).subscribe({
      next: (questionnaire) => {
        this.questionnaireGenere = questionnaire;
        this.generationEnCours = false;
        alert(`Généré : "${questionnaire.titre}" !`);
        this.chargerQuestionnaires();
      },
      error: (err) => {
        console.error("Erreur génération", err);
        alert("Erreur: " + (err.error?.message || "Inconnue."));
        this.generationEnCours = false;
      }
    });
  }

  // ====================================================================
  // === MÉTHODE DE SAUVEGARDE CORRIGÉE POUR PUBLIER UN TEST FINAL       ===
  // ====================================================================
  sauvegarderQuestionnaire(): void {
    if (!this.questionnaireValide()) {
      alert("Veuillez remplir tous les champs obligatoires (titre, matière, chapitre) et ajouter au moins une question.");
      return;
    }

    this.sauvegardeEnCours = true;

    const payload: QuestionnairePayload = {
      titre: this.questionnaire.titre,
      chapitreId: this.questionnaire.chapitreId!,
      duree: this.questionnaire.duree,
      description: this.questionnaire.description,
      questions: this.questionnaire.questions
    };

    this.questionnaireService.sauvegarderQuestionnaire(payload).pipe(
      switchMap((questionnaireCree: any) => {
        console.log('Étape 1/2 : Questionnaire et questions créés.', questionnaireCree);

        if (!questionnaireCree || !questionnaireCree.questions || questionnaireCree.questions.length === 0) {
          throw new Error("La création du questionnaire n'a pas retourné les IDs des questions.");
        }

        // Récupération des IDs des questions qui viennent d'être créées
        const questionIds = questionnaireCree.questions.map((q: any) => q.id);

        const testRequest: CreateTestRequest = {
          titre: questionnaireCree.titre,
          chapitreId: questionnaireCree.chapitre.id,
          questionIds: questionIds
        };

        console.log('Étape 2/2 : Publication du Test avec le payload :', testRequest);
        // Appel au TestService pour créer/mettre à jour le Test avec les questions
        return this.testService.createTest(testRequest);
      })
    ).subscribe({
      next: () => {
        alert("Test créé et publié avec succès !");
        this.reinitialiserFormulaireManuel();
        this.chargerQuestionnaires();
        this.sauvegardeEnCours = false;
      },
      error: (err) => {
        console.error("Erreur lors du processus de sauvegarde/publication :", err);
        alert("Erreur: " + (err.error?.message || "Une erreur inconnue est survenue."));
        this.sauvegardeEnCours = false;
      }
    });
  }
}
