import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { QuestionnaireService, QuestionnaireDetail, Test } from '../../../../services/questionnaire.service';

@Component({
  selector: 'app-questionnaire-details',
  templateUrl: './questionnaire-details.component.html',
  styleUrls: ['./questionnaire-details.component.css']
})
export class QuestionnaireDetailsComponent implements OnInit {
  questionnaire!: QuestionnaireDetail;
  tests: Test[] = [];

  constructor(
    private route: ActivatedRoute,
    private questionnaireService: QuestionnaireService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.loadQuestionnaire(id);
    this.loadTests(id);
  }

  // Charger les infos du questionnaire
  loadQuestionnaire(id: number): void {
    this.questionnaireService.getQuestionnaireDetails(id).subscribe({
      next: (q: QuestionnaireDetail) => {
        this.questionnaire = q;
      },
      error: (err) => {
        console.error('Erreur lors du chargement du questionnaire', err);
      }
    });
  }

  // Charger les tests associés
  loadTests(questionnaireId: number): void {
    this.questionnaireService.getTestsByQuestionnaire(questionnaireId).subscribe({
      next: (tests: Test[]) => {
        this.tests = tests;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des tests', err);
      }
    });
  }

  // CRUD Tests
  createTest(): void {
    const newTest: Partial<Test> = { titre: 'Nouveau test' };
    this.questionnaireService.createTest(this.questionnaire.id, newTest).subscribe({
      next: () => this.loadTests(this.questionnaire.id),
      error: (err) => console.error('Erreur lors de la création du test', err)
    });
  }

  updateTest(test: Test): void {
    const updatedTest: Partial<Test> = {
      titre: test.titre + ' (modifié)',
      duree: test.duree,
      description: test.description
    };
    this.questionnaireService.updateTest(test.id, updatedTest).subscribe({
      next: () => this.loadTests(this.questionnaire.id),
      error: (err) => console.error('Erreur lors de la mise à jour du test', err)
    });
  }

  deleteTest(testId: number): void {
    this.questionnaireService.deleteTest(testId).subscribe({
      next: () => this.loadTests(this.questionnaire.id),
      error: (err) => console.error('Erreur lors de la suppression du test', err)
    });
  }
}
