// Interface pour la progression par cours
export interface CourseProgress {
  courseId: number;
  courseCode: string;
  courseName: string;
  scoreMoyen: number;
  testsPasses: number;
  statutRecommandation: string;
}

// Interface principale pour le parcours étudiant
export interface StudentJourney {
  studentId: number;
  nomComplet: string;
  email: string;
  formationActuelle: string;
  niveauEtude: string;
  moyenneGeneraleTests: number;
  testsPasses: number;
  progressionParCours: CourseProgress[];
  parcoursType: 'Recommandé' | 'Mixte' | 'Choisi';
}