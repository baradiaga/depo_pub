export interface ChapitreProgress {
  chapitreId: number;
  chapitreNom: string;
  ordre: number;
  scoreMoyen: number;
  parcoursType: 'RECOMMANDE' | 'CHOISI' | 'MIXTE';
  dateDernierTest: string;
  nombreTests: number;
}

export interface StudentCourseWithChapters {
  courseId: number;
  courseCode: string;
  courseName: string;
  scoreMoyen: number;
  chapitres: ChapitreProgress[];
}