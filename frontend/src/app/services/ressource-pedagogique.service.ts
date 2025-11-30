// Fichier : src/app/services/ressource-pedagogique.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RessourcePedagogique } from '../models/models'; // Interface à créer

@Injectable({
  providedIn: 'root'
} )
export class RessourcePedagogiqueService {

 private apiUrl = 'http://localhost:8080/api/ressources';

  constructor(private http: HttpClient ) { }

  // Récupérer toutes les ressources
  getAllRessources(): Observable<RessourcePedagogique[]> {
    return this.http.get<RessourcePedagogique[]>(this.apiUrl );
  }

  // Téléverser une nouvelle ressource
  televerserRessource(fichier: File, metadata: any): Observable<RessourcePedagogique> {
    const formData = new FormData();
    formData.append('fichier', fichier, fichier.name);
    // Convertir les métadonnées en JSON string
    formData.append('metadata', JSON.stringify(metadata));

    return this.http.post<RessourcePedagogique>(this.apiUrl, formData );
  }

  // Supprimer une ressource
  supprimerRessource(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}` );
  }

  // Obtenir l'URL de téléchargement
  getTelechargementUrl(nomFichierStocke: string): string {
    return `${this.apiUrl}/telecharger/${nomFichierStocke}`;
  }
}
