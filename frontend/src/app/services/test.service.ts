// Fichier : src/app/services/test.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Question, ResultatTest, CreateTestRequest, Questionnaire } from '../models/models';
import { environment } from '../../environments/environment';
// ====================================================================
// === NOUVELLE INTERFACE POUR L'HISTORIQUE DES RÉSULTATS           ===
// ====================================================================
/**
 * Représente un seul élément dans la liste de l'historique des résultats.
 * Doit correspondre au DTO `HistoriqueResultatDto.java` du backend.
 */
export interface HistoriqueResultat {
  nomChapitre: string;
  dateSoumission: string; // Les dates sont souvent transmises comme des chaînes de caractères (ISO)
  scoreObtenu: number;
  scoreTotalPossible: number;
  pourcentage: number;
}

@Injectable({
  providedIn: 'root'
})
export class TestService {

  // private apiUrl = 'http://localhost:8080/api/tests';
   private apiUrl =  `${environment.apiUrl}/tests`;

  constructor(private http: HttpClient) { }

  // ====================================================================
  // === MÉTHODES EXISTANTES                                           ===
  // ====================================================================

  getQuestionsPourChapitre(chapitreId: number): Observable<Question[]> {
    console.log(`[TestService] Appel API pour récupérer les questions du chapitre ${chapitreId}`);
    return this.http.get<Question[]>(`${this.apiUrl}/chapitre/${chapitreId}/questions`);
  }

  soumettreReponses(chapitreId: number, reponses: any): Observable<ResultatTest> {
    console.log(`[TestService] Appel API pour soumettre les réponses du chapitre ${chapitreId}`);
    return this.http.post<ResultatTest>(`${this.apiUrl}/chapitre/${chapitreId}/soumettre`, reponses);
  }

  // ====================================================================
  // === NOUVELLE MÉTHODE POUR CRÉER UN TEST                          ===
  // ====================================================================
  /**
   * Crée un nouveau test à partir des données fournies.
   * @param request Objet contenant les infos du test (CreateTestRequest).
   * @returns Un Observable du test créé.
   */
  createTest(request: CreateTestRequest): Observable<any> {
    console.log(`[TestService] Appel API pour créer un test :`, request);
    return this.http.post<any>(`${this.apiUrl}`, request);
  }

  // ====================================================================
  // === NOUVELLE MÉTHODE POUR L'HISTORIQUE DES RÉSULTATS             ===
  // ====================================================================
  getMonHistorique(): Observable<HistoriqueResultat[]> {
    console.log(`[TestService] Appel API pour récupérer l'historique des résultats.`);
    return this.http.get<HistoriqueResultat[]>(`${this.apiUrl}/mon-historique`);
  }
// Récupérer les questionnaires disponibles pour un chapitre
getQuestionnairesDisponibles(chapitreId: number): Observable<Questionnaire[]> {
  // Correction ici : QuestionnaireManuel[] au lieu de Questionnaire[]
  return this.http.get<Questionnaire[]>(`${this.apiUrl}/chapitre/${chapitreId}/choix-questionnaires`);
}

// Assigner un questionnaire au test
assignerQuestionnaire(chapitreId: number, questionnaireId: number): Observable<any> {
  return this.http.post(`${this.apiUrl}/chapitre/${chapitreId}/assigner/${questionnaireId}`, {});
}

 
}
