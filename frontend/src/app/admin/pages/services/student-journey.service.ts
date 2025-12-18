import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StudentJourney } from '../models/student-journey.model';
import { ChapitreProgress } from '../models/chapitre-progress.model';

@Injectable({
  providedIn: 'root'
})
export class StudentJourneyService {
  private apiUrl = 'http://localhost:8080/api/student-journey';
  private progressionApiUrl = 'http://localhost:8080/api/progression';

  constructor(private http: HttpClient) { }

  /**
   * Récupère la liste des étudiants filtrée par type de parcours.
   */
  getAllStudentsOverview(parcoursType?: string): Observable<StudentJourney[]> {
    let params = new HttpParams();
    if (parcoursType) {
      params = params.set('type', parcoursType);
    }
    
    return this.http.get<StudentJourney[]>(`${this.apiUrl}/all`, { params });
  }

  /**
   * Récupère le détail du parcours d'un étudiant spécifique.
   */
  getStudentJourneyDetail(studentId: number): Observable<StudentJourney> {
    return this.http.get<StudentJourney>(`${this.apiUrl}/${studentId}`);
  }

  /**
   * NOUVELLE MÉTHODE: Récupère la progression par chapitre d'un étudiant
   */
  getStudentChaptersProgress(
    studentId: number, 
    parcoursType?: string
  ): Observable<ChapitreProgress[]> {
    let params = new HttpParams();
    if (parcoursType) {
      params = params.set('parcoursType', parcoursType);
    }
    
    return this.http.get<ChapitreProgress[]>(
      `${this.progressionApiUrl}/etudiant/${studentId}/chapitres`,
      { params }
    );
  }

  /**
   * NOUVELLE MÉTHODE: Récupère la progression groupée par matière
   */
  getStudentChaptersGroupedByMatiere(
    studentId: number, 
    parcoursType?: string
  ): Observable<{ [key: string]: ChapitreProgress[] }> {
    let params = new HttpParams();
    if (parcoursType) {
      params = params.set('parcoursType', parcoursType);
    }
    
    return this.http.get<{ [key: string]: ChapitreProgress[] }>(
      `${this.progressionApiUrl}/etudiant/${studentId}/chapitres-groupes`,
      { params }
    );
  }
}