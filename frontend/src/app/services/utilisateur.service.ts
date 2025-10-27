// Fichier : src/app/services/utilisateur.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// L'import de 'environment' est supprimé

export interface Utilisateur {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role?: string;
}

@Injectable({
  providedIn: 'root'
} )
export class UtilisateurService {
  // =======================================================
  // === URL DE L'API MISE EN DUR ICI ===
  // =======================================================
  private baseUrl = 'http://localhost:8080/api'; // Adaptez si votre port ou base est différent

  constructor(private http: HttpClient ) { }

  getEnseignants(): Observable<Utilisateur[]> {
    return this.http.get<Utilisateur[]>(`${this.baseUrl}/admin/users/role/ENSEIGNANT` );
  }
}
