import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { StudentJourneyService } from '../services/student-journey.service';
import { StudentJourney } from '../models/student-journey.model';
import { ChapitreProgress } from '../models/chapitre-progress.model';

@Component({
  selector: 'app-gestion-parcours',
  templateUrl: './gestion-parcours.component.html',
  styleUrls: ['./gestion-parcours.component.css']
})
export class GestionParcoursComponent implements OnInit {

  studentsOverview: StudentJourney[] = [];
  selectedStudentJourney: StudentJourney | null = null;
  chapitresEtudiant: ChapitreProgress[] = [];
  chapitresGroupesParMatiere: { [key: string]: ChapitreProgress[] } = {};

  loading: boolean = false;
  errorMessage: string | null = null;

  // Filtrage par type
  parcoursTypes: string[] = ['RECOMMANDE', 'CHOISI', 'MIXTE'];
  currentParcoursType: string = 'RECOMMANDE';
  parcoursTypeLabel: string = '';

  // Vue détaillée
  showChapitresDetail: boolean = false;
  detailLoading: boolean = false;

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
    this.resetDetailView();
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
          // Charger les chapitres automatiquement
          this.viewStudentChapters(studentId);
        },
        error: (err) => {
          console.error(err);
          this.errorMessage = `Erreur lors du chargement du parcours de l'étudiant ${studentId}.`;
          this.loading = false;
        }
      });
  }

  viewStudentChapters(studentId: number): void {
    if (!studentId) return;
    
    this.detailLoading = true;
    this.showChapitresDetail = true;
    
    this.journeyService.getStudentChaptersProgress(studentId, this.currentParcoursType)
      .subscribe({
        next: (chapitres) => {
          this.chapitresEtudiant = chapitres;
          this.detailLoading = false;
        },
        error: (err) => {
          console.error('Erreur chapitres:', err);
          this.chapitresEtudiant = [];
          this.detailLoading = false;
        }
      });
  }

  viewStudentChaptersGrouped(studentId: number): void {
    if (!studentId) return;
    
    this.detailLoading = true;
    this.showChapitresDetail = true;
    
    this.journeyService.getStudentChaptersGroupedByMatiere(studentId, this.currentParcoursType)
      .subscribe({
        next: (grouped) => {
          this.chapitresGroupesParMatiere = grouped;
          this.detailLoading = false;
        },
        error: (err) => {
          console.error('Erreur chapitres groupés:', err);
          this.chapitresGroupesParMatiere = {};
          this.detailLoading = false;
        }
      });
  }

  getCouleurScore(score: number): string {
    if (score >= 70) return 'success';
    if (score >= 50) return 'warning';
    return 'danger';
  }

  getCouleurParcours(type: string): string {
    switch (type) {
      case 'RECOMMANDE': return 'primary';
      case 'CHOISI': return 'success';
      case 'MIXTE': return 'info';
      default: return 'secondary';
    }
  }

  getObjectKeys(obj: any): string[] {
    if (!obj) return [];
    return Object.keys(obj);
  }

  getMatiereFromChapitre(chapitre: ChapitreProgress): string {
    // À adapter selon votre logique pour extraire la matière
    return chapitre.chapitreNom.split(' - ')[0] || 'Non spécifiée';
  }

  closeDetailView(): void {
    this.selectedStudentJourney = null;
    this.chapitresEtudiant = [];
    this.chapitresGroupesParMatiere = {};
    this.showChapitresDetail = false;
  }

  resetDetailView(): void {
    this.closeDetailView();
  }
}