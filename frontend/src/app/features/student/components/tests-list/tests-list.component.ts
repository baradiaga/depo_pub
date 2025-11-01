import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TestService, Question, ResultatTest } from '../../../../services/test.service';
import { ChapitreService, ChapitreDetail } from '../../../../services/chapitre.service';
import { EtudiantTestsDashboard, MatiereDashboard, ChapitreDashboard, TestDashboard } from '../../../../models/dashboard.model';

@Component({
  selector: 'app-tests-list',
  templateUrl: './tests-list.component.html',
  styleUrls: ['./tests-list.component.css']
})
export class TestsListComponent implements OnInit {
  // --- Nouvelles propriétés pour le Dashboard ---
  dashboard: EtudiantTestsDashboard | null = null;
  matieres: MatiereDashboard[] = [];
  matiereSelectionnee: MatiereDashboard | null = null;
  chapitreSelectionne: ChapitreDashboard | null = null;
  testSelectionne: TestDashboard | null = null;

  // --- Propriétés pour le Test en cours ---
  isTestMode = false; // Vrai si un test est en cours de passation
  isLoading = true;
  isSubmitting = false;
  reponsesUtilisateur: Map<number, any> = new Map();
  questions: Question[] = [];
  displayedQuestions: Question[] = [];
  page = 1;
  pageSize = 5;
  totalPages = 1;

  constructor(
    private route: ActivatedRoute, 
    private router: Router,
    private testService: TestService,
    private chapitreService: ChapitreService
  ) {}

  ngOnInit() {
    // Le composant ne charge plus un test directement via l'URL, mais le dashboard.
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.isLoading = true;
    this.testService.getEtudiantTestsDashboard().subscribe({
      next: (data) => {
        this.dashboard = data;
        this.matieres = data.matieres;
        this.isLoading = false;
      },
      error: (err) => {
        console.error("[TestsList] ERREUR lors du chargement du dashboard", err);
        this.isLoading = false;
      }
    });
  }

  // --- Logique de sélection ---

  selectionnerMatiere(matiere: MatiereDashboard): void {
    this.matiereSelectionnee = matiere;
    this.chapitreSelectionne = null;
    this.testSelectionne = null;
  }

  selectionnerChapitre(chapitre: ChapitreDashboard): void {
    this.chapitreSelectionne = chapitre;
    this.testSelectionne = null;
  }

  selectionnerTest(test: TestDashboard): void {
    this.testSelectionne = test;
    this.questions = [];
    this.displayedQuestions = [];
    this.page = 1;
    this.totalPages = 1;
    this.reponsesUtilisateur = new Map();
    this.isTestMode = true;
    this.loadQuestions(this.chapitreSelectionne!.id); // Les tests sont liés au chapitre
  }

  // --- Logique de chargement des questions (adaptée) ---

  loadQuestions(chapitreId: number): void {
    this.isLoading = true;
    this.testService.getQuestionsPourChapitre(chapitreId).subscribe({
        next: (data) => {
            this.questions = data;
            this.totalPages = Math.ceil(this.questions.length / this.pageSize);
            this.updateDisplayedQuestions();
            this.isLoading = false;
        },
        error: (err) => {
            console.error("[TestsList] ERREUR lors du chargement des questions", err);
            this.isLoading = false;
        }
    });
  }

  // --- Logique de validation du test (adaptée) ---

  validerTest(): void {
    if (!this.testSelectionne) return;

    this.isSubmitting = true;
    const reponsesAEnvoyer = Object.fromEntries(this.reponsesUtilisateur);
    const testId = this.testSelectionne.id; 

    this.testService.soumettreReponses(testId, reponsesAEnvoyer).subscribe({
      next: (resultat: ResultatTest) => {
        this.isSubmitting = false;
        
        // Après la soumission, on revient au dashboard et on le recharge
        this.isTestMode = false;
        this.loadDashboard();
        
        // On navigue vers la recommandation (si nécessaire)
        const params = { 
          score: resultat.scoreObtenu, 
          total: resultat.totalPointsPossible, 
          chapitreId: this.chapitreSelectionne!.id,
          matiereId: this.matiereSelectionnee!.id 
        };
        
        this.router.navigate(['/app/student/recommendation'], { queryParams: params });
      },
      error: (err: any) => {
        this.isSubmitting = false;
        console.error("[TestsList] ERREUR lors de la soumission du test", err);
      }
    });
  }

  // --- Le reste des méthodes (pagination, sélection des réponses) est inchangé ---
  updateDisplayedQuestions(): void { const startIndex = (this.page - 1) * this.pageSize; this.displayedQuestions = this.questions.slice(startIndex, startIndex + this.pageSize); }
  choisirReponse(questionId: number, reponseId: number): void { this.reponsesUtilisateur.set(questionId, reponseId); }
  choisirReponseVraiFaux(questionId: number, reponse: boolean): void { this.reponsesUtilisateur.set(questionId, reponse); }
  choisirReponseMultiple(questionId: number, reponseId: number, event: any): void { let reponsesActuelles = this.reponsesUtilisateur.get(questionId) as number[] | undefined; if (!reponsesActuelles || !Array.isArray(reponsesActuelles)) { reponsesActuelles = []; } if (event.target.checked) { reponsesActuelles.push(reponseId); } else { const index = reponsesActuelles.indexOf(reponseId); if (index > -1) { reponsesActuelles.splice(index, 1); } } this.reponsesUtilisateur.set(questionId, reponsesActuelles); }
  saisirReponseTexte(questionId: number, event: any): void { this.reponsesUtilisateur.set(questionId, event.target.value); }
  suivant(): void { if (this.page < this.totalPages) { this.page++; this.updateDisplayedQuestions(); } }
  precedent(): void { if (this.page > 1) { this.page--; this.updateDisplayedQuestions(); } }
}
