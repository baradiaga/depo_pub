// Fichier : src/app/features/student/components/tests-list/tests-list.component.ts (Version finale avec compte à rebours)

// On doit importer OnDestroy pour pouvoir nettoyer le minuteur
import { Component, OnInit, OnDestroy } from '@angular/core'; 
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { TestService } from '../../../../services/test.service';
import { ChapitreService } from '../../../../services/chapitre.service';
import { Question, ResultatTest, ChapitreDetail } from '../../../../models/models';

@Component({
  selector: 'app-tests-list',
  templateUrl: './tests-list.component.html',
  styleUrls: ['./tests-list.component.css']
})
// On implémente OnDestroy
export class TestsListComponent implements OnInit, OnDestroy {

  isLoading = true;
  isSubmitting = false;
  questions: Question[] = [];
  page = 1;
  pageSize = 1;
  totalPages = 0;
  displayedQuestions: Question[] = [];
  reponsesUtilisateur: Map<number, any> = new Map();
  private chapitreId!: number;
  private matiereId!: number;

  // ====================================================================
  // === VARIABLES AJOUTÉES POUR LE COMPTE À REBOURS                    ===
  // ====================================================================
  private timerInterval: any; // Pour stocker la référence du minuteur (setInterval)
  public tempsRestant = { minutes: 0, secondes: 0 }; // Objet pour l'affichage
  public dureeTestEnMinutes = 0; // Durée totale du test, 0 = pas de limite

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private testService: TestService,
    private chapitreService: ChapitreService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('chapitreId');
    if (idParam) {
      this.chapitreId = +idParam;
      this.loadChapitreDetailsAndQuestions();
    } else {
      this.toastr.error("ID de chapitre manquant. Impossible de charger le test.");
      this.isLoading = false;
    }
  }

  /**
   * Implémentation de OnDestroy pour nettoyer le minuteur
   * lorsque l'utilisateur quitte la page du test. C'est crucial pour éviter les fuites de mémoire.
   */
  ngOnDestroy(): void {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }
  }

  loadChapitreDetailsAndQuestions(): void {
    // ... (logique inchangée)
    this.isLoading = true;
    this.chapitreService.getChapitreDetails(this.chapitreId).subscribe({
      next: (chapitre: ChapitreDetail) => {
        this.matiereId = chapitre.elementConstitutifId;
        this.loadQuestions();
      },
      error: (err) => {
        this.toastr.error("Erreur lors du chargement des informations du chapitre.");
        console.error(err);
        this.isLoading = false;
      }
    });
  }

  loadQuestions(): void {
    this.testService.getQuestionsPourChapitre(this.chapitreId).subscribe({
      next: (data: Question[]) => {
        if (data && data.length > 0) {
          this.questions = data;
          this.totalPages = this.questions.length;
          this.updateDisplayedQuestions();

          // ====================================================================
          // === DÉMARRAGE DU COMPTE À REBOURS                              ===
          // ====================================================================
          // On récupère la durée depuis la première question (toutes ont la même)
          const duree = data[0].dureeTest;
          if (duree && duree > 0) {
            this.dureeTestEnMinutes = duree;
            this.demarrerCompteARebours(this.dureeTestEnMinutes);
          }

        } else {
          this.toastr.info("Aucune question n'est disponible pour ce test.");
        }
        this.isLoading = false;
      },
      error: (err: any) => {
        this.toastr.error("Erreur lors du chargement des questions du test.");
        console.error(err);
        this.isLoading = false;
      }
    });
  }

  // ====================================================================
  // === NOUVELLE MÉTHODE POUR GÉRER LE COMPTE À REBOURS              ===
  // ====================================================================
  demarrerCompteARebours(minutes: number): void {
    let secondesTotales = minutes * 60;

    this.timerInterval = setInterval(() => {
      if (secondesTotales <= 0) {
        clearInterval(this.timerInterval);
        this.toastr.warning("Le temps est écoulé ! Le test va être soumis automatiquement.", "Temps écoulé");
        this.validerTest(); // Soumission automatique
      } else {
        secondesTotales--;
        this.tempsRestant.minutes = Math.floor(secondesTotales / 60);
        this.tempsRestant.secondes = secondesTotales % 60;
      }
    }, 1000); // Met à jour chaque seconde
  }

  updateDisplayedQuestions(): void {
    // ... (logique inchangée)
    const startIndex = (this.page - 1) * this.pageSize;
    this.displayedQuestions = this.questions.slice(startIndex, startIndex + this.pageSize);
  }

  calculerProgression(): number {
    // ... (logique inchangée)
    if (this.totalPages === 0) return 0;
    return (this.page / this.totalPages) * 100;
  }

  validerTest(): void {
    // On s'assure de ne pas soumettre deux fois
    if (this.isSubmitting) return; 

    this.isSubmitting = true;
    // On arrête le minuteur dès que le test est soumis
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }

    const reponsesAEnvoyer = Object.fromEntries(this.reponsesUtilisateur);

    this.testService.soumettreReponses(this.chapitreId, reponsesAEnvoyer).subscribe({
      next: (resultat: ResultatTest) => {
        this.toastr.success("Test soumis avec succès !");
        this.isSubmitting = false;
        
        this.router.navigate(['/app/student/recommendation'], {
          queryParams: {
            score: resultat.scoreObtenu,
            total: resultat.totalPointsPossible,
            chapitreId: this.chapitreId,
            matiereId: this.matiereId 
          }
        });
      },
      error: (err: any) => {
        this.toastr.error("Erreur lors de la soumission du test.");
        console.error(err);
        this.isSubmitting = false;
      }
    });
  }

  // --- Le reste des méthodes est inchangé ---
  choisirReponse(questionId: number, reponseId: number): void { this.reponsesUtilisateur.set(questionId, reponseId); }
  choisirReponseVraiFaux(questionId: number, reponse: boolean): void { this.reponsesUtilisateur.set(questionId, reponse); }
  choisirReponseMultiple(questionId: number, reponseId: number, event: any): void { let reponsesActuelles = this.reponsesUtilisateur.get(questionId) as number[] | undefined; if (!reponsesActuelles || !Array.isArray(reponsesActuelles)) { reponsesActuelles = []; } if (event.target.checked) { reponsesActuelles.push(reponseId); } else { const index = reponsesActuelles.indexOf(reponseId); if (index > -1) { reponsesActuelles.splice(index, 1); } } this.reponsesUtilisateur.set(questionId, reponsesActuelles); }
  saisirReponseTexte(questionId: number, event: any): void { this.reponsesUtilisateur.set(questionId, event.target.value); }
  precedent(): void { if (this.page > 1) { this.page--; this.updateDisplayedQuestions(); } }
  suivant(): void { if (this.page < this.totalPages) { this.page++; this.updateDisplayedQuestions(); } }
}
