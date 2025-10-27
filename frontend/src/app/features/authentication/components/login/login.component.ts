import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
} )
export class LoginComponent {
  // On garde les propriétés pour le formulaire
  credentials = {
    email: '',
    motDePasse: '' // Assurez-vous que votre API attend 'motDePasse' et non 'password'
  };
  errorMessage = '';
  showPassword = false;

  // On injecte uniquement AuthService, Router n'est plus nécessaire ici pour la redirection
  constructor(private authService: AuthService) {}

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  onSubmit(): void {
    this.errorMessage = ''; // Réinitialiser les erreurs

    // =================================================================
    // === LOGIQUE SIMPLIFIÉE ===
    // =================================================================
    this.authService.login(this.credentials).subscribe({
      next: () => {
        // EN CAS DE SUCCÈS : ON NE FAIT RIEN ICI !
        // La redirection est entièrement gérée par le AuthService.
        // Le composant n'a plus besoin de s'en soucier.
        console.log('Connexion réussie. La redirection est gérée par le service.');
      },
      error: (err: HttpErrorResponse) => {
        // EN CAS D'ÉCHEC : On affiche un message à l'utilisateur.
        console.error('Erreur de connexion:', err);
        this.errorMessage = 'L\'email ou le mot de passe est incorrect.';
      }
    });
    // =================================================================
  }
}
