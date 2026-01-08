import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface CourseProgress {
  courseId: number;
  courseName: string;
  notesDetaillees: number[];
  testsPasses: number;
  statutRecommandation: 'Faible' | 'Moyen' | 'Bonne maîtrise';
}

export interface StudentJourney {
  studentId: number;
  nomComplet: string;
  email: string;
  formationActuelle: string;
  niveauEtude: string;
  moyenneGeneraleTests: number;
  testsPasses: number;
  parcoursType: string;
  progressionParCours: CourseProgress[];
}


@Injectable({
  providedIn: 'root'
})
export class StudentService {
  private readonly API_URL = `${environment.apiUrl}/front/student-journey`;
  private readonly TEST_URL = `${environment.apiUrl}/tests`; // URL pour TestService

  constructor(private http: HttpClient) {}

  // ======================================================
  // 1. CATEGORIE : SUPERVISION & MONITORING
  // ======================================================

  /** Récupère tous les étudiants liés au tuteur connecté */
  getStudentsForTeacher(): Observable<StudentJourney[]> {
    return this.http.get<StudentJourney[]>(`${this.API_URL}/teacher-students`);
  }

  /** Détail complet de la progression d'un étudiant (Scores, status, parcours) */
  getStudentDetail(studentId: number): Observable<StudentJourney> {
    return this.http.get<StudentJourney>(`${this.API_URL}/${studentId}`);
  }

  /** Récupère l'historique chronologique des tests d'un étudiant */
  getStudentTestHistory(studentId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.TEST_URL}/historique/${studentId}`);
  }

  // ======================================================
  // 2. CATEGORIE : INGENIERIE PEDAGOGIQUE
  // ======================================================

  /** Crée un test à partir d'un questionnaire existant */
  createTestFromQuestionnaire(questionnaireId: number): Observable<any> {
    return this.http.post(`${this.TEST_URL}/from-questionnaire/${questionnaireId}`, {});
  }

  /** Crée un test sur mesure en sélectionnant des questions précises */
  createCustomTest(chapitreId: number, titre: string, questionIds: number[]): Observable<any> {
    return this.http.post(`${this.TEST_URL}/custom`, { chapitreId, titre, questionIds });
  }

  /** Récupère les questions disponibles pour un chapitre donné */
  getQuestionsByChapitre(chapitreId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.TEST_URL}/questions-chapitre/${chapitreId}`);
  }

  // ======================================================
  // 3. CATEGORIE : GESTION ET VALIDATION
  // ======================================================

  /** Modifie le type de parcours d'un étudiant (Recommandé, Choisi, Mixte) */
  updateParcoursType(etudiantId: number, nouveauType: string): Observable<void> {
    const params = new HttpParams().set('nouveauType', nouveauType);
    return this.http.put<void>(`${this.API_URL}/${etudiantId}/type`, {}, { params });
  }

  /** Récupère tous les parcours filtrés par type (Vue globale pour admin/tuteur) */
  getAllJourneysByType(type: string): Observable<StudentJourney[]> {
    return this.http.get<StudentJourney[]>(`${this.API_URL}/all?type=${type}`);
  }
}
