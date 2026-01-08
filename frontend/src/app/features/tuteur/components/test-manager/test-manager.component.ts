import { Component, OnInit } from '@angular/core';
import { QuestionnaireService, QuestionnaireDetail } from '../../../../services/questionnaire.service';
import { StudentService } from '../../../../services/student.service';

@Component({
  selector: 'app-test-manager',
  templateUrl: './test-manager.component.html',
  styleUrls: ['./test-manager.component.css']
})
export class TestManagerComponent implements OnInit {
  questionnaires: QuestionnaireDetail[] = [];
  selectedQId: number | null = null;
  isProcessing = false;

  constructor(
    // Public pour l'accès HTML, résout l'erreur d'accessibilité
    public questionnaireService: QuestionnaireService, 
    private studentService: StudentService
  ) {}

  ngOnInit(): void {
    this.loadExercices();
  }

  loadExercices(): void {
    this.isProcessing = true;
    // Typage explicite (data: QuestionnaireDetail[]) pour corriger l'erreur 7006
    this.questionnaireService.getExercicesOnly().subscribe({
      next: (data: QuestionnaireDetail[]) => {
        this.questionnaires = data;
        this.isProcessing = false;
      },
      error: (err: any) => {
        console.error('Erreur lors du chargement des exercices', err);
        this.isProcessing = false;
      }
    });
  }

  onTransformerEnTest(): void {
    if (this.selectedQId) {
      this.isProcessing = true;
      this.studentService.createTestFromQuestionnaire(this.selectedQId).subscribe({
        next: () => {
          alert('Succès : L\'exercice a été transformé en Test officiel !');
          this.isProcessing = false;
          this.selectedQId = null;
          this.loadExercices(); // Rafraîchir la liste
        },
        error: (err: any) => {
          console.error(err);
          this.isProcessing = false;
        }
      });
    }
  }

  onSupprimer(id: number | undefined): void {
    if (id && confirm('Voulez-vous supprimer cet exercice ?')) {
      this.questionnaireService.supprimerQuestionnaire(id).subscribe({
        next: () => this.loadExercices(),
        error: (err: any) => console.error(err)
      });
    }
  }
}
