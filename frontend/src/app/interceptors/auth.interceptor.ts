import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable( )
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    
    console.log(`[INTERCEPTEUR - DÉBUT] Requête interceptée pour ${request.url}`);

    try {
      // --- BLOC TRY N°1 : Récupération du token ---
      console.log('[INTERCEPTEUR] Étape 1: Tentative de récupération du token via authService.getToken()');
      const authToken = this.authService.getToken();
      console.log('[INTERCEPTEUR] Étape 2: Résultat de getToken() :', authToken ? 'Token trouvé' : 'Token NON trouvé (null/undefined)');

      if (authToken) {
        // --- BLOC TRY N°2 : Clonage de la requête ---
        try {
          console.log('[INTERCEPTEUR] Étape 3: Token trouvé. Tentative de clonage de la requête et ajout de l\'en-tête.');
          const authReq = request.clone({
            headers: request.headers.set('Authorization', 'Bearer ' + authToken)
          });
          console.log('[INTERCEPTEUR] Étape 4: Clonage réussi. La requête part avec l\'en-tête d\'autorisation.');
          
          // On passe la requête modifiée
          return next.handle(authReq);

        } catch (cloneError) {
          console.error('[INTERCEPTEUR] ❌ ERREUR FATALE lors du clonage de la requête :', cloneError);
          // Si le clonage échoue, on laisse passer la requête originale pour ne pas tout bloquer,
          // mais on saura que le problème est ici.
          return next.handle(request);
        }

      } else {
        console.warn('[INTERCEPTEUR] Étape 3: Aucun token trouvé. La requête part sans en-tête d\'autorisation.');
        // Si aucun token, on laisse passer la requête originale
        return next.handle(request);
      }

    } catch (globalError) {
      // Ce bloc attrapera toute erreur synchrone qui pourrait se produire dans la logique ci-dessus.
      console.error('[INTERCEPTEUR] ❌ ERREUR FATALE SYNCHRONE dans l\'intercepteur :', globalError);
      // On laisse passer la requête originale pour ne pas bloquer l'application.
      return next.handle(request);
    }
  }
}
