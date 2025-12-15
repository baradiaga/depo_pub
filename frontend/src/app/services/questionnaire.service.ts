import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// ============================================
// INTERFACES CORRIGÉES (alignées avec le backend)
// ============================================

export interface Reponse {
  id: number;
  texte: string;
  correcte: boolean;  // 🔥 Retiré le ? car toujours présent
}

export interface Question {
  id: number;
  enonce: string;
  type: 'QCM' | 'QCU' | 'VRAI_FAUX' | 'TEXTE_LIBRE';
  points: number;
  reponses: Reponse[];
}

// 🔥 INTERFACE CORRIGÉE - Alignée avec QuestionnaireDetailDto du backend
// Dans questionnaire.service.ts - MODIFIEZ l'interface
export interface QuestionnaireDetail {
  id: number;
  titre: string;
  description: string;
  duree: number;
  questions: Question[];
  
  // ⚡ RENDEZ CES CHAMPS OPTIONNELS avec ?
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
// SERVICE CORRIGÉ
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

  /**
   * Récupère la liste de tous les questionnaires.
   * Le backend retourne maintenant les questions incluses
   */
  getQuestionnaires(): Observable<QuestionnaireDetail[]> {
    return this.http.get<QuestionnaireDetail[]>(this.apiUrl);
  }

  /**
   * Récupère les détails d’un questionnaire par ID (INCLUT les questions)
   * 🔥 C'est la méthode principale à utiliser
   */
  getQuestionnaireById(id: number): Observable<QuestionnaireDetail> {
    return this.http.get<QuestionnaireDetail>(`${this.apiUrl}/${id}`);
  }

  /**
   * @deprecated - Utilisez plutôt getQuestionnaireById() qui inclut déjà les questions
   * Gardé pour compatibilité si d'autres composants l'utilisent
   */
  getQuestionnaireDetails(id: number): Observable<QuestionnaireDetail> {
    console.warn('⚠️ Méthode dépréciée: utilisez getQuestionnaireById() qui inclut les questions');
    return this.getQuestionnaireById(id);
  }

  /**
   * @deprecated - PLUS NÉCESSAIRE car les questions sont déjà dans getQuestionnaireById()
   * Le backend retourne une erreur 404 car cet endpoint n'existe probablement pas
   */
  getQuestionsByQuestionnaireId(id: number): Observable<Question[]> {
    console.warn('⚠️ Méthode dépréciée: les questions sont déjà incluses dans getQuestionnaireById()');
    
    // Option 1: Retourner un observable vide (si vous ne voulez pas casser le code existant)
    return new Observable<Question[]>(observer => {
      observer.next([]);
      observer.complete();
    });
    
    // Option 2: Lever une erreur pour forcer la migration
    // throw new Error('Cette méthode est obsolète. Utilisez getQuestionnaireById()');
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
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // ============================================
  // TESTS ASSOCIÉS
  // ============================================

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

  // ============================================
  // MÉTHODES UTILITAIRES
  // ============================================

  /**
   * Vérifie la structure des données reçues (pour debug)
   */
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