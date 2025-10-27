// Fichier : src/app/services/user.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// On définit une interface pour la réponse de l'API /me
export interface UserProfile {
  id: number;
  email: string;
  nom: string;
  prenom: string;
  role: string;
  actif: boolean;
}

@Injectable({
  providedIn: 'root'
} )
export class UserService {
  // L'URL de votre endpoint qui renvoie les infos de l'utilisateur connecté
  private profileUrl = 'http://localhost:8080/api/auth/me';

  constructor(private http: HttpClient ) { }

  /**
   * Récupère le profil complet de l'utilisateur actuellement authentifié.
   * @returns Un Observable contenant les informations du profil.
   */
  getCurrentUserProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(this.profileUrl );
  }
}
