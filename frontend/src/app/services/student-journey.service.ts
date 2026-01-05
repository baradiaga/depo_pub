import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';
import { environment } from '../../environments/environment';

// Interfaces pour correspondre à vos DTOs Backend
// Cherchez cette interface en haut de votre fichier student-journey.service.ts
export interface CourseProgress {
  courseId: number;
  courseCode: string;
  courseName: string;
  testsPasses: number;
  statutRecommandation: string;
  // AJOUTEZ CETTE LIGNE :
  notesDetaillees: number[]; 
}


export interface StudentJourney {
  studentId: number;
  nomComplet: string;
  email: string;
  formationActuelle: string;
  niveauEtude: string;
  parcoursType: string;
  notesDetaillees: number[];
  testsPasses: number;
  progressionParCours: CourseProgress[];
}

@Injectable({
  providedIn: 'root'
})
export class StudentJourneyService {
  private readonly API_URL = `${environment.apiUrl}/front/student-journey`;

  constructor(
    private http: HttpClient,
    private toastr: ToastrService
  ) {}

  /**
   * Récupère le parcours de l'étudiant connecté
   */
  getMine(): Observable<StudentJourney> {
    return this.http.get<StudentJourney>(`${this.API_URL}/me`).pipe(
      catchError(err => this.handleError(err, 'Impossible de charger votre parcours'))
    );
  }

  /**
   * Admin : Récupère tous les parcours (avec filtre optionnel)
   */
  getAll(type?: string): Observable<StudentJourney[]> {
    let params = new HttpParams();
    if (type) params = params.set('type', type);

    return this.http.get<StudentJourney[]>(`${this.API_URL}/all`, { params }).pipe(
      tap(() => this.toastr.info('Liste des parcours chargée', 'Admin')),
      catchError(err => this.handleError(err, 'Erreur lors de la récupération des parcours'))
    );
  }

  /**
   * Enseignant : Récupère les parcours de ses étudiants
   */
  getMyStudents(): Observable<StudentJourney[]> {
    return this.http.get<StudentJourney[]>(`${this.API_URL}/mes-etudiants`).pipe(
      catchError(err => this.handleError(err, 'Erreur lors du chargement de vos étudiants'))
    );
  }

  /**
   * Récupère un parcours spécifique par ID
   */
  getById(studentId: number): Observable<StudentJourney> {
    return this.http.get<StudentJourney>(`${this.API_URL}/${studentId}`).pipe(
      catchError(err => this.handleError(err, `Erreur pour l'étudiant ${studentId}`))
    );
  }

  /**
   * Gestion centralisée des erreurs avec Toastr
   */
  private handleError(error: any, message: string) {
    console.error(error);
    this.toastr.error(message, 'Erreur Système');
    return throwError(() => new Error(message));
  }
}
