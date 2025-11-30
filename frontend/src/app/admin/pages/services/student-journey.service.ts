// Fichier : src/app/services/student-journey.service.ts (Mise à jour)

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StudentJourney } from '../models/student-journey.model';


@Injectable({
  providedIn: 'root'
} )
export class StudentJourneyService {
  private apiUrl = 'http://localhost:8080/api/student-journey';

  constructor(private http: HttpClient ) { }

  /**
   * Récupère la liste des étudiants filtrée par type de parcours.
   * @param parcoursType - Type de parcours à filtrer ('RECOMMANDE', 'CHOISI', 'MIXTE')
   */
  getAllStudentsOverview(parcoursType?: string): Observable<StudentJourney[]> {
    let params = new HttpParams();
    if (parcoursType) {
      params = params.set('type', parcoursType);
    }
    
    return this.http.get<StudentJourney[]>(`${this.apiUrl}/all`, { params } );
  }

  /**
   * Récupère le détail du parcours d'un étudiant spécifique.
   */
  getStudentJourneyDetail(studentId: number): Observable<StudentJourney> {
    return this.http.get<StudentJourney>(`${this.apiUrl}/${studentId}` );
  }
}
