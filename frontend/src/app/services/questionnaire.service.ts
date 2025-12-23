import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// ============================================
// INTERFACES CORRIGÉES (alignées avec le backend)
// ============================================

export interface Reponse {
  id: number;
  texte: string;
  correcte: boolean;
}

export interface Question {
  id: number;
  enonce: string;
  type: 'QCM' | 'QCU' | 'VRAI_FAUX' | 'TEXTE_LIBRE';
  points: number;
  reponses: Reponse[];
}

export interface QuestionnaireDetail {
  id: number;
  titre: string;
  description: string;
  duree: number;
  questions: Question[];
  dateCreation?: string;
  chapitreId?: number;
  matiereId?: number;
  nomChapitre?: string;
  nomMatiere?: string;
  nombreQuestions?: number;
}

export interface Test {
  id: number;
  titre: string;
  duree?: number;
  description?: string;
}

export interface QuestionnairePayload {
  titre: string;
  chapitreId: number;
  duree?: number;
  description: string;
  questions: any[];
}

export interface ParametresGeneration {
  titre: string;
  duree?: number;
  nombreQuestions: number;
  chapitresIds: number[];
}

// ============================================
// NOUVELLE INTERFACE POUR LA GÉNÉRATION DEPUIS BANQUE
// ============================================

export interface GenerationRequestDto {
  themes: string[];
  nombreQuestions: number;
  niveau: string;
}

// ============================================
// SERVICE COMPLET AVEC NOUVELLE MÉTHODE
// ============================================

@Injectable({
  providedIn: 'root'
})
export class QuestionnaireService {
  private apiUrl = 'http://localhost:8080/api/questionnaires';

  constructor(private http: HttpClient) {}

  // ============================================
  // QUESTIONNAIRES
  // ============================================

  getQuestionnaires(): Observable<QuestionnaireDetail[]> {
    return this.http.get<QuestionnaireDetail[]>(this.apiUrl);
  }

  getQuestionnaireById(id: number): Observable<QuestionnaireDetail> {
    return this.http.get<QuestionnaireDetail>(`${this.apiUrl}/${id}`);
  }

  getQuestionnaireDetails(id: number): Observable<QuestionnaireDetail> {
    console.warn('⚠️ Méthode dépréciée: utilisez getQuestionnaireById() qui inclut les questions');
    return this.getQuestionnaireById(id);
  }

  getQuestionsByQuestionnaireId(id: number): Observable<Question[]> {
    console.warn('⚠️ Méthode dépréciée: les questions sont déjà incluses dans getQuestionnaireById()');
    return new Observable<Question[]>(observer => {
      observer.next([]);
      observer.complete();
    });
  }

  sauvegarderQuestionnaire(questionnaire: QuestionnairePayload): Observable<void> {
    return this.http.post<void>(this.apiUrl, questionnaire);
  }

  // ============================================
  // GÉNÉRATION DE QUESTIONNAIRE - NOUVELLE MÉTHODE
  // ============================================

  genererQuestionnaireDepuisBanque(params: GenerationRequestDto): Observable<QuestionnaireDetail> {
    const url = `${this.apiUrl}/generer-depuis-banque`;
    console.log('📤 Envoi de la requête de génération:', params);
    return this.http.post<QuestionnaireDetail>(url, params);
  }

  genererQuestionnaireAutomatique(params: ParametresGeneration): Observable<QuestionnaireDetail> {
    console.warn('⚠️ Méthode dépréciée: utilisez genererQuestionnaireDepuisBanque()');
    
    const generationRequest: GenerationRequestDto = {
      themes: params.chapitresIds.map(id => `Chapitre ${id}`),
      nombreQuestions: params.nombreQuestions,
      niveau: 'INTERMEDIAIRE'
    };
    
    return this.genererQuestionnaireDepuisBanque(generationRequest);
  }

  supprimerQuestionnaire(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // ============================================
  // TESTS ASSOCIÉS
  // ============================================

  getTestsByQuestionnaire(questionnaireId: number): Observable<Test[]> {
    const url = `${this.apiUrl}/${questionnaireId}/tests`;
    return this.http.get<Test[]>(url);
  }

  createTest(questionnaireId: number, test: Partial<Test>): Observable<Test> {
    const url = `${this.apiUrl}/${questionnaireId}/tests`;
    return this.http.post<Test>(url, test);
  }

  updateTest(testId: number, test: Partial<Test>): Observable<Test> {
    const url = `${this.apiUrl}/tests/${testId}`;
    return this.http.put<Test>(url, test);
  }

  deleteTest(testId: number): Observable<void> {
    const url = `${this.apiUrl}/tests/${testId}`;
    return this.http.delete<void>(url);
  }

  // ============================================
  // MÉTHODES UTILITAIRES
  // ============================================

  debugQuestionnaireData(data: QuestionnaireDetail): void {
    console.log('🔍 DEBUG - Structure des données:', {
      id: data.id,
      titre: data.titre,
      duree: data.duree,
      hasQuestions: !!data.questions,
      questionsCount: data.questions ? data.questions.length : 0,
      firstQuestion: data.questions && data.questions.length > 0 ? {
        enonce: data.questions[0].enonce,
        type: data.questions[0].type,
        reponsesCount: data.questions[0].reponses.length
      } : null
    });
  }
}