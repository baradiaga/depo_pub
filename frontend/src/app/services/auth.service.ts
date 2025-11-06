// Fichier : src/app/services/auth.service.ts (Version Complète et Finale)

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { UserRole } from '../models/user.model';

// --- INTERFACE MISE À JOUR POUR LE TOKEN DÉCODÉ ---
// Le backend doit inclure 'nom' et 'prenom' dans le token JWT.
interface DecodedToken {
  sub: string; // L'email
  authorities: string[];
  permissions: string[];
  nom: string;
  prenom: string;
  iat: number;
  exp: number;
}

// --- INTERFACE MISE À JOUR POUR LA RÉPONSE DU LOGIN ---
// Le backend doit renvoyer ces champs lors du login.
export interface LoginResponse {
  token: string;
  id: number;
  email: string;
  role: string;
  nom: string;
  prenom: string;
}

@Injectable({
  providedIn: 'root'
} )
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken( ));
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  private permissionsSubject = new BehaviorSubject<void>(undefined);

  constructor(
    private http: HttpClient,
    private router: Router
   ) {}

  /**
   * Gère la connexion de l'utilisateur.
   * Nettoie l'ancienne session, stocke la nouvelle et redirige.
   */
  login(credentials: any): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials ).pipe(
      tap(response => {
        // 1. Nettoyer toute session précédente pour éviter les conflits.
        localStorage.clear();

        // 2. Stocker les informations de la nouvelle session.
        localStorage.setItem('auth_token', response.token);
        localStorage.setItem('user_id', response.id.toString());
        localStorage.setItem('user_email', response.email);
        localStorage.setItem('user_role', response.role);
        localStorage.setItem('user_nom', response.nom);
        localStorage.setItem('user_prenom', response.prenom);

        // 3. Notifier le reste de l'application que l'état a changé.
        this.isAuthenticatedSubject.next(true);
        this.permissionsSubject.next();

        // 4. Rediriger l'utilisateur vers la page appropriée.
        const userRole = response.role as UserRole;
        const destination = this.getRedirectPathByRole(userRole);
        this.router.navigate([destination]);
      })
    );
  }

  /**
   * Gère la déconnexion de l'utilisateur.
   */
  logout(): void {
    // On peut appeler l'endpoint de logout du backend, mais on nettoie le client dans tous les cas.
    this.http.post(`${this.apiUrl}/logout`, {} ).subscribe({
      next: () => this.performClientLogout(),
      error: () => this.performClientLogout() // On déconnecte le client même si le serveur a une erreur.
    });
  }

  /**
   * Logique de nettoyage côté client lors de la déconnexion.
   */
  private performClientLogout(): void {
    // Utiliser clear() est la méthode la plus sûre pour une déconnexion totale.
    localStorage.clear();

    this.isAuthenticatedSubject.next(false);
    this.permissionsSubject.next();
    this.router.navigate(['/auth/login']);
  }

  /**
   * Détermine le chemin de redirection approprié en fonction du rôle de l'utilisateur.
   */
  private getRedirectPathByRole(role: UserRole | null): string {
    if (!role) {
      return '/auth/login';
    }
    if (role === 'ETUDIANT') {
      return '/app/curriculum/matieres';
    }
    // Ajoutez ici vos autres cas de redirection
    switch (role) {
      case 'ADMIN':
      case 'RESPONSABLE_FORMATION':
        return '/app/admin';
      case 'ENSEIGNANT':
        return '/app/enseignant/dashboard';
      default:
        return '/auth/login'; // Sécurité pour les rôles inconnus
    }
  }

  // ====================================================================
  // === NOUVELLE MÉTHODE POUR RÉCUPÉRER LE NOM COMPLET                ===
  // ====================================================================
  /**
   * Récupère le nom complet de l'utilisateur connecté.
   * Privilégie les données du localStorage pour la rapidité.
   * @returns Le nom complet (ex: "Abdoulaye Thiaw") ou null si non trouvé.
   */
  getUserFullName(): string | null {
    const prenom = localStorage.getItem('user_prenom');
    const nom = localStorage.getItem('user_nom');

    if (prenom && nom) {
      return `${prenom} ${nom}`;
    }
    
    // Solution de secours si le localStorage est vide mais qu'un token existe
    const decodedToken = this.getDecodedToken();
    if (decodedToken && decodedToken.prenom && decodedToken.nom) {
      return `${decodedToken.prenom} ${decodedToken.nom}`;
    }

    return null;
  }

  // --- Méthodes utilitaires pour accéder aux données de session ---

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  getUserRole(): UserRole | null {
    const role = localStorage.getItem('user_role');
    return role as UserRole | null;
  }

  hasToken(): boolean {
    return !!this.getToken();
  }

  private getDecodedToken(): DecodedToken | null {
    const token = this.getToken();
    if (token) {
      try {
        return jwtDecode<DecodedToken>(token);
      } catch (error) {
        console.error("Erreur lors du décodage du token:", error);
        return null;
      }
    }
    return null;
  }

  getUserPermissions(): string[] {
    const decodedToken = this.getDecodedToken();
    return decodedToken?.permissions || [];
  }

  hasPermission(permissionKey: string): boolean {
    return this.getUserPermissions().includes(permissionKey);
  }

  getPermissions$(): Observable<void> {
    return this.permissionsSubject.asObservable();
  }
}
