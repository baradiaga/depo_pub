import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from './services/auth.service'; // Assurez-vous que le chemin est correct
import { UserRole } from './models/user.model'; // Assurez-vous que le chemin est correct

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    // --- 1. L'utilisateur est-il connecté ? ---
    const isAuthenticated = this.authService.hasToken(); // Utilise une méthode simple et fiable

    if (!isAuthenticated) {
      console.error('AuthGuard: Utilisateur non authentifié. Redirection vers la connexion.');
      // Si non, on le redirige vers la page de connexion
      return this.router.createUrlTree(['/auth/login']);
    }

    // --- 2. La route nécessite-t-elle des rôles spécifiques ? ---
    const requiredRoles = route.data['roles'] as Array<UserRole>;

    // Si la route n'a pas de champ 'data.roles', on autorise l'accès (pour les pages connectées mais sans rôle requis)
    if (!requiredRoles || requiredRoles.length === 0) {
      return true;
    }

    // --- 3. L'utilisateur a-t-il le bon rôle ? ---
    const userRole = this.authService.getUserRole();

    if (!userRole) {
      console.error('AuthGuard: Rôle utilisateur non trouvé. Redirection vers la connexion.');
      // Si l'utilisateur est connecté mais que son rôle est introuvable, c'est une anomalie. On déconnecte.
      this.authService.logout();
      return false;
    }

    const hasRequiredRole = requiredRoles.includes(userRole);

    if (hasRequiredRole) {
      // L'utilisateur a le bon rôle, on autorise l'accès.
      return true;
    } else {
      // L'utilisateur n'a pas le bon rôle.
      console.warn(`AuthGuard: Accès refusé. L'utilisateur avec le rôle '${userRole}' a tenté d'accéder à une route nécessitant les rôles :`, requiredRoles);
      
      // On le redirige vers sa page d'accueil par défaut pour éviter une boucle infinie.
      // Vous pouvez aussi créer une page "Accès Refusé" (403).
      const defaultPath = this.getRedirectPathByRole(userRole); // On réutilise la logique du service
      return this.router.createUrlTree([defaultPath]);
    }
  }

  // Méthode miroir de celle du service pour éviter une dépendance circulaire si nécessaire
  private getRedirectPathByRole(role: UserRole | null): string {
    if (!role) {
      return '/auth/login';
    }
    if (role === 'ETUDIANT') {
      return '/app/curriculum/matieres';
    }
    switch (role) {
      case 'ADMIN': return '/app/admin';
      case 'ENSEIGNANT': return '/app/enseignant';
      case 'TUTEUR': return '/app/tuteur';
      case 'TECHNOPEDAGOGUE': return '/app/technopedagogue';
      case 'RESPONSABLE_FORMATION': return '/app/admin';
      default: return '/auth/login';
    }
  }
}
