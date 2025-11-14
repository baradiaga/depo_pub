// Fichier : src/app/features/student/components/passer-test-connaissance/passer-test-connaissance.component.ts

import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

// --- On importe le bon service et les bons modèles ---
import { RecommandationTestService } from '../../../../services/recommandation-test.service';
import { Question } from '../../../../models/models'; // Assurez-vous que ce chemin est correct

@Component({
  selector: 'app-passer-test-connaissance',
  templateUrl: './passer-test-connaissance.component.html',
  styleUrls: ['./passer-test-connaissance.component.css']
})
export class PasserTestConnaissanceComponent implements OnInit, OnDestroy {

  isLoading = true;
  isSubmitting = false;
  
  questions: Question[] = [];
  
  page = 1;
  pageSize = 1;
  totalPages = 0;
  displayedQuestions: Question[] = [];

  reponsesUtilisateur: Map<number, any> = new Map();

  private matiereId!: number;

  // --- Variables pour le chronomètre ---
  public dureeTestEnMinutes = 0;
  private tempsRestantEnSecondes = 0;
  public tempsAffiche = '00:00';
  private timerInterval: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private recoTestService: RecommandationTestService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('matiereId');
    if (idParam) {
      this.matiereId = +idParam;
      this.loadQuestions();
    } else {
      this.toastr.error("ID de matière manquant. Impossible de charger le test.", "Erreur de Navigation");
      this.isLoading = false;
    }
  }

  ngOnDestroy(): void {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }
  }

  loadQuestions(): void {
    this.isLoading = true;
    this.recoTestService.getQuestionsPourTestDeConnaissance(this.matiereId).subscribe({
      next: (data: Question[]) => {
        if (data && data.length > 0) {
          this.questions = data;
          this.totalPages = this.questions.length;
          this.updateDisplayedQuestions();
          
          if (this.questions[0]?.dureeTest) {
            this.demarrerChrono(this.questions[0].dureeTest);
          }
        } else {
          this.toastr.info("Aucune question n'est disponible pour ce test de connaissance.", "Information");
        }
        this.isLoading = false;
      },
      error: (err: any) => {
        this.toastr.error("Erreur lors du chargement des questions du test.", "Erreur Réseau");
        this.isLoading = false;
      }
    });
  }

  updateDisplayedQuestions(): void {
    const startIndex = (this.page - 1) * this.pageSize;
    this.displayedQuestions = this.questions.slice(startIndex, startIndex + this.pageSize);
  }

  calculerProgression(): number {
    if (this.totalPages === 0) return 0;
    return (this.page / this.totalPages) * 100;
  }

  validerTest(): void {
    if (this.isSubmitting) return;
    this.isSubmitting = true;
    clearInterval(this.timerInterval);

    const reponsesAEnvoyer = Object.fromEntries(this.reponsesUtilisateur);
    
    console.log("Soumission des réponses pour le test de connaissance de la matière ID:", this.matiereId);
    console.log("Réponses:", reponsesAEnvoyer);
    
    this.toastr.success("Test de connaissance terminé ! Vos recommandations seront bientôt mises à jour.", "Félicitations !");
    this.router.navigate(['/app/student/parcours']);
  }

  demarrerChrono(minutes: number): void {
    if (minutes <= 0) return;
    this.dureeTestEnMinutes = minutes;
    this.tempsRestantEnSecondes = this.dureeTestEnMinutes * 60;

    this.timerInterval = setInterval(() => {
      this.tempsRestantEnSecondes--;
      const mins = Math.floor(this.tempsRestantEnSecondes / 60);
      const secs = this.tempsRestantEnSecondes % 60;
      this.tempsAffiche = `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;

      if (this.tempsRestantEnSecondes <= 0) {
        clearInterval(this.timerInterval);
        this.toastr.warning("Le temps est écoulé ! Le test va être soumis automatiquement.", "Temps écoulé");
        this.validerTest();
      }
    }, 1000);
  }

  choisirReponse(questionId: number, reponseId: number): void { this.reponsesUtilisateur.set(questionId, reponseId); }
  choisirReponseVraiFaux(questionId: number, reponse: boolean): void { this.reponsesUtilisateur.set(questionId, reponse); }
  choisirReponseMultiple(questionId: number, reponseId: number, event: any): void { let reponsesActuelles = this.reponsesUtilisateur.get(questionId) as number[] | undefined; if (!reponsesActuelles || !Array.isArray(reponsesActuelles)) { reponsesActuelles = []; } if (event.target.checked) { reponsesActuelles.push(reponseId); } else { const index = reponsesActuelles.indexOf(reponseId); if (index > -1) { reponsesActuelles.splice(index, 1); } } this.reponsesUtilisateur.set(questionId, reponsesActuelles); }
  saisirReponseTexte(questionId: number, event: any): void { this.reponsesUtilisateur.set(questionId, event.target.value); }
  precedent(): void { if (this.page > 1) { this.page--; this.updateDisplayedQuestions(); } }
  suivant(): void { if (this.page < this.totalPages) { this.page++; this.updateDisplayedQuestions(); } }
}
