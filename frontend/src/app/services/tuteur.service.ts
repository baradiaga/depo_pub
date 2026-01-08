import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
// Importez vos interfaces Utilisateur et Inscription si elles existent
import { Utilisateur, Inscription } from '../models/models'; 

@Injectable({
  providedIn: 'root'
})
export class TuteurService {
  private apiUrl = `${environment.apiUrl}/tuteur`;

  constructor(private http: HttpClient) { }

  /**
   * Récupère la liste des étudiants sous la responsabilité du tuteur
   */
  getMesEtudiants(tuteurId: number): Observable<Utilisateur[]> {
    return this.http.get<Utilisateur[]>(`${this.apiUrl}/${tuteurId}/etudiants`);
  }

  /**
   * Récupère le détail des inscriptions (pour voir les statuts et les matières)
   */
  getSuiviInscriptions(tuteurId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${tuteurId}/details-suivi`);
  }

  /**
   * Valide l'inscription d'un étudiant
   */
  validerInscription(inscriptionId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/inscription/${inscriptionId}/valider`, {});
  }
}
