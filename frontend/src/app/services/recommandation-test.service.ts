// Fichier : src/app/services/recommandation-test.service.ts (Version Finale et Complète)

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';


import { Question } from '../models/models';
import { environment } from '../../environments/environment';

export interface ChapitreTest {
  id: number;
  numero: string;
  nom: string;
  resultat?: number;
  categorie?: string;
  actionValidee: boolean;
}

export interface ElementConstitutif {
  id: number;
  code: string;
  nom: string;
  credit: number;
  description: string;
  chapitres: ChapitreTest[];
  enseignant?: { id: number; nom: string; prenom: string; };
}

export interface MatiereSelection {
  id: number;
  nom: string;
}

// --- NOUVELLES INTERFACES POUR LE DIAGNOSTIC ---
export interface OptionDiagnostic {
  id: number;
  texte: string;
}

export interface QuestionDiagnostic {
  id: number;
  enonce: string;
  chapitreId: number;
  typeQuestion: string;
  options: OptionDiagnostic[];
}


export interface ReponseSoumise {
  questionId: number;
  reponse: number | number[] | string | boolean; // Le type de réponse peut varier
}


@Injectable({
  providedIn: 'root'
})
export class RecommandationTestService {

  // private baseUrl = 'http://localhost:8080/api';
  private apiUrl =  `${environment.apiUrl}`;

  constructor(private http: HttpClient ) { }

  

  getListeMatieres(): Observable<MatiereSelection[]> {
    return this.http.get<ElementConstitutif[]>(`${this.apiUrl}/elements-constitutifs/mes-matieres` ).pipe(
      map(elements =>
        elements.map(ec => ({
          id: ec.id,
          nom: ec.nom
        }))
      )
    );
  }

  getDetailsEC(ecId: number): Observable<ElementConstitutif | undefined> {
    if (!ecId) {
      return of(undefined);
    }
    return this.http.get<ElementConstitutif>(`${this.apiUrl}/elements-constitutifs/${ecId}` );
  }

  getQuestionsPourTestDeConnaissance(matiereId: number): Observable<Question[]> {
    return this.http.get<Question[]>(`${this.apiUrl}/tests/connaissance/${matiereId}` );
  }

  // --- MÉTHODES POUR LE TEST DE DIAGNOSTIC ---

  genererTestDiagnostic(matiereId: number): Observable<QuestionDiagnostic[]> {
    return this.http.get<QuestionDiagnostic[]>(`${this.apiUrl}/diagnostic/generer-test/${matiereId}` );
  }

  
soumettreTestDiagnostic(soumission: { reponses: ReponseSoumise[] }): Observable<any> {
    // On envoie DIRECTEMENT le paramètre 'soumission' au backend.
    // Il a déjà la bonne structure { reponses: [...] }.
    return this.http.post<any>(`${this.apiUrl}/diagnostic/corriger-test`, soumission );
  }
}
