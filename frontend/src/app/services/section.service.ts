// Fichier : src/app/services/section.service.ts (Corrigé et Final)

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// --- INTERFACES ---
// On définit des interfaces pour garantir la cohérence des données.

// L'objet que nous envoyons au backend. Doit correspondre au DTO SectionUpdateRequest.
export interface SectionUpdatePayload {
  titre: string;
  contenu: string;
}

// L'objet Section tel qu'il est utilisé dans notre application.
export interface Section {
  id: number;
  titre: string;
  contenu: string;
  ordre: number;
}

@Injectable({
  providedIn: 'root'
} )
export class SectionService {

  private apiUrl = 'http://localhost:8080/api'; // On utilise l'URL de base

  constructor(private http: HttpClient ) { }

  /**
   * Met à jour le contenu et le titre d'une section.
   * @param section L'objet Section complet, car le backend a besoin du titre et du contenu.
   * @returns Un Observable de la section mise à jour.
   */
  updateContenu(section: Section): Observable<Section> {
    
    // 1. On construit le payload qui correspond EXACTEMENT au DTO du backend.
    const payload: SectionUpdatePayload = {
      titre: section.titre, // Le champ 'titre' était manquant.
      contenu: section.contenu || '' // On s'assure que le contenu n'est jamais null.
    };

    // 2. On construit l'URL complète et correcte.
    const url = `${this.apiUrl}/sections/${section.id}/contenu`;

    // 3. On utilise la méthode PUT, comme défini dans notre SectionController.
    return this.http.put<Section>(url, payload );
  }

  // Vous pouvez ajouter d'autres méthodes ici plus tard si nécessaire,
  // par exemple pour créer ou supprimer une section individuellement.
}
