// Dans matiere.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// ====================================================================
// --- INTERFACES (MODÈLES DE DONNÉES ) ---
// ====================================================================

export interface TestPourListe {
  id: number;
  titre: string;
  // Ajoutez d'autres propriétés si nécessaire
}

export interface ChapitrePourListe {
  id: number;
  nom: string;
  niveau: number;
  objectif: string;
  tests: TestPourListe[];
  numero?: number;
  resultat?: string;
  categorie?: string;
}

export interface MatiereAvecDetails {
  id: number;
  nom: string;
  description: string;
  chapitres: ChapitrePourListe[];
  ec: string; 
  ordre: number; 
  coefficient: number; 
}

// NOUVELLE INTERFACE : pour les données à envoyer lors de la création d'une matière.
// Seuls les champs nécessaires sont listés.
export interface NouvelleMatiereDto {
  nom: string;
  ec?: string;
  description?: string;
}


// ====================================================================
// --- CLASSE DU SERVICE ---
// ====================================================================

@Injectable({
  providedIn: 'root'
})
export class MatiereService {
  private apiUrl = 'http://localhost:8080/api/matieres';

  constructor(private http: HttpClient ) { }

  /**
   * Récupère la liste complète des matières avec tous leurs détails.
   * C'est la méthode principale pour obtenir la liste.
   */
  getMatieres(): Observable<MatiereAvecDetails[]> {
    // Appelle 'GET /api/matieres', qui est maintenant géré par le backend.
    return this.http.get<MatiereAvecDetails[]>(this.apiUrl );
  }

  /**
   * Récupère uniquement la liste des noms des matières.
   */
  getNomsMatieres(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/noms` );
  }

  /**
   * Récupère une seule matière par son identifiant.
   */
  getMatiereById(id: number): Observable<MatiereAvecDetails> {
    return this.http.get<MatiereAvecDetails>(`${this.apiUrl}/${id}` );
  }

  // ====================================================================
  // --- NOUVELLE FONCTIONNALITÉ : AJOUTER UNE MATIÈRE ---
  // ====================================================================
  /**
   * Envoie une nouvelle matière au backend pour la création.
   * @param matiereData Les données de la matière à créer (nom, etc.).
   */
  addMatiere(matiereData: NouvelleMatiereDto): Observable<MatiereAvecDetails> {
    // Fait un appel POST à 'http://localhost:8080/api/matieres'
    // avec les données de la nouvelle matière dans le corps de la requête.
    return this.http.post<MatiereAvecDetails>(this.apiUrl, matiereData );
  }
}
