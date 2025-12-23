import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { StudentJourneyService } from '../services/student-journey.service';
import { StudentJourney } from '../models/student-journey.model';
import { ChapitreProgress } from '../models/chapitre-progress.model';
import { Subscription } from 'rxjs';

// Interfaces locales
interface SortConfig {
  field: string;
  direction: 'asc' | 'desc';
}

interface StudentStats {
  scoreMoyen: number;
  chapitresTermines: number;
  tempsTotal: number;
  meilleurScore: number;
  pireScore: number;
}

// Interface pour la réponse groupée
interface GroupedChapters {
  [key: string]: ChapitreProgress[];
}

@Component({
  selector: 'app-gestion-parcours',
  templateUrl: './gestion-parcours.component.html',
  styleUrls: ['./gestion-parcours.component.css']
})
export class GestionParcoursComponent implements OnInit, OnDestroy {
  // CORRECTION: Utiliser le bon type
  studentsOverview: StudentJourney[] = [];
  selectedStudentJourney: StudentJourney | null = null;
  chapitresEtudiant: ChapitreProgress[] = [];
  // CORRECTION: Type correct pour les chapitres groupés
  chapitresGroupesParMatiere: GroupedChapters = {};

  // États de chargement
  loadingStates = {
    overview: false,
    studentDetail: false,
    chapters: false,
    groupedChapters: false
  };

  loadingMessages = {
    overview: 'Chargement de la liste des étudiants...',
    studentDetail: 'Récupération des informations étudiant...',
    chapters: 'Analyse de la progression par chapitre...',
    groupedChapters: 'Regroupement des chapitres par matière...'
  };

  currentLoadingMessage: string = '';
  
  // Gestion d'erreurs
  errorMessage: string | null = null;
  errorType: 'network' | 'auth' | 'server' | 'not_found' | 'generic' | null = null;

  // Configuration des parcours
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
  sortConfig: SortConfig = { field: 'nom', direction: 'asc' };

  // Souscriptions
  private subscriptions = new Subscription();

  private readonly SCORE_THRESHOLDS = {
    EXCELLENT: 80,
    BON: 60,
    MOYEN: 40,
    FAIBLE: 0
  } as const;

