import { Component } from '@angular/core';
import { ThemeService } from '../../../core/services/theme.service';
import { AuthService } from '../../../services/auth.service'; // Assurez-vous que le chemin est correct
import { Observable } from 'rxjs';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {

  // Variable pour exposer l'état de connexion au template
  public isAuthenticated$: Observable<boolean>;

  constructor(
    private themeService: ThemeService,
    private authService: AuthService // 1. Injection du service d'authentification
  ) {
    // 2. Initialisation de l'observable pour le template
    this.isAuthenticated$ = this.authService.isAuthenticated$;
  }

  setTheme(mode: 'light' | 'dark' | 'auto'): void {
    this.themeService.setThemeMode(mode);
  }

  /**
   * 3. Méthode pour gérer la déconnexion.
   * Elle sera appelée par le bouton "Déconnexion" dans le HTML.
   */
  logout(): void {
    this.authService.logout();
  }
}
