// Fichier : src/app/features/student/components/tests-list/tests-list.component.ts (Corrigé et Finalisé)

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

// --- IMPORTS CORRIGÉS ---
import { TestService, Test, Question } from '../../../../services/test.service';
import { ResultatTestService, ReponseUtilisateur, Resultat } from '../../../../services/resultat-test.service';

@Component({
  selector: 'app-tests-list',
  templateUrl: './tests-list.component.html',
  styleUrls: ['./tests-list.component.css']
})
export class TestsListComponent implements OnInit {

  isLoading = true;
  isSubmitting = false;
  
  test: Test | null = null;
  questions: Question[] = [];
  
  page = 1;
  pageSize = 1;
  totalPages = 0;
  displayedQuestions: Question[] = [];

  reponsesUtilisateur: Map<number, ReponseUtilisateur> = new Map();

  private chapitreId!: number;
  private testId!: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private testService: TestService,
    private resultatTestService: ResultatTestService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.chapitreId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.chapitreId) {
      this.chargerTest();
    } else {
      this.toastr.error("ID de chapitre manquant. Impossible de charger le test.");
      this.isLoading = false;
    }
  }

  chargerTest(): void {
    this.isLoading = true;
    this.testService.getTestsByChapitre(this.chapitreId).subscribe({
      next: (tests: Test[]) => {
        if (tests.length > 0) {
          this.test = tests[0];
          this.testId = this.test.id;
          this.questions = this.test.questions || [];
          this.totalPages = this.questions.length;
          this.updateDisplayedQuestions();
        } else {
          this.toastr.info("Aucun test n'est disponible pour ce chapitre.");
        }
        this.isLoading = false;
      },
      error: (err: any) => {
        this.toastr.error("Erreur lors du chargement du test.");
        console.error(err);
        this.isLoading = false;
      }
    });
  }

  updateDisplayedQuestions(): void {
    const startIndex = (this.page - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.displayedQuestions = this.questions.slice(startIndex, endIndex);
  }

  choisirReponse(questionId: number, reponseId: number): void {
    this.reponsesUtilisateur.set(questionId, { questionId, reponseIds: [reponseId], texteReponse: null });
  }

  choisirReponseMultiple(questionId: number, reponseId: number, event: Event): void {
    const input = event.target as HTMLInputElement;
    const reponseActuelle = this.reponsesUtilisateur.get(questionId) || { questionId, reponseIds: [], texteReponse: null };
    
    if (input.checked) {
      reponseActuelle.reponseIds.push(reponseId);
    } else {
      reponseActuelle.reponseIds = reponseActuelle.reponseIds.filter(id => id !== reponseId);
    }
    this.reponsesUtilisateur.set(questionId, reponseActuelle);
  }

  choisirReponseVraiFaux(questionId: number, reponseBool: boolean): void {
    const question = this.questions.find(q => q.id === questionId);
    if (!question) return;
    
    const reponseCorrecte = question.reponses.find(r => r.texte.toLowerCase() === String(reponseBool));
    if (reponseCorrecte) {
      this.choisirReponse(questionId, reponseCorrecte.id);
    }
  }

  saisirReponseTexte(questionId: number, event: Event): void {
    const texte = (event.target as HTMLTextAreaElement).value;
    this.reponsesUtilisateur.set(questionId, { questionId, reponseIds: [], texteReponse: texte });
  }

  precedent(): void {
    if (this.page > 1) {
      this.page--;
      this.updateDisplayedQuestions();
    }
  }

  suivant(): void {
    if (this.page < this.totalPages) {
      this.page++;
      this.updateDisplayedQuestions();
    }
  }

  validerTest(): void {
    this.isSubmitting = true;
    const payload = Array.from(this.reponsesUtilisateur.values());

    this.resultatTestService.soumettreResultat(this.testId, payload).subscribe({
      next: (resultat: Resultat) => {
        this.toastr.success("Test soumis avec succès !");
        this.isSubmitting = false;
        
        // La propriété 'chapitre' n'existe pas sur Test, nous utilisons l'ID que nous avons déjà.
        const elementConstitutifId = 0; // Remarque: Cette info n'est pas disponible ici.
                                        // Il faudrait que l'API de test la renvoie.
                                        // Pour l'instant, on met 0.

        this.router.navigate(['/app/student/recommendation'], {
          queryParams: {
            score: resultat.score,
            total: resultat.scoreTotal,
            chapitreId: this.chapitreId,
            matiereId: elementConstitutifId 
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
}
