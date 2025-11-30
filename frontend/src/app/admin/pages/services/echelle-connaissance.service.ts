// Fichier : src/app/services/echelle-connaissance.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Définition de l'interface pour le frontend
export interface EchelleConnaissance {
  id?: number; // Optionnel pour la création
  intervalle: string;
  description: string;
  recommandation: string;
}

@Injectable({
  providedIn: 'root'
} )
export class EchelleConnaissanceService {
  private apiUrl = 'http://localhost:8080/api/echelles-connaissance'; // Assurez-vous que le port 8080 est correct

  constructor(private http: HttpClient ) { }

  // READ: Récupérer toutes les échelles
  getAll(): Observable<EchelleConnaissance[]> {
    return this.http.get<EchelleConnaissance[]>(this.apiUrl );
  }

  // CREATE: Créer une nouvelle échelle
  create(echelle: EchelleConnaissance): Observable<EchelleConnaissance> {
    // L'ID est retiré car il est généré par le backend
    const { id, ...dataToSend } = echelle;
    return this.http.post<EchelleConnaissance>(this.apiUrl, dataToSend );
  }

  // UPDATE: Mettre à jour une échelle
  update(echelle: EchelleConnaissance): Observable<EchelleConnaissance> {
    return this.http.put<EchelleConnaissance>(`${this.apiUrl}/${echelle.id}`, echelle );
  }

  // DELETE: Supprimer une échelle
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}` );
  }
}
