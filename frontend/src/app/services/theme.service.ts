// Fichier : src/app/services/theme.service.ts (Version Améliorée)

import { Injectable, Renderer2, RendererFactory2 } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

// 1. Utilisation d'un type et d'une constante pour la clarté et la sécurité
export type ThemeMode = 'light' | 'dark' | 'auto';
const THEME_STORAGE_KEY = 'app-theme-mode';

@Injectable({
  providedIn: 'root',
})
export class ThemeService {
  // 2. Utilisation d'un Renderer2 pour manipuler le DOM de manière sécurisée
  private renderer: Renderer2;

  // 3. Utilisation d'un BehaviorSubject pour un état réactif
  // Il stocke le mode actuel et notifie les abonnés de tout changement.
  private currentTheme$: BehaviorSubject<ThemeMode>;

  constructor(rendererFactory: RendererFactory2) {
    // On crée une instance de Renderer2, la méthode recommandée par Angular
    this.renderer = rendererFactory.createRenderer(null, null);

    // On initialise le BehaviorSubject avec la valeur stockée ou 'auto' par défaut
    const savedMode = localStorage.getItem(THEME_STORAGE_KEY) as ThemeMode | null;
    this.currentTheme$ = new BehaviorSubject<ThemeMode>(savedMode || 'auto');

    // On écoute les changements de thème du système d'exploitation
    this.listenToSystemThemeChanges();

    // On applique le thème initial
    this.applyTheme(this.currentTheme$.value);
  }

  /**
   * Expose le thème actuel comme un Observable public.
   * Les composants peuvent s'y abonner pour réagir aux changements de thème.
   */
  public getThemeMode(): Observable<ThemeMode> {
    return this.currentTheme$.asObservable();
  }

  /**
   * Met à jour le mode de thème, le stocke et l'applique.
   * @param mode Le nouveau mode à appliquer.
   */
  public setThemeMode(mode: ThemeMode): void {
    localStorage.setItem(THEME_STORAGE_KEY, mode);
    this.currentTheme$.next(mode); // Notifie les abonnés du changement
    this.applyTheme(mode);
  }

  /**
   * Logique centrale pour appliquer le thème visuel au corps du document.
   * @param mode Le mode de thème à activer.
   */
  private applyTheme(mode: ThemeMode): void {
    const effectiveMode = this.getEffectiveMode(mode);

    // On retire les anciennes classes de thème pour éviter les conflits
    this.renderer.removeClass(document.body, 'light-theme');
    this.renderer.removeClass(document.body, 'dark-theme');

    // On ajoute la nouvelle classe de thème
    this.renderer.addClass(document.body, `${effectiveMode}-theme`);
    console.log(`[ThemeService] Thème appliqué : ${effectiveMode}`);
  }

  /**
   * Détermine le thème effectif ('light' ou 'dark') à partir du mode ('auto' inclus).
   */
  private getEffectiveMode(mode: ThemeMode): 'light' | 'dark' {
    if (mode === 'auto') {
      // On vérifie la préférence système
      return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    }
    return mode;
  }

  /**
   * 4. Écoute les changements de thème du système d'exploitation.
   * Si le mode actuel est 'auto', le thème de l'application se met à jour automatiquement.
   */
  private listenToSystemThemeChanges(): void {
    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (event) => {
      // On ne réagit que si l'utilisateur est en mode 'auto'
      if (this.currentTheme$.value === 'auto') {
        const newColorScheme = event.matches ? 'dark' : 'light';
        console.log(`[ThemeService] Changement de thème système détecté. Nouveau thème : ${newColorScheme}`);
        this.applyTheme('auto');
      }
    });
  }
}
