// Fichier : src/app/services/element-constitutif.service.ts (Corrigé avec notifications)

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { tap } from 'rxjs/operators';

// On importe les interfaces depuis le fichier central
import { ElementConstitutifRequest, ElementConstitutifResponse } from '../models/models';


@Injectable({
  providedIn: 'root'
} )
export class ElementConstitutifService {
  private apiUrl = 'http://localhost:8080/api/elements-constitutifs';

  // ====================================================================
  // === SYSTÈME DE NOTIFICATION AJOUTÉ ICI                            ===
  // ====================================================================
  private _refreshNeeded$ = new Subject<void>( );

  get refreshNeeded$() {
    return this._refreshNeeded$;
  }
  // ====================================================================

  constructor(private http: HttpClient ) { }

  /**
   * Récupère la liste de base des EC.
   */
  findAll(): Observable<ElementConstitutifResponse[]> {
    return this.http.get<ElementConstitutifResponse[]>(this.apiUrl );
  }

  /**
   * Récupère la liste des EC avec leurs chapitres.
   */
  getElementsConstitutifsAvecDetails(): Observable<ElementConstitutifResponse[]> {
    return this.http.get<ElementConstitutifResponse[]>(`${this.apiUrl}/details` );
  }

  /**
   * Récupère uniquement la liste des noms de tous les EC.
   */
  findAllNoms(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/noms` );
  }

  /**
   * Récupère les matières d'un enseignant.
   */
  getMesMatieres(): Observable<ElementConstitutifResponse[]> {
    return this.http.get<ElementConstitutifResponse[]>(`${this.apiUrl}/mes-matieres` );
  }

  /**
   * Crée un nouvel EC et notifie les composants.
   */
  create(ueId: number, payload: ElementConstitutifRequest): Observable<ElementConstitutifResponse> {
    return this.http.post<ElementConstitutifResponse>(`http://localhost:8080/api/unites-enseignement/${ueId}/elements-constitutifs`, payload ).pipe(
      tap(() => {
        this._refreshNeeded$.next();
      })
    );
  }

  /**
   * Met à jour un EC et notifie les composants.
   */
  update(id: number, payload: ElementConstitutifRequest): Observable<ElementConstitutifResponse> {
    return this.http.put<ElementConstitutifResponse>(`${this.apiUrl}/${id}`, payload ).pipe(
      tap(() => {
        this._refreshNeeded$.next();
      })
    );
  }

  /**
   * Supprime un EC et notifie les composants.
   */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}` ).pipe(
      tap(() => {
        this._refreshNeeded$.next();
      })
    );
  }
}
