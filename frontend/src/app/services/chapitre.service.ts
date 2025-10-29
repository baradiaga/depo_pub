import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// --- Interfaces ---

export interface ChapitrePayload {
  matiere: string;
  titre: string;
  niveau: number;
  objectif: string;
  sections: { titre: string, contenu: string }[];
}

/**
 * Interface pour les détails complets d'un chapitre.
 * Inclut maintenant matiereId.
 */
export interface ChapitreDetail {
  id: number;
  titre: string;
  matiereId: number; // <-- Champ important
  matiereNom: string;
  niveau: number;
  objectif: string;
  sections: { id: number; titre: string; contenu?: string }[];
}

/**
 * Interface légère pour les listes de chapitres.
 */
// L'interface ChapitreInfo n'est plus nécessaire car le backend retourne ChapitreDetail pour la liste.
// export interface ChapitreInfo {
//   id: number;
//   nom: string;
//   matiere: string;
// }

@Injectable({
  providedIn: 'root'
} )
export class ChapitreService {

  private apiUrl = 'http://localhost:8080/api/chapitres';

  constructor(private http: HttpClient ) { }

  // --- Méthodes ---

  creerChapitre(chapitre: ChapitrePayload): Observable<ChapitreDetail> {
    return this.http.post<ChapitreDetail>(this.apiUrl, chapitre );
  }

  getAllChapitres(): Observable<ChapitreDetail[]> {
    return this.http.get<ChapitreDetail[]>(this.apiUrl);
  }

  updateChapitre(id: number, chapitre: ChapitrePayload): Observable<ChapitreDetail> {
    return this.http.put<ChapitreDetail>(`${this.apiUrl}/${id}`, chapitre);
  }

  deleteChapitre(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Récupère les détails complets d'un chapitre par son ID.
   */
  getChapitreById(id: number): Observable<ChapitreDetail> {
    return this.http.get<ChapitreDetail>(`${this.apiUrl}/${id}` );
  }

  findChapitreByMatiereAndNiveau(matiere: string, niveau: number): Observable<ChapitreDetail> {
    const params = { matiere: matiere, niveau: niveau.toString() };
    return this.http.get<ChapitreDetail>(`${this.apiUrl}/search`, { params } );
  }

  /**
   * Récupère la liste des chapitres pour une matière spécifique.
   */
  getChapitresParMatiere(matiereId: number): Observable<ChapitreDetail[]> {
    const params = { matiereId: matiereId.toString() };
    return this.http.get<ChapitreDetail[]>(this.apiUrl, { params } );
  }
}
