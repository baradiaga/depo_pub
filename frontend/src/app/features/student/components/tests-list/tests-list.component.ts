import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TestService, Question, ResultatTest } from '../../../../services/test.service';
import { ChapitreService, ChapitreDetail } from '../../../../services/chapitre.service';

@Component({
  selector: 'app-tests-list',
  templateUrl: './tests-list.component.html',
  styleUrls: ['./tests-list.component.css']
})
export class TestsListComponent implements OnInit {
  chapitreId!: number;
  matiereId!: number;
  // ... (autres propriétés)
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
    const idParam = this.route.snapshot.paramMap.get('chapitreId');
    if (idParam) {
      this.chapitreId = +idParam;
      console.log(`[TestsList] ngOnInit: Chapitre ID récupéré de l'URL = ${this.chapitreId}`);
      this.loadChapitreDetailsAndQuestions();
    } else {
      console.error("[TestsList] ngOnInit: Aucun ID de chapitre trouvé dans l'URL !");
      this.isLoading = false;
    }
  }

  loadChapitreDetailsAndQuestions(): void {
    this.isLoading = true;
    console.log(`[TestsList] loadChapitreDetails: Appel du service pour récupérer les détails du chapitre ${this.chapitreId}`);
    this.chapitreService.getChapitreById(this.chapitreId).subscribe({
      next: (chapitreDetail) => {
        this.matiereId = chapitreDetail.matiereId;
        console.log(`[TestsList] loadChapitreDetails: Matière ID récupérée = ${this.matiereId}`);
        this.loadQuestions(); // On charge les questions seulement après avoir eu le matiereId
      },
      error: (err) => {
        console.error("[TestsList] ERREUR lors du chargement des détails du chapitre", err);
        this.isLoading = false;
      }
    });
  }

  loadQuestions(): void {
    // ... (logique inchangée)
    this.testService.getQuestionsPourChapitre(this.chapitreId).subscribe({
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

  validerTest(): void {
    this.isSubmitting = true;
    const reponsesAEnvoyer = Object.fromEntries(this.reponsesUtilisateur);
    const testId = this.chapitreId; 

    console.log(`[TestsList] validerTest: Soumission du test pour chapitre ID ${testId}`);
    this.testService.soumettreReponses(testId, reponsesAEnvoyer).subscribe({
      next: (resultat: ResultatTest) => {
        this.isSubmitting = false;
        console.log('[TestsList] validerTest: Réponse du back-end reçue :', resultat);

        const params = { 
          score: resultat.scoreObtenu, 
          total: resultat.totalPointsPossible, 
          chapitreId: this.chapitreId,
          matiereId: this.matiereId 
        };

        console.log('[TestsList] validerTest: Navigation vers /recommendation avec les paramètres :', params);
        
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
