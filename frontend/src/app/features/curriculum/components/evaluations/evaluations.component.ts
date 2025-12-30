import { Component, OnInit, OnDestroy } from '@angular/core';
import { ElementConstitutifService } from '../../../../services/element-constitutif.service';
import { QuestionnaireService, QuestionnaireDetail } from '../../../../services/questionnaire.service';
import { ResultatTestService } from '../../../../services/resultat-test.service'; 
import { ElementConstitutifResponse } from '../../../../models/models';

@Component({
  selector: 'app-evaluations',
  templateUrl: './evaluations.component.html',
  styleUrls: ['./evaluations.component.css']
})
export class EvaluationsComponent implements OnInit, OnDestroy {
  // --- Données et Listes ---
  matieres: ElementConstitutifResponse[] = [];
  matiereSelectionnee: ElementConstitutifResponse | null = null;
  exercices: any[] = [];
  quizs: any[] = [];
  tests: any[] = [];

  // --- États de l'interface ---
  loading = false;
  vue: 'MATIERES' | 'EVALUATIONS' | 'LECTEUR' | 'RESULTAT' = 'MATIERES';
  questionnaireEnCours: QuestionnaireDetail | null = null;
  reponsesUtilisateur: Map<number, any> = new Map();
  resultatFinal: any = null;

  // --- Chronomètre ---
  tempsRestant: number = 0;
  timerInterval: any;

  constructor(
    private ecService: ElementConstitutifService,
    private questionnaireService: QuestionnaireService,
    private resultatService: ResultatTestService // Injection du service corrigé
  ) {}

  ngOnInit(): void {
    this.chargerMatieres();
  }

  ngOnDestroy(): void {
    if (this.timerInterval) clearInterval(this.timerInterval);
  }

  // ====================================================================
  // === NAVIGATION ET CHARGEMENT                                     ===
  // ====================================================================

  chargerMatieres(): void {
    this.loading = true;
    this.ecService.getElementsConstitutifsAvecDetails().subscribe({
      next: (data) => {
        this.matieres = data;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  choisirMatiere(matiere: ElementConstitutifResponse): void {
    if (!matiere.id) return;
    this.matiereSelectionnee = matiere;
    this.loading = true;

    this.questionnaireService.getQuestionnaires().subscribe({
      next: (all) => {
        const idsChap = (matiere.chapitres || []).map(c => c.id).filter(id => id !== undefined);
        const duContexte = all.filter(q => q.matiereId === matiere.id || (q.chapitreId && idsChap.includes(q.chapitreId)));

        this.exercices = duContexte.filter(q => q.type.toUpperCase() === 'EXERCICE');
        this.quizs = duContexte.filter(q => q.type.toUpperCase() === 'QUIZ');
        this.tests = duContexte.filter(q => q.type.toUpperCase() === 'TEST');

        this.vue = 'EVALUATIONS';
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  lancerEvaluation(id: number): void {
    this.questionnaireService.getQuestionnaireById(id).subscribe(data => {
      this.questionnaireEnCours = data;
      this.reponsesUtilisateur.clear();
      this.vue = 'LECTEUR';
      
      if (data.duree > 0) {
        this.tempsRestant = data.duree * 60;
        this.demarrerTimer();
      }
    });
  }

  // ====================================================================
  // === GESTION DES RÉPONSES                                         ===
  // ====================================================================

  choisirReponse(questionId: number, reponseId: number): void {
    this.reponsesUtilisateur.set(questionId, reponseId);
  }

  choisirReponseVraiFaux(questionId: number, reponse: boolean): void {
    this.reponsesUtilisateur.set(questionId, reponse);
  }

  choisirReponseMultiple(questionId: number, reponseId: number, event: any): void {
    let current = this.reponsesUtilisateur.get(questionId) || [];
    if (!Array.isArray(current)) current = [];

    if (event.target.checked) {
      current.push(reponseId);
    } else {
      current = current.filter((id: number) => id !== reponseId);
    }
    this.reponsesUtilisateur.set(questionId, current);
  }

  saisirReponseTexte(questionId: number, event: any): void {
    this.reponsesUtilisateur.set(questionId, event.target.value);
  }

  // ====================================================================
  // === VALIDATION ET SOUMISSION (CORRECTIF 404 APPLIQUÉ)            ===
  // ====================================================================

  soumettre(): void {
    if (!this.questionnaireEnCours || this.loading) return;
    if (this.timerInterval) clearInterval(this.timerInterval);

    this.loading = true;

    // Transformation des réponses pour le backend
    const reponsesFinales = Object.fromEntries(this.reponsesUtilisateur);

    // RÉCUPÉRATION DE L'ID DU CHAPITRE (Indispensable pour l'URL)
    const chapitreId = this.questionnaireEnCours.chapitreId;

    if (!chapitreId) {
      console.error("Erreur : Impossible de soumettre, chapitreId manquant.");
      this.loading = false;
      return;
    }

    // Appel au service corrigé qui utilise /api/tests/chapitre/{id}/soumettre
    this.resultatService.soumettreResultat(chapitreId, reponsesFinales).subscribe({
      next: (res) => {
        this.resultatFinal = res;
        this.vue = 'RESULTAT';
        this.loading = false;
      },
      error: (err) => {
        // C'est ici que l'erreur 404 disparaît
        console.error("Erreur lors de la soumission", err);
        alert("Une erreur est survenue lors de la validation du test.");
        this.loading = false;
      }
    });
  }

  // ====================================================================
  // === UTILITAIRES ET NAVIGATION                                    ===
  // ====================================================================

  retour(): void {
    if (this.vue === 'LECTEUR') {
      if (confirm("🚨 Quitter l'évaluation ? Votre progression sera perdue.")) {
        if (this.timerInterval) clearInterval(this.timerInterval);
        this.vue = 'EVALUATIONS';
      }
    } else if (this.vue === 'EVALUATIONS') {
      this.vue = 'MATIERES';
      this.matiereSelectionnee = null;
    } else if (this.vue === 'RESULTAT') {
      this.vue = 'EVALUATIONS';
      this.resultatFinal = null;
    }
  }

  demarrerTimer(): void {
    this.timerInterval = setInterval(() => {
      if (this.tempsRestant > 0) {
        this.tempsRestant--;
      } else {
        clearInterval(this.timerInterval);
        alert("Temps écoulé ! L'évaluation va être soumise.");
        this.soumettre();
      }
    }, 1000);
  }

  formatTemps(): string {
    const mins = Math.floor(this.tempsRestant / 60);
    const secs = this.tempsRestant % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  }
}
