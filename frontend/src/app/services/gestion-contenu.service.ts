// Fichier 1/4 : src/app/services/gestion-contenu.service.ts (Version Finale et Définitive)

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChapitreContenu, Section, ChapitreCreateDto, SectionCreateDto, SectionUpdateDto } from '../models/gestion-contenu.models';

@Injectable({
  providedIn: 'root'
} )
export class GestionContenuService {

  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient ) { }

  /**
   * GET /api/matieres/{id}/contenu
   * Récupère l'arborescence complète du contenu pour une matière donnée.
   */
  getContenuCompletMatiere(matiereId: number): Observable<ChapitreContenu[]> {
    return this.http.get<ChapitreContenu[]>(`${this.apiUrl}/matieres/${matiereId}/contenu` );
  }

  /**
   * POST /api/matieres/{id}/chapitres
   * Crée un nouveau chapitre pour une matière.
   */
  createChapitre(matiereId: number, data: ChapitreCreateDto): Observable<ChapitreContenu> {
    const url = `${this.apiUrl}/matieres/${matiereId}/chapitres`;
    return this.http.post<ChapitreContenu>(url, data );
  }

  /**
   * DELETE /api/chapitres/{id}
   * Supprime un chapitre par son ID.
   */
  deleteChapitre(chapitreId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/chapitres/${chapitreId}` );
  }

  /**
   * POST /api/chapitres/{id}/sections
   * Crée une nouvelle section pour un chapitre.
   */
  createSection(chapitreId: number, data: SectionCreateDto): Observable<Section> {
    const url = `${this.apiUrl}/chapitres/${chapitreId}/sections`;
    return this.http.post<Section>(url, data );
  }

  /**
   * PUT /api/sections/{id}
   * Met à jour une section existante.
   */
  updateSection(sectionId: number, data: SectionUpdateDto): Observable<Section> {
    const url = `${this.apiUrl}/sections/${sectionId}`;
    return this.http.put<Section>(url, data );
  }

  /**
   * DELETE /api/sections/{id}
   * Supprime une section par son ID.
   */
  deleteSection(sectionId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/sections/${sectionId}` );
  }
}
