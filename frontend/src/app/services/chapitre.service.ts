// Fichier : src/app/services/chapitre.service.ts (Corrigé avec notification)

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators'; // <-- IMPORT AJOUTÉ

// On importe les interfaces depuis le fichier central.
import { Chapitre, ChapitrePayload } from './models';
// On importe le service à notifier.
import { ElementConstitutifService } from './element-constitutif.service'; // <-- IMPORT AJOUTÉ

@Injectable({
  providedIn: 'root'
} )
export class ChapitreService {
  private apiUrl = 'http://localhost:8080/api/chapitres';

  // ====================================================================
  // === CORRECTION APPLIQUÉE ICI                                     ===
  // ====================================================================
  // On injecte ElementConstitutifService pour pouvoir l'appeler.
  constructor(
    private http: HttpClient,
    private ecService: ElementConstitutifService
   ) { }

  /**
   * Crée un nouveau chapitre et notifie les autres composants du changement.
   */
  creerChapitre(payload: ChapitrePayload): Observable<Chapitre> {
    return this.http.post<Chapitre>(this.apiUrl, payload ).pipe(
      // L'opérateur 'tap' permet d'exécuter une action sans modifier la réponse.
      tap(() => {
        // Après la création réussie d'un chapitre, on déclenche le rafraîchissement
        // de la liste des matières, car un nouveau chapitre y a été ajouté.
        console.log("Chapitre créé, envoi de la notification de rafraîchissement...");
        this.ecService.refreshNeeded$.next();
      })
    );
  }

  /**
   * Récupère un chapitre par son ID.
   */
  findById(id: number): Observable<Chapitre> {
    return this.http.get<Chapitre>(`${this.apiUrl}/${id}` );
  }

  /**
   * Trouve un chapitre par le nom de sa matière et son niveau.
   */
  findChapitreByMatiereAndNiveau(matiereNom: string, niveau: number): Observable<Chapitre> {
    const params = new HttpParams()
      .set('matiere', matiereNom)
      .set('niveau', niveau.toString());
    return this.http.get<Chapitre>(`${this.apiUrl}/search`, { params } );
  }

  /**
   * Récupère tous les chapitres pour une matière donnée.
   */
  getChapitresParMatiere(matiereId: number): Observable<Chapitre[]> {
    return this.http.get<Chapitre[]>(`http://localhost:8080/api/elements-constitutifs/${matiereId}/chapitres` );
  }
}
