// Fichier : frontend/src/app/features/student/components/deroulement-test/deroulement-test.component.ts (Version Finale Corrigée)

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { QuestionDiagnostic, RecommandationTestService, ReponseSoumise } from '../../../../services/recommandation-test.service';

@Component({
  selector: 'app-deroulement-test',
  templateUrl: './deroulement-test.component.html',
  styleUrls: ['./deroulement-test.component.scss']
})
export class DeroulementTestComponent implements OnInit {

  matiereId: number | null = null;
  questions: QuestionDiagnostic[] = [];
  isLoading = true;
  isSubmitting = false;

  reponsesUtilisateur: { [questionId: number]: any } = {};

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private recommandationTestService: RecommandationTestService,
    private toastr: ToastrService
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      this.matiereId = id ? +id : null;
      if (this.matiereId) {
        this.chargerQuestions(this.matiereId);
      } else {
        this.isLoading = false;
        this.toastr.error("ID de matière non trouvé dans l'URL.", "Erreur critique");
      }
    });
  }

  chargerQuestions(matiereId: number): void {
    this.isLoading = true;
    this.recommandationTestService.genererTestDiagnostic(matiereId).subscribe({
      next: (data: QuestionDiagnostic[]) => {
        this.questions = data;

        // ====================================================================
        // === CORRECTION POUR L'ERREUR ExpressionChanged...               ===
        // ====================================================================
        // On initialise les réponses pour les QCM ici, dans le code TypeScript.
        this.questions.forEach(question => {
          if (question.typeQuestion === 'QCM') {
            this.reponsesUtilisateur[question.id] = {};
          }
        });

        this.isLoading = false;
        if (data.length > 0) {
          this.toastr.success(`${data.length} questions ont été chargées.`, "Test prêt !");
        } else {
          this.toastr.warning("Aucune question n'a pu être trouvée pour cette matière.", "Test vide");
        }
      },
      error: (err: any) => {
        this.isLoading = false;
        this.toastr.error("Une erreur est survenue lors du chargement des questions.", "Erreur");
        console.error(err);
      }
    });
  }

  soumettreTest(): void {
    this.isSubmitting = true;
    this.toastr.info("Soumission de vos réponses en cours...", "Veuillez patienter");

    const reponsesFinales: ReponseSoumise[] = Object.keys(this.reponsesUtilisateur).map(questionIdStr => {
      const questionId = parseInt(questionIdStr, 10);
      const question = this.questions.find(q => q.id === questionId);
      let reponseTraitee = this.reponsesUtilisateur[questionId];

      if (question && question.typeQuestion === 'QCM') {
        reponseTraitee = Object.keys(reponseTraitee)
          .filter(optionId => reponseTraitee[optionId] === true)
          .map(optionId => parseInt(optionId, 10));
      }

      return {
        questionId: questionId,
        reponse: reponseTraitee
      };
    });

    const payload = { reponses: reponsesFinales };
    console.log('Payload envoyé au service:', payload);

    this.recommandationTestService.soumettreTestDiagnostic(payload).subscribe({
      next: (resultat: any) => {
        this.isSubmitting = false;
        this.toastr.success("Test soumis et corrigé avec succès !", "Félicitations !");
        console.log("Résultat du test reçu du backend:", resultat);
        this.router.navigate(['/app/student/resultat-diagnostic'], { state: { resultat: resultat } });
      },
      error: (err: any) => {
        this.isSubmitting = false;
        this.toastr.error("Une erreur est survenue lors de la soumission du test.", "Erreur");
        console.error(err);
      }
    });
  }
}
