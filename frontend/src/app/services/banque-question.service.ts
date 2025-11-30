// Fichier : src/app/services/banque-question.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BanqueQuestionCreation, BanqueQuestionDetail, EvaluationQuestion } from '../models/models'; // Assurez-vous du bon chemin

@Injectable({
  providedIn: 'root'
} )
export class BanqueQuestionService {

  private apiUrl = 'http://localhost:8080/api/banque-questions';


  constructor(private http: HttpClient ) { }

  // Créer une nouvelle question dans la banque
  creerQuestion(question: BanqueQuestionCreation): Observable<BanqueQuestionDetail> {
    return this.http.post<BanqueQuestionDetail>(this.apiUrl, question );
  }

  // Mettre à jour une question existante
  mettreAJourQuestion(id: number, question: BanqueQuestionCreation): Observable<BanqueQuestionDetail> {
    return this.http.put<BanqueQuestionDetail>(`${this.apiUrl}/${id}`, question );
  }

  // Récupérer une question par ID
  getQuestionById(id: number): Observable<BanqueQuestionDetail> {
    return this.http.get<BanqueQuestionDetail>(`${this.apiUrl}/${id}` );
  }

  // Récupérer toutes les questions (ou utiliser des filtres)
  getAllQuestions(): Observable<BanqueQuestionDetail[]> {
    // TODO: Ajouter des paramètres de recherche/filtrage ici
    return this.http.get<BanqueQuestionDetail[]>(this.apiUrl );
  }

  // Supprimer une question
  supprimerQuestion(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}` );
  }

  // Évaluer une question (si vous implémentez cette fonctionnalité)
  evaluerQuestion(id: number, evaluation: EvaluationQuestion): Observable<any> {
    return this.http.post(`${this.apiUrl}/${id}/evaluer`, evaluation );
  }
}
