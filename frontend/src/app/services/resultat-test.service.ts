// Fichier : src/app/services/resultat-test.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
/** 
 * Ce service est dédié à la soumission des réponses et à la gestion des scores.
 * En 2025, nous utilisons le point d'entrée unique défini dans le backend : /api/tests
 */
@Injectable({
  providedIn: 'root'
})
export class ResultatTestService {

  // CORRECTION : L'URL doit correspondre au @RequestMapping("/api/tests") de votre TestController.java
  // private apiUrl = 'http://localhost:8080/api/tests';
  private apiUrl =  `${environment.apiUrl}/tests`;

  constructor(private http: HttpClient) { }

  /**
   * Soumet les réponses d'un étudiant pour un chapitre donné.
   * Cette méthode corrige l'erreur 404 en utilisant la bonne structure d'URL.
   */
  soumettreResultat(chapitreId: number, reponses: any): Observable<any> {
    // CORRECTION : On remplace le ":" par "/" et on utilise le bon chemin backend
    // Résultat final attendu : http://localhost:8080/api/tests/chapitre/9/soumettre
    const url = `${this.apiUrl}/chapitre/${chapitreId}/soumettre`;
    
    console.log(`[ResultatService] Envoi vers : ${url}`);
    
    // On envoie l'objet des réponses au backend via POST
    return this.http.post<any>(url, reponses);
  }
// src/app/services/resultat-test.service.ts

// resultat-test.service.ts
verifierEntrainement(questionnaireId: number, reponses: any): Observable<any> {
  // On change le segment d'URL de 'chapitre' vers 'questionnaire'
  return this.http.post<any>(`${environment.apiUrl}/tests/questionnaire/${questionnaireId}/entrainement`, reponses);
}


}
