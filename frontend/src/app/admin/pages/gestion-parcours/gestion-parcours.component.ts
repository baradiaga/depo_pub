import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { StudentJourneyService } from '../services/student-journey.service';
import { StudentJourney } from '../models/student-journey.model';

@Component({
  selector: 'app-gestion-parcours',
  templateUrl: './gestion-parcours.component.html',
  styleUrls: ['./gestion-parcours.component.css']
})
export class GestionParcoursComponent implements OnInit {

  studentsOverview: StudentJourney[] = [];
  selectedStudentJourney: StudentJourney | null = null;

  loading: boolean = false;
  errorMessage: string | null = null;

  // Filtrage par type
  parcoursTypes: string[] = ['RECOMMANDE', 'CHOISI', 'MIXTE'];
  currentParcoursType: string = 'RECOMMANDE';
  parcoursTypeLabel: string = '';

  constructor(
    private journeyService: StudentJourneyService,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.currentParcoursType = params['type'] || 'RECOMMANDE';
      this.setParcoursTypeLabel();
      this.loadStudentsOverview();
    });
  }

  setParcoursTypeLabel(): void {
    switch (this.currentParcoursType) {
      case 'RECOMMANDE': this.parcoursTypeLabel = 'Parcours Recommandés'; break;
      case 'CHOISI': this.parcoursTypeLabel = 'Parcours Choisis'; break;
      case 'MIXTE': this.parcoursTypeLabel = 'Parcours Mixtes'; break;
      default: this.parcoursTypeLabel = 'Tous les Parcours';
    }
  }

  changeParcoursType(type: string): void {
    if (this.currentParcoursType === type) return;
    this.currentParcoursType = type;
    this.setParcoursTypeLabel();
    this.loadStudentsOverview();
    this.selectedStudentJourney = null;
  }

  loadStudentsOverview(): void {
    this.loading = true;
    this.errorMessage = null;

    this.journeyService.getAllStudentsOverview(this.currentParcoursType)
      .subscribe({
        next: (data) => {
          this.studentsOverview = data;
          this.loading = false;
        },
        error: (err) => {
          console.error(err);
          this.errorMessage = "Erreur lors du chargement des étudiants. (Accès ADMIN requis)";
          this.loading = false;
        }
      });
  }

  viewStudentDetail(studentId: number): void {
    this.loading = true;
    this.errorMessage = null;

    this.journeyService.getStudentJourneyDetail(studentId)
      .subscribe({
        next: (data) => {
          this.selectedStudentJourney = data;
          this.loading = false;
        },
        error: (err) => {
          console.error(err);
          this.errorMessage = `Erreur lors du chargement du parcours de l'étudiant ${studentId}.`;
          this.loading = false;
        }
      });
  }

  closeDetailView(): void {
    this.selectedStudentJourney = null;
  }
}
