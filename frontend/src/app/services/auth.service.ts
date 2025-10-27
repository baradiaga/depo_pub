import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { UserRole } from '../models/user.model';

// Interface pour le token décodé
interface DecodedToken {
  sub: string;
  authorities: string[];
  permissions: string[];
  iat: number;
  exp: number;
}

// Interface pour la réponse de l'API de connexion
export interface LoginResponse {
  token: string;
  id: number;
  email: string;
  role: string;
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
   * Gère la connexion de l'utilisateur, le stockage des données et la redirection.
   */
  login(credentials: any): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials ).pipe(
      tap(response => {
        // 1. Stocker les informations de la session dans le localStorage
        localStorage.setItem('auth_token', response.token);
        localStorage.setItem('user_id', response.id.toString());
        localStorage.setItem('user_email', response.email);
        localStorage.setItem('user_role', response.role);

        // 2. Notifier le reste de l'application que l'utilisateur est connecté
        this.isAuthenticatedSubject.next(true);
        this.permissionsSubject.next();

        // 3. Rediriger l'utilisateur vers la page appropriée
        const userRole = response.role as UserRole;
        const destination = this.getRedirectPathByRole(userRole);
        this.router.navigate([destination]);
      })
    );
  }

  /**
   * Gère la déconnexion de l'utilisateur côté serveur et client.
   */
  logout(): void {
    this.http.post(`${this.apiUrl}/logout`, {} ).subscribe({
      next: () => this.performClientLogout(),
      error: () => this.performClientLogout() // Déconnecte côté client même si le serveur ne répond pas
    });
  }

  /**
   * Logique de nettoyage côté client lors de la déconnexion.
   */
  private performClientLogout(): void {
    // Nettoyer le localStorage
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_id');
    localStorage.removeItem('user_email');
    localStorage.removeItem('user_role');

    // Mettre à jour l'état de l'application
    this.isAuthenticatedSubject.next(false);
    this.permissionsSubject.next();

    // Rediriger vers la page de connexion
    this.router.navigate(['/auth/login']);
  }

  /**
   * Détermine le chemin de redirection approprié en fonction du rôle de l'utilisateur.
   */
  private getRedirectPathByRole(role: UserRole | null): string {
    if (!role) {
      return '/auth/login'; // Sécurité
    }

    // Règle n°1 : Cas spécifique pour l'étudiant
    if (role === 'ETUDIANT') {
      // Assurez-vous que ce chemin correspond à votre routing pour MatieresReprendreComponent
      return '/app/curriculum/matieres';
    }

    // Règle n°2 : Cas général pour tous les autres rôles (leur dashboard/page d'accueil)
    switch (role) {
      case 'ADMIN':
        return '/app/admin'; // Sera redirigé vers /app/admin/dashboard
      case 'ENSEIGNANT':
        return '/app/enseignant'; // Sera redirigé vers sa page par défaut
      case 'TUTEUR':
        return '/app/tuteur';
      case 'TECHNOPEDAGOGUE':
        return '/app/technopedagogue';
      case 'RESPONSABLE_FORMATION':
        return '/app/admin'; // Pour l'instant, vers le dashboard admin
      default:
        return '/auth/login'; // Sécurité pour les rôles inconnus
    }
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

  hasCustomPermissions(): boolean {
    return this.getUserPermissions().length > 0;
  }
}
