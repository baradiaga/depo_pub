import { Component, OnInit } from '@angular/core';
import { AngularEditorConfig } from '@kolkov/angular-editor';
import { QuestionnaireService, QuestionnairePayload, ParametresGeneration, QuestionnaireDetail } from '../../../../services/questionnaire.service';
import { MatiereService, MatiereAvecDetails, ChapitrePourListe } from '../../../../services/matiere.service';

// Interface pour le formulaire (côté front-end)
export interface Question {
  type: 'qcm' | 'qcu' | 'vrai_faux' | 'texte_libre';
  enonce: string;
  points: number;
  difficulte: 'facile' | 'moyen' | 'difficile';
  reponses?: { texte: string; correcte: boolean; }[];
  reponseVraiFaux?: boolean;
}

export interface QuestionnaireManuel {
  titre: string;
  matiere: string;
  chapitre: string;
  duree: number;
  description: string;
  questions: Question[];
}

@Component({
  selector: 'app-gestion-questionnaire',
  templateUrl: './gestion-questionnaire.component.html',
  styleUrls: ['./gestion-questionnaire.component.css']
})
export class GestionQuestionnaireComponent implements OnInit {

  listeQuestionnaires: QuestionnaireDetail[] = [];
  chargementListe = false;
  modeCreation: 'manuel' | 'automatique' = 'manuel';
  matieres: MatiereAvecDetails[] = [];
  chapitresDisponibles: ChapitrePourListe[] = [];
  sauvegardeEnCours = false;

  questionnaire: QuestionnaireManuel = {
    titre: '', matiere: '', chapitre: '', duree: 0, description: '', questions: []
  };

  parametresGeneration: ParametresGeneration = {
    titre: '', nombreQuestions: 10, duree: 0, chapitresIds: []
  };
  
  matiereAutoId = '';
  generationEnCours = false;
  questionnaireGenere: QuestionnaireDetail | null = null;

  editorConfig: AngularEditorConfig = {
    editable: true, spellcheck: true, height: '150px', placeholder: 'Entrez votre texte ici...',
    enableToolbar: true, showToolbar: true, sanitize: true, toolbarPosition: 'top',
  };

  constructor(
    private questionnaireService: QuestionnaireService,
    private matiereService: MatiereService
  ) {}

  ngOnInit(): void {
    this.chargerMatieresEtChapitres();
    this.chargerQuestionnaires();
  }

  chargerMatieresEtChapitres(): void {
    // ====================================================================
    // --- CORRECTION APPLIQUÉE ICI ---
    // On spécifie le type du paramètre 'data' pour résoudre l'erreur TS7006.
    // ====================================================================
     this.matiereService.getMatieres().subscribe((data: MatiereAvecDetails[]) => { 
      this.matieres = data; 
    });
  }

  chargerQuestionnaires(): void {
    this.chargementListe = true;
    this.questionnaireService.getQuestionnaires().subscribe({
      next: (data) => {
        this.listeQuestionnaires = data;
        this.chargementListe = false;
      },
      error: (err) => {
        console.error("Erreur lors du chargement de la liste", err);
        alert("Impossible de charger la liste des questionnaires.");
        this.chargementListe = false;
      }
    });
  }

  changerMode(event: Event): void {
    this.modeCreation = (event.target as HTMLSelectElement).value as 'manuel' | 'automatique';
  }

  onMatiereChangeManuel(): void {
    const matiereTrouvee = this.matieres.find(m => m.id === +this.questionnaire.matiere);
    this.chapitresDisponibles = matiereTrouvee ? matiereTrouvee.chapitres : [];
    this.questionnaire.chapitre = '';
  }

  sauvegarderQuestionnaire(): void {
    if (!this.questionnaireValide()) {
      alert("Veuillez remplir tous les champs obligatoires et ajouter au moins une question.");
      return;
    }

    const payload: QuestionnairePayload = {
      titre: this.questionnaire.titre,
      chapitreId: +this.questionnaire.chapitre,
      duree: this.questionnaire.duree,
      description: this.questionnaire.description,
      questions: this.questionnaire.questions
    };

    this.sauvegardeEnCours = true;
    this.questionnaireService.sauvegarderQuestionnaire(payload).subscribe({
      next: () => {
        alert(`Le questionnaire "${payload.titre}" a été créé avec succès !`);
        this.reinitialiserFormulaireManuel();
        this.chargerQuestionnaires();
        this.sauvegardeEnCours = false;
      },
      error: (err) => {
        console.error("Erreur lors de la sauvegarde :", err);
        alert("Une erreur est survenue. Message : " + (err.error?.message || "Erreur inconnue."));
        this.sauvegardeEnCours = false;
      }
    });
  }

  reinitialiserFormulaireManuel(): void {
    this.questionnaire = {
      titre: '', matiere: '', chapitre: '', duree: 0, description: '', questions: []
    };
    this.chapitresDisponibles = [];
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

  marquerReponseCorrecte(question: Question, reponseIndex: number): void {
    if (!question.reponses) {
      return;
    }
    if (question.type === 'qcu') {
      question.reponses.forEach((rep, idx) => rep.correcte = (idx === reponseIndex));
    } else if (question.type === 'qcm') {
      const reponse = question.reponses[reponseIndex];
      if (reponse) {
        reponse.correcte = !reponse.correcte;
      }
    }
  }

  questionnaireValide(): boolean {
    return !!(this.questionnaire.titre && this.questionnaire.matiere && this.questionnaire.chapitre && this.questionnaire.questions.length > 0);
  }

  onMatiereChangeAuto(): void {
    const matiereTrouvee = this.matieres.find(m => m.id === +this.matiereAutoId);
    this.chapitresDisponibles = matiereTrouvee ? matiereTrouvee.chapitres : [];
    this.parametresGeneration.chapitresIds = [];
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
    if (confirm(`Êtes-vous sûr de vouloir supprimer le questionnaire "${titre}" ? Cette action est irréversible.`)) {
      this.questionnaireService.supprimerQuestionnaire(id).subscribe({
        next: () => {
          alert(`Le questionnaire "${titre}" a été supprimé avec succès.`);
          this.chargerQuestionnaires();
        },
        error: (err) => {
          console.error("Erreur lors de la suppression :", err);
          alert("Une erreur est survenue lors de la suppression du questionnaire.");
        }
      });
    }
  }

  parametresGenerationValides(): boolean {
    return !!(this.parametresGeneration.titre && this.matiereAutoId && this.parametresGeneration.chapitresIds.length > 0 && this.parametresGeneration.nombreQuestions > 0);
  }

  genererQuestionnaire(): void {
    if (!this.parametresGenerationValides()) {
      alert("Veuillez remplir tous les champs obligatoires.");
      return;
    }
    this.generationEnCours = true;
    this.questionnaireGenere = null;

    this.questionnaireService.genererQuestionnaireAutomatique(this.parametresGeneration).subscribe({
      next: (questionnaire) => {
        this.questionnaireGenere = questionnaire;
        this.generationEnCours = false;
        alert(`Questionnaire "${questionnaire.titre}" (ID: ${questionnaire.id}) généré avec succès !`);
        this.chargerQuestionnaires();
      },
      error: (err) => {
        console.error("Erreur lors de la génération automatique", err);
        alert("Erreur: " + (err.error?.message || "Une erreur inconnue est survenue."));
        this.generationEnCours = false;
      }
    });
  }
}
