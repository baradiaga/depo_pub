// src/app/admin/pages/models/course-progress.model.ts
export interface CourseProgressDto {
  courseId: number;
  courseCode: string;
  courseName: string;
  scoreMoyen: number;        // Score moyen aux tests de ce cours
  testsPasses: number;
  statutRecommandation: string; // "Faible", "Moyen", "Bonne maîtrise"
}
