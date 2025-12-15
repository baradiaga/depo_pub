// Fichier : src/app/services/chapitre.service.ts (Version mise à jour)

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Chapitre, ChapitrePayload, ChapitreDetail } from '../models/models';

// --- NOUVELLES INTERFACES POUR LES DONNÉES COMPLÈTES ---
// Ces interfaces doivent correspondre aux DTOs de votre backend.

export interface SectionDetail {
  id: number;
  titre: string;
  contenu: string;
  ordre: number;
}

export interface ChapitreAvecSections {
  id: number;
  nom: string;
  objectif: string;
  sections: SectionDetail[];
}


@Injectable({
  providedIn: 'root'
} )
export class ChapitreService {
  private apiUrl = 'http://localhost:8080/api/chapitres';

  constructor(private http: HttpClient ) { }

  // --- VOS MÉTHODES EXISTANTES (INCHANGÉES) ---

  getChapitreDetails(id: number): Observable<ChapitreDetail> {
    console.log(`[ChapitreService] Appel API pour récupérer les détails du chapitre ${id}`);
    return this.http.get<ChapitreDetail>(`${this.apiUrl}/${id}/details` );
  }

  creerChapitre(payload: ChapitrePayload): Observable<Chapitre> {
    return this.http.post<Chapitre>(this.apiUrl, payload );
  }

  findById(id: number): Observable<Chapitre> {
    return this.http.get<Chapitre>(`${this.apiUrl}/${id}` );
  }

  findChapitreByMatiereAndNiveau(matiereNom: string, niveau: number): Observable<Chapitre> {
    const params = new HttpParams()
      .set('matiere', matiereNom)
      .set('niveau', niveau.toString());
    const url = `${this.apiUrl}/search/complet`;
    return this.http.get<Chapitre>(url, { params } );
  }

  getChapitresParMatiere(matiereId: number): Observable<Chapitre[]> {
    return this.http.get<Chapitre[]>(`http://localhost:8080/api/elements-constitutifs/${matiereId}/chapitres` );
  }

  // ====================================================================
  // === NOUVELLE MÉTHODE AJOUTÉE POUR LA PAGE DE DÉTAIL DE L'ÉTUDIANT   ===
  // ====================================================================
  /**
   * Récupère les détails complets d'un chapitre, y compris le contenu de ses sections.
   * @param id L'ID du chapitre à récupérer.
   */
  getChapitreComplet(id: number): Observable<ChapitreAvecSections> {
    const url = `${this.apiUrl}/${id}/details-complets`;
    console.log(`[ChapitreService] Appel de l'URL pour le détail complet : ${url}`);
    return this.http.get<ChapitreAvecSections>(url );
  }
  getChapitreById(id: number): Observable<Chapitre> {
    return this.http.get<Chapitre>(`${this.apiUrl}/${id}`);
  }
}
