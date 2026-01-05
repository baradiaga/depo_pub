import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { StudentJourneyService, StudentJourney, CourseProgress } from '../../../services/student-journey.service';
import { Subscription } from 'rxjs';

// Interface pour la réponse groupée
interface GroupedChapters {
  [key: string]: CourseProgress[];
}

interface SortConfig {
  field: string;
  direction: 'asc' | 'desc';
}

@Component({
  selector: 'app-gestion-parcours',
  templateUrl: './gestion-parcours.component.html',
  styleUrls: ['./gestion-parcours.component.css']
})
export class GestionParcoursComponent implements OnInit, OnDestroy {
  // Données
  studentsOverview: StudentJourney[] = [];
  selectedStudentJourney: StudentJourney | null = null;
  chapitresEtudiant: CourseProgress[] = [];
  chapitresGroupesParMatiere: GroupedChapters = {};

  // États de chargement
  loadingStates = {
    overview: false,
    studentDetail: false,
    chapters: false
  };

  currentLoadingMessage: string = '';
  errorMessage: string | null = null;
  errorType: 'network' | 'auth' | 'server' | 'not_found' | 'generic' | null = null;

  // Configuration des filtres
  parcoursTypes = [
    { value: 'RECOMMANDE', label: 'Parcours Recommandés', color: 'primary', icon: '📊' },
    { value: 'CHOISI', label: 'Parcours Choisis', color: 'success', icon: '🎯' },
    { value: 'MIXTE', label: 'Parcours Mixtes', color: 'info', icon: '🔄' }
  ];

  currentParcoursType: string = 'RECOMMANDE';
  parcoursTypeLabel: string = 'Parcours Recommandés';

  // Vue détaillée
  showChapitresDetail: boolean = false;
  activeDetailTab: 'liste' | 'matieres' | 'stats' = 'liste';

  // Tri
  sortConfig: SortConfig = { field: 'nomComplet', direction: 'asc' };

  private subscriptions = new Subscription();

  constructor(
    private journeyService: StudentJourneyService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    // Écoute les changements de paramètres dans l'URL
    this.subscriptions.add(
      this.route.queryParams.subscribe(params => {
        const type = params['type'] || 'RECOMMANDE';
        this.currentParcoursType = type;
        this.loadStudentsOverview();
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  // --- CHARGEMENT DES DONNÉES ---

  loadStudentsOverview(): void {
    this.startLoading('overview', 'Chargement de la liste des étudiants...');
    this.clearErrors();

    this.subscriptions.add(
      this.journeyService.getAll(this.currentParcoursType).subscribe({
        next: (data) => {
          this.studentsOverview = data;
          this.applySorting();
          this.stopLoading('overview');
        },
        error: (err) => {
          this.handleServiceError(err, 'chargement des étudiants');
          this.stopLoading('overview');
        }
      })
    );
  }

  viewStudentDetail(studentId: number): void {
    this.startLoading('studentDetail', 'Récupération du parcours détaillé...');
    this.clearErrors();
    this.showChapitresDetail = true;

    this.subscriptions.add(
      this.journeyService.getById(studentId).subscribe({
        next: (data) => {
          this.selectedStudentJourney = data;
          this.chapitresEtudiant = data.progressionParCours;
          this.groupChaptersLocally(data.progressionParCours);
          this.stopLoading('studentDetail');
        },
        error: (err) => {
          this.handleServiceError(err, `chargement du parcours de l'étudiant`);
          this.stopLoading('studentDetail');
        }
      })
    );
  }

  // --- LOGIQUE MÉTIER ---

  private groupChaptersLocally(chapters: CourseProgress[]): void {
    this.chapitresGroupesParMatiere = chapters.reduce((acc, curr) => {
      const key = curr.courseCode || 'AUTRE';
      if (!acc[key]) acc[key] = [];
      acc[key].push(curr);
      return acc;
    }, {} as GroupedChapters);
  }

  changeParcoursType(type: string): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { type },
      queryParamsHandling: 'merge'
    });
  }

  applySorting(): void {
    this.studentsOverview.sort((a, b) => {
      const valA = (a as any)[this.sortConfig.field];
      const valB = (b as any)[this.sortConfig.field];
      return this.sortConfig.direction === 'asc' 
        ? (valA > valB ? 1 : -1) 
        : (valA < valB ? 1 : -1);
    });
  }

  // --- GESTION DES ÉTATS (UI) ---

  private startLoading(type: keyof typeof this.loadingStates, message: string): void {
    this.loadingStates[type] = true;
    this.currentLoadingMessage = message;
  }

  private stopLoading(type: keyof typeof this.loadingStates): void {
    this.loadingStates[type] = false;
  }

  private clearErrors(): void {
    this.errorMessage = null;
    this.errorType = null;
  }

  private handleServiceError(error: any, context: string): void {
    console.error(`Erreur: ${context}`, error);
    if (error.status === 403) {
      this.errorMessage = "Accès refusé. Vous devez être Administrateur.";
      this.errorType = 'auth';
    } else {
      this.errorMessage = `Une erreur est survenue lors du ${context}.`;
      this.errorType = 'generic';
    }
  }

  resetDetailView(): void {
    this.selectedStudentJourney = null;
    this.showChapitresDetail = false;
    this.chapitresEtudiant = [];
  }
  getScoreClass(score: number): string {
  if (score >= 70) return 'badge badge-success';
  if (score >= 40) return 'badge badge-warning';
  return 'badge badge-danger';
}

getStatutBadgeClass(statut: string): string {
  if (statut?.includes('Bonne')) return 'badge-success';
  if (statut?.includes('Moyen')) return 'badge-warning';
  return 'badge-danger';
}

}
