// src/app/guards/auth.guard.ts

import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): Observable<boolean> {
    return this.authService.isAuthenticated$.pipe(
      take(1), // Prend la dernière valeur émise et se désinscrit
      map(isAuthenticated => {
        if (isAuthenticated) {
          return true; // L'utilisateur est authentifié, accès autorisé
        } else {
          // L'utilisateur n'est pas authentifié, redirection vers la page de login
          this.router.navigate(['/login']);
          return false;
        }
      })
    );
  }
}
