// Fichier : src/app/features/admin/components/gestion-parcours/gestion-parcours.component.ts (Mise à jour)

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {  StudentJourneyService } from '../services/student-journey.service';
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

  // Propriétés pour afficher le type de parcours actuel
  currentParcoursType: string = '';
  parcoursTypeLabel: string = '';

  constructor(
    private journeyService: StudentJourneyService,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    // Écouter les changements de paramètres de requête
    this.route.queryParams.subscribe(params => {
      this.currentParcoursType = params['type'] || '';
      this.setParcoursTypeLabel();
      this.loadStudentsOverview();
    });
  }

  private setParcoursTypeLabel() {
    switch (this.currentParcoursType) {
      case 'RECOMMANDE':
        this.parcoursTypeLabel = 'Parcours Recommandés';
        break;
      case 'CHOISI':
        this.parcoursTypeLabel = 'Parcours Choisis';
        break;
      case 'MIXTE':
        this.parcoursTypeLabel = 'Parcours Mixtes';
        break;
      default:
       // this.parcoursTypeLabel = 'Tous les Parcours';
    }
  }

  loadStudentsOverview() {
    this.loading = true;
    this.errorMessage = null;
    
    this.journeyService.getAllStudentsOverview(this.currentParcoursType).subscribe({
      next: (data) => {
        this.studentsOverview = data;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = "Erreur lors du chargement de la vue d'ensemble des étudiants. (Accès ADMIN requis)";
        this.loading = false;
        console.error(err);
      }
    });
  }

  viewStudentDetail(studentId: number) {
    this.loading = true;
    this.errorMessage = null;
    this.selectedStudentJourney = null;

    this.journeyService.getStudentJourneyDetail(studentId).subscribe({
      next: (data) => {
        this.selectedStudentJourney = data;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = `Erreur lors du chargement du parcours de l'étudiant ${studentId}.`;
        this.loading = false;
        console.error(err);
      }
    });
  }

  closeDetailView() {
    this.selectedStudentJourney = null;
  }
}
