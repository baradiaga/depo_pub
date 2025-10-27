import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service'; // Assurez-vous que le chemin vers votre AuthService est correct

/**
 * Intercepteur HTTP qui ajoute automatiquement le token JWT (Bearer Token )
 * à l'en-tête 'Authorization' de toutes les requêtes sortantes,
 * si un token est disponible dans l'AuthService.
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  // On injecte l'AuthService pour avoir accès à la méthode getToken()
  constructor(private authService: AuthService) {}

  /**
   * La méthode principale de l'intercepteur, appelée pour chaque requête HTTP.
   * @param request La requête sortante originale.
   * @param next Le prochain gestionnaire dans la chaîne d'intercepteurs.
   * @returns Un Observable de l'événement HTTP.
   */
  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    
    // 1. On récupère le token en utilisant la méthode centralisée de l'AuthService.
    const authToken = this.authService.getToken();

    // 2. On vérifie si le token existe.
    if (authToken) {
      // 3. Si un token existe, on clone la requête originale pour y ajouter
      //    l'en-tête 'Authorization' avec le format "Bearer [token]".
      const authReq = request.clone({
        headers: request.headers.set('Authorization', 'Bearer ' + authToken)
      });

      // 4. On passe la nouvelle requête (avec l'en-tête) au prochain gestionnaire.
      return next.handle(authReq);
    }

    // 5. Si aucun token n'est trouvé, on laisse passer la requête originale sans la modifier.
    //    Ceci est important pour les requêtes publiques comme le login.
    return next.handle(request);
  }
}
