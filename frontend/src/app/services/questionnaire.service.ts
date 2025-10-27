import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Structure pour la création manuelle
export interface QuestionnairePayload {
  titre: string;
  chapitreId: number;
  duree?: number;
  description: string;
  questions: any[];
}

// Structure pour la génération automatique
export interface ParametresGeneration {
  titre: string;
  duree?: number;
  nombreQuestions: number;
  chapitresIds: number[];
}

// NOUVELLE INTERFACE: Correspond au QuestionnaireDetailDto du back-end
export interface QuestionnaireDetail {
  id: number;
  titre: string;
  duree: number;
  description: string;
  nomChapitre: string;
  nomMatiere: string;
  nombreQuestions: number;
}

@Injectable({
  providedIn: 'root'
} )
export class QuestionnaireService {
  private apiUrl = 'http://localhost:8080/api/questionnaires';

  constructor(private http: HttpClient ) {}

  /**
   * Récupère la liste de tous les questionnaires.
   * Renvoie un tableau de questionnaires détaillés.
   */
  getQuestionnaires(): Observable<QuestionnaireDetail[]> {
    return this.http.get<QuestionnaireDetail[]>(this.apiUrl );
  }

  /**
   * Crée un questionnaire manuellement. Ne renvoie pas de contenu.
   */
  sauvegarderQuestionnaire(questionnaire: QuestionnairePayload): Observable<void> {
    return this.http.post<void>(this.apiUrl, questionnaire );
  }

  /**
   * Crée un questionnaire automatiquement.
   * Renvoie le questionnaire détaillé qui a été généré.
   */
  genererQuestionnaireAutomatique(params: ParametresGeneration): Observable<QuestionnaireDetail> {
    const url = `${this.apiUrl}/generer-depuis-banque`;
    return this.http.post<QuestionnaireDetail>(url, params );
  }

  /**
   * Supprime un questionnaire par son ID. Ne renvoie pas de contenu.
   */
  supprimerQuestionnaire(id: number): Observable<void> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<void>(url );
  }
}