  constructor(
    private journeyService: StudentJourneyService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.subscriptions.add(
      this.route.queryParams.subscribe(params => {
        const type = params['type'];
        if (type && this.isValidParcoursType(type)) {
          this.changeParcoursType(type, false);
        } else {
          this.router.navigate([], {
            relativeTo: this.route,
            queryParams: { type: 'RECOMMANDE' },
            queryParamsHandling: 'merge'
          });
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  private isValidParcoursType(type: string): boolean {
    return this.parcoursTypes.some(p => p.value === type);
  }

  changeParcoursType(type: string, navigate: boolean = true): void {
    if (this.currentParcoursType === type) return;

    const parcoursConfig = this.parcoursTypes.find(p => p.value === type);
    if (!parcoursConfig) return;

    this.currentParcoursType = type;
    this.parcoursTypeLabel = parcoursConfig.label;

    if (navigate) {
      this.router.navigate([], {
        relativeTo: this.route,
        queryParams: { type },
        queryParamsHandling: 'merge'
      });
    } else {
      this.loadStudentsOverview();
      this.resetDetailView();
    }
  }

  loadStudentsOverview(): void {
    this.startLoading('overview');
    this.clearErrors();

    const subscription = this.journeyService.getAllStudentsOverview(this.currentParcoursType)
      .subscribe({
        next: (data) => {
          this.studentsOverview = data;
          this.applySorting();
          this.stopLoading('overview');
        },
        error: (err) => {
          this.handleServiceError(err, 'chargement des étudiants');
          this.stopLoading('overview');
        }
      });

    this.subscriptions.add(subscription);
  }

  viewStudentDetail(studentId: number): void {
    this.startLoading('studentDetail');
    this.clearErrors();
    this.resetDetailView();

    const subscription = this.journeyService.getStudentJourneyDetail(studentId)
      .subscribe({
        next: (data) => {
          this.selectedStudentJourney = data;
          this.stopLoading('studentDetail');
          this.viewStudentChapters(studentId);
        },
        error: (err) => {
          this.handleServiceError(err, `chargement du parcours de l'étudiant ${studentId}`);
          this.stopLoading('studentDetail');
        }
      });

    this.subscriptions.add(subscription);
  }

  viewStudentChapters(studentId: number): void {
    if (!studentId) return;

    this.startLoading('chapters');
    this.activeDetailTab = 'liste';
    this.showChapitresDetail = true;

    const subscription = this.journeyService.getStudentChaptersProgress(studentId, this.currentParcoursType)
      .subscribe({
        next: (chapitres) => {
          this.chapitresEtudiant = chapitres;
          this.stopLoading('chapters');
        },
        error: (err) => {
          console.error('Erreur lors du chargement des chapitres:', err);
          this.chapitresEtudiant = [];
          this.stopLoading('chapters');
        }
      });

    this.subscriptions.add(subscription);
  }

  viewStudentChaptersGrouped(studentId: number): void {
    if (!studentId) return;

    this.startLoading('groupedChapters');
    this.activeDetailTab = 'matieres';
    this.showChapitresDetail = true;

    const subscription = this.journeyService.getStudentChaptersGroupedByMatiere(studentId, this.currentParcoursType)
      .subscribe({
        next: (grouped) => {
          this.chapitresGroupesParMatiere = grouped;
          this.stopLoading('groupedChapters');
        },
        error: (err) => {
          console.error('Erreur lors du chargement des chapitres groupés:', err);
          this.chapitresGroupesParMatiere = {};
          this.stopLoading('groupedChapters');
        }
      });

    this.subscriptions.add(subscription);
  }

  private handleServiceError(error: any, context: string): void {
    console.error(`Erreur dans ${context}:`, error);

    if (error.status === 0 || error.status === 504) {
      this.errorMessage = 'Problème de connexion au serveur. Vérifiez votre réseau internet.';
      this.errorType = 'network';
    } else if (error.status === 401) {
      this.errorMessage = 'Votre session a expiré. Veuillez vous reconnecter.';
      this.errorType = 'auth';
    } else if (error.status === 403) {
      this.errorMessage = 'Vous n\'avez pas les permissions nécessaires pour accéder à cette ressource.';
      this.errorType = 'auth';
    } else if (error.status === 404) {
      this.errorMessage = 'Les données demandées sont introuvables.';
      this.errorType = 'not_found';
    } else if (error.status >= 500) {
      this.errorMessage = 'Le serveur rencontre des difficultés. Veuillez réessayer dans quelques instants.';
      this.errorType = 'server';
    } else {
      this.errorMessage = `Une erreur est survenue lors du ${context}.`;
      this.errorType = 'generic';
    }
  }

  retryLoad(): void {
    this.clearErrors();
    
    if (this.selectedStudentJourney) {
      this.viewStudentDetail(this.selectedStudentJourney.id);
    } else {
      this.loadStudentsOverview();
    }
  }

  private clearErrors(): void {
    this.errorMessage = null;
    this.errorType = null;
  }

  private startLoading(type: keyof typeof this.loadingStates): void {
    this.loadingStates[type] = true;
    this.currentLoadingMessage = this.loadingMessages[type];
  }

  private stopLoading(type: keyof typeof this.loadingStates): void {
    this.loadingStates[type] = false;
    
    const isLoading = Object.values(this.loadingStates).some(state => state);
    if (!isLoading) {
      this.currentLoadingMessage = '';
    }
  }

  get isLoading(): boolean {
    return Object.values(this.loadingStates).some(state => state);
  }

  sortBy(field: string): void {
    if (this.sortConfig.field === field) {
      this.sortConfig.direction = this.sortConfig.direction === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortConfig.field = field;
      this.sortConfig.direction = 'asc';
    }
    
    this.applySorting();
  }

  private applySorting(): void {
    if (!this.studentsOverview.length) return;

    this.studentsOverview.sort((a, b) => {
      const aValue = (a as any)[this.sortConfig.field];
      const bValue = (b as any)[this.sortConfig.field];
      
      if (aValue == null && bValue == null) return 0;
      if (aValue == null) return 1;
      if (bValue == null) return -1;
      
      if (typeof aValue === 'number' && typeof bValue === 'number') {
        return this.sortConfig.direction === 'asc' ? aValue - bValue : bValue - aValue;
      }
      
      const aString = String(aValue).toLowerCase();
      const bString = String(bValue).toLowerCase();
      
      if (aString < bString) return this.sortConfig.direction === 'asc' ? -1 : 1;
      if (aString > bString) return this.sortConfig.direction === 'asc' ? 1 : -1;
      return 0;
    });
  }

  changeDetailTab(tab: 'liste' | 'matieres' | 'stats'): void {
    this.activeDetailTab = tab;
    
    if (tab === 'matieres' && this.selectedStudentJourney && Object.keys(this.chapitresGroupesParMatiere).length === 0) {
      this.viewStudentChaptersGrouped(this.selectedStudentJourney.id);
    }
  }

  closeDetailView(): void {
    this.selectedStudentJourney = null;
    this.chapitresEtudiant = [];
    this.chapitresGroupesParMatiere = {};
    this.showChapitresDetail = false;
    this.activeDetailTab = 'liste';
  }

  resetDetailView(): void {
    this.closeDetailView();
  }

  getStudentStats(): StudentStats | null {
    if (!this.chapitresEtudiant.length) return null;

    const scores = this.chapitresEtudiant.map(c => c.score || 0);
    const tempsTotal = this.chapitresEtudiant.reduce((sum, c) => sum + (c.tempsPasse || 0), 0);
    const chapitresTermines = this.chapitresEtudiant.filter(c => c.statut === 'TERMINE').length;

    return {
      scoreMoyen: scores.reduce((sum, score) => sum + score, 0) / scores.length,
      chapitresTermines,
      tempsTotal,
      meilleurScore: Math.max(...scores),
      pireScore: Math.min(...scores)
    };
  }

  formatTemps(minutes: number | undefined): string {
    if (!minutes || minutes <= 0) return '0 min';
    
    const heures = Math.floor(minutes / 60);
    const mins = minutes % 60;
    
    if (heures > 0) {
      return `${heures}h ${mins}min`;
    }
    return `${mins} min`;
  }

  formatDate(date: Date | string | undefined): string {
    if (!date) return 'Non disponible';
    
    try {
      const dateObj = new Date(date);
      if (isNaN(dateObj.getTime())) return 'Date invalide';
      
      const now = new Date();
      const diffMs = now.getTime() - dateObj.getTime();
      const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
      
      if (diffDays === 0) {
        const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
        if (diffHours < 1) return "À l'instant";
        if (diffHours === 1) return 'Il y a 1 heure';
        return `Il y a ${diffHours} heures`;
      }
      if (diffDays === 1) return 'Hier';
      if (diffDays < 7) return `Il y a ${diffDays} jours`;
      
      return dateObj.toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: 'short',
        year: 'numeric'
      });
    } catch {
      return 'Date invalide';
    }
  }

  needsAttention(student: StudentJourney): boolean {
    return (
      (student.scoreGlobal || 0) < this.SCORE_THRESHOLDS.MOYEN ||
      (student.pourcentageCompletion || 0) < 30 ||
      this.isInactive(student.dateDerniereActivite)
    );
  }

  // CORRECTION : Rendue publique (pas de modificateur private)
  isInactive(lastActivity?: Date | string): boolean {
    if (!lastActivity) return true;
    
    try {
      const last = new Date(lastActivity);
      if (isNaN(last.getTime())) return true;
      
      const now = new Date();
      const diffDays = Math.floor((now.getTime() - last.getTime()) / (1000 * 3600 * 24));
      return diffDays > 7;
    } catch {
      return true;
    }
  }

  getCouleurScore(score: number | undefined): string {
    const scoreValue = score || 0;
    if (scoreValue >= this.SCORE_THRESHOLDS.EXCELLENT) return 'success';
    if (scoreValue >= this.SCORE_THRESHOLDS.BON) return 'warning';
    if (scoreValue >= this.SCORE_THRESHOLDS.MOYEN) return 'info';
    return 'danger';
  }

  getCouleurParcours(type: string): string {
    const parcours = this.parcoursTypes.find(p => p.value === type);
    return parcours?.color || 'secondary';
  }

  getIconeParcours(type: string): string {
    const parcours = this.parcoursTypes.find(p => p.value === type);
    return parcours?.icon || '📋';
  }

  getObjectKeys(obj: any): string[] {
    if (!obj || typeof obj !== 'object') return [];
    return Object.keys(obj);
  }

  getMatiereFromChapitre(chapitre: ChapitreProgress): string {
    if (!chapitre?.chapitreNom) return 'Non spécifiée';
    
    const separators = [' - ', ' | ', ' : ', ' / '];
    
    for (const separator of separators) {
      if (chapitre.chapitreNom.includes(separator)) {
        return chapitre.chapitreNom.split(separator)[0].trim();
      }
    }
    
    const words = chapitre.chapitreNom.split(' ');
    return words.length > 2 ? words.slice(0, 2).join(' ') : chapitre.chapitreNom;
  }

  exportToCSV(): void {
    if (!this.studentsOverview.length) return;

    const headers = ['Nom', 'Prénom', 'Email', 'Type Parcours', 'Score Global', 'Progression', 'Temps Passé', 'Dernière Activité'];
    
    const csvData = this.studentsOverview.map(student => [
      `"${student.nom}"`,
      `"${student.prenom}"`,
      `"${student.email || ''}"`,
      student.parcoursType,
      `${student.scoreGlobal || 0}%`,
      `${student.pourcentageCompletion || 0}%`,
      this.formatTemps(student.tempsTotalPasse || 0),
      this.formatDate(student.dateDerniereActivite)
    ]);
    
    const csvContent = [
      headers.join(','),
      ...csvData.map(row => row.join(','))
    ].join('\n');
    
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `etudiants_${this.currentParcoursType}_${new Date().toISOString().split('T')[0]}.csv`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  }

  getErrorIcon(): string {
    switch (this.errorType) {
      case 'network': return '🌐';
      case 'auth': return '🔒';
      case 'server': return '🚨';
      case 'not_found': return '🔍';
      case 'generic': return '⚠️';
      default: return '❓';
    }
  }

  isLoadingType(type: keyof typeof this.loadingStates): boolean {
    return this.loadingStates[type];
  }

  get hasData(): boolean {
    return this.studentsOverview.length > 0;
  }

  get hasChapters(): boolean {
    return this.chapitresEtudiant.length > 0;
  }

  get hasGroupedChapters(): boolean {
    return Object.keys(this.chapitresGroupesParMatiere).length > 0;
  }
}