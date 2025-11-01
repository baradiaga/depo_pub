import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EtudiantTestsDashboard } from '../models/dashboard.model';

// --- Interfaces ---

export interface ReponseOption { id: number; texte: string; }
export interface Question { id: number; enonce: string; type: 'QCM' | 'QCU' | 'VRAI_FAUX' | 'TEXTE_LIBRE'; points: number; reponses: ReponseOption[]; }

/**
 * Interface pour le résultat du test retourné par le back-end.
 */
export interface ResultatTest {
  chapitreId: number;
  scoreObtenu: number;
  totalPointsPossible: number;
  dateSoumission: string;
}

@Injectable({
  providedIn: 'root'
} )
export class TestService {
  private apiUrl = 'http://localhost:8080/api/tests';
  private etudiantApiUrl = 'http://localhost:8080/api/etudiants';

  constructor(private http: HttpClient ) { }

  /**
   * Récupère les questions pour un chapitre donné.
   */
  getQuestionsPourChapitre(chapitreId: number): Observable<Question[]> {
    const url = `${this.apiUrl}/chapitre/${chapitreId}/questions`;
    return this.http.get<Question[]>(url );
  }

  /**
   * Soumet les réponses de l'utilisateur et attend un objet ResultatTest en retour.
   */
  soumettreReponses(testId: number, reponses: { [key: string]: any }): Observable<ResultatTest> {
    const url = `${this.apiUrl}/${testId}/submit`;
    return this.http.post<ResultatTest>(url, reponses );
  }

  /**
   * Récupère les données structurées pour le tableau de bord des tests de l'étudiant.
   */
  getEtudiantTestsDashboard(): Observable<EtudiantTestsDashboard> {
    const url = `${this.etudiantApiUrl}/dashboard/tests`;
    return this.http.get<EtudiantTestsDashboard>(url);
  }
}
