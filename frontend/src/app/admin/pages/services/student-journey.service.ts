import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, shareReplay } from 'rxjs';
import { StudentJourney } from '../models/student-journey.model';
import { ChapitreProgress } from '../models/chapitre-progress.model';

@Injectable({
  providedIn: 'root'
})
export class StudentJourneyService {
  private readonly BASE_URL = 'http://localhost:8080/api';
  private readonly CACHE_DURATION = 5 * 60 * 1000; // 5 minutes en millisecondes
  
  // Cache simple avec timestamp
  private cache = new Map<string, { data: Observable<any>, timestamp: number }>();

  constructor(private http: HttpClient) { }

  // Méthode utilitaire pour construire les params
  private buildParams(params?: { [key: string]: string }): HttpParams {
    let httpParams = new HttpParams();
    
    if (params) {
      Object.keys(params).forEach(key => {
        const value = params[key];
        if (value !== null && value !== undefined && value !== '') {
          httpParams = httpParams.set(key, value);
        }
      });
    }
    
    return httpParams;
  }

  // Méthode générique pour gérer le cache
  private getCached<T>(key: string, requestFn: () => Observable<T>): Observable<T> {
    const now = Date.now();
    const cached = this.cache.get(key);
    
    // Vérifier si le cache est valide
    if (!cached || (now - cached.timestamp) > this.CACHE_DURATION) {
      // Créer une nouvelle requête avec cache
      const request = requestFn().pipe(
        shareReplay(1) // Cache la dernière valeur
      );
      
      this.cache.set(key, { data: request, timestamp: now });
    }
    
    return this.cache.get(key)!.data;
  }

  // Récupère la liste des étudiants filtrée par type de parcours.
  getAllStudentsOverview(parcoursType?: string): Observable<StudentJourney[]> {
    const cacheKey = `overview_${parcoursType || 'all'}`;
    
    return this.getCached(cacheKey, () => {
      const params = this.buildParams(parcoursType ? { type: parcoursType } : undefined);
      
      return this.http.get<StudentJourney[]>(
        `${this.BASE_URL}/student-journey/all`,
        { params }
      );
    });
  }

  // Récupère le détail du parcours d'un étudiant spécifique.
  getStudentJourneyDetail(studentId: number): Observable<StudentJourney> {
    const cacheKey = `detail_${studentId}`;
    
    return this.getCached(cacheKey, () => {
      return this.http.get<StudentJourney>(
        `${this.BASE_URL}/student-journey/${studentId}`
      );
    });
  }

  // Récupère la progression par chapitre d'un étudiant
  getStudentChaptersProgress(
    studentId: number, 
    parcoursType?: string
  ): Observable<ChapitreProgress[]> {
    const cacheKey = `chapters_${studentId}_${parcoursType || 'all'}`;
    
    return this.getCached(cacheKey, () => {
      const params = this.buildParams(parcoursType ? { parcoursType } : undefined);
      
      return this.http.get<ChapitreProgress[]>(
        `${this.BASE_URL}/progression/etudiant/${studentId}/chapitres`,
        { params }
      );
    });
  }

  // Récupère la progression groupée par matière
  getStudentChaptersGroupedByMatiere(
    studentId: number, 
    parcoursType?: string
  ): Observable<{ [key: string]: ChapitreProgress[] }> {
    const cacheKey = `grouped_${studentId}_${parcoursType || 'all'}`;
    
    return this.getCached(cacheKey, () => {
      const params = this.buildParams(parcoursType ? { parcoursType } : undefined);
      
      return this.http.get<{ [key: string]: ChapitreProgress[] }>(
        `${this.BASE_URL}/progression/etudiant/${studentId}/chapitres-groupes`,
        { params }
      );
    });
  }

  // Nettoyer le cache manuellement
  clearCache(key?: string): void {
    if (key) {
      this.cache.delete(key);
    } else {
      this.cache.clear();
    }
  }

  // Nettoyer le cache pour un étudiant spécifique
  clearStudentCache(studentId: number): void {
    const keysToDelete: string[] = [];
    
    this.cache.forEach((value, key) => {
      if (key.includes(`_${studentId}_`) || key === `detail_${studentId}`) {
        keysToDelete.push(key);
      }
    });
    
    keysToDelete.forEach(key => this.cache.delete(key));
  }
}