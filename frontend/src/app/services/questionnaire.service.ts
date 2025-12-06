import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Interface pour un Test
export interface Test {
  id: number;
  titre: string;
  duree?: number;
  description?: string;
}
// Structure pour la création manuelle
export interface QuestionnairePayload {
  titre: string;
  chapitreId: number;
  duree?: number;
  description: string;
  questions: any[];
}
// Interface correspondant au QuestionnaireDetailDto du back-end
export interface QuestionnaireDetail {
  id: number;
  titre: string;
  description: string;
  dateCreation: string;     // correspond à LocalDateTime côté backend
  chapitreId: number | null; 
  matiereId: number | null;
  nomChapitre?: string;
  nomMatiere?: string;
  nombreQuestions?: number;
  duree?: number;
}



// Structure pour la génération automatique
export interface ParametresGeneration {
  titre: string;
  duree?: number;
  nombreQuestions: number;
  chapitresIds: number[];
}



@Injectable({
  providedIn: 'root'
})
export class QuestionnaireService {
  private apiUrl = 'http://localhost:8080/api/questionnaires';

  constructor(private http: HttpClient) {}

  /**
   * Récupère la liste de tous les questionnaires.
   */
  getQuestionnaires(): Observable<QuestionnaireDetail[]> {
    return this.http.get<QuestionnaireDetail[]>(this.apiUrl);
  }

  /**
   * Crée un questionnaire manuellement.
   */
  sauvegarderQuestionnaire(questionnaire: QuestionnairePayload): Observable<void> {
    return this.http.post<void>(this.apiUrl, questionnaire);
  }

  /**
   * Génère un questionnaire automatiquement.
   */
  genererQuestionnaireAutomatique(params: ParametresGeneration): Observable<QuestionnaireDetail> {
    const url = `${this.apiUrl}/generer-depuis-banque`;
    return this.http.post<QuestionnaireDetail>(url, params);
  }

  /**
   * Supprime un questionnaire par son ID.
   */
  supprimerQuestionnaire(id: number): Observable<void> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<void>(url);
  }

  /**
   * Récupère les détails d’un questionnaire par ID.
   */
  getQuestionnaireDetails(id: number): Observable<QuestionnaireDetail> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<QuestionnaireDetail>(url);
  }

  // ====================================================================
  // === TESTS ASSOCIÉS AU QUESTIONNAIRE                               ===
  // ====================================================================

  /**
   * Récupère les tests associés à un questionnaire.
   */
  getTestsByQuestionnaire(questionnaireId: number): Observable<Test[]> {
    const url = `${this.apiUrl}/${questionnaireId}/tests`;
    return this.http.get<Test[]>(url);
  }

  /**
   * Crée un test lié à un questionnaire.
   */
  createTest(questionnaireId: number, test: Partial<Test>): Observable<Test> {
    const url = `${this.apiUrl}/${questionnaireId}/tests`;
    return this.http.post<Test>(url, test);
  }

  /**
   * Met à jour un test existant.
   */
  updateTest(testId: number, test: Partial<Test>): Observable<Test> {
    const url = `${this.apiUrl}/tests/${testId}`;
    return this.http.put<Test>(url, test);
  }

  /**
   * Supprime un test par son ID.
   */
  deleteTest(testId: number): Observable<void> {
    const url = `${this.apiUrl}/tests/${testId}`;
    return this.http.delete<void>(url);
  }
}
