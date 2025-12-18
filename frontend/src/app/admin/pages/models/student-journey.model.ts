import { CourseProgressDto } from './course-progress.model';
import { ChapitreProgress } from './chapitre-progress.model';

export interface StudentJourney {
  studentId: number;
  nomComplet: string;
  email: string;
  formationActuelle: string;
  niveauEtude: string;
  moyenneGeneraleTests: number;
  testsPasses: number;
  progressionParCours: CourseProgressDto[];
  parcoursType: 'Recommandé' | 'Mixte' | 'Choisi';
  
  // Optionnel: ajouter les chapitres si backend les fournit
  chapitresProgress?: ChapitreProgress[];
}