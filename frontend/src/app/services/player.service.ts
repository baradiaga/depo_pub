import { Injectable, signal, computed, inject } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

@Injectable({
  providedIn: 'root'
})
export class PlayerService {
  private sanitizer = inject(DomSanitizer);

  // État du lecteur
  public currentUrl = signal<string | null>(null);
  public isVisible = signal<boolean>(false);

  // 1. Extraction de l'ID YouTube (Signal calculé)
  public youtubeId = computed(() => {
    const url = this.currentUrl();
    if (!url) return null;

    // Regex robuste pour extraire l'ID de 11 caractères
    const regExp = /^.*(?:(?:youtu\.be\/|v\/|vi\/|u\/\w\/|embed\/|shorts\/)|(?:(?:watch)?\?v(?:i)?=|\&v(?:i)?=))([^#\&\?]*).*/;
    const match = url.match(regExp);

    // match[1] contient le premier groupe de capture (l'ID)
    if (match && match[1].length === 11) {
      console.log('DEBUG - ID YouTube trouvé :', match[1]);
      return match[1];
    }
    
    return null;
  });

  // 2. Sécurisation de l'URL pour les autres types (Iframe)
  public safeUrl = computed(() => {
    const url = this.currentUrl();
    if (!url) return null;
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  });

  // Méthode pour ouvrir le lecteur
  open(url: string) {
    console.log('DEBUG - Ouverture URL :', url);
    this.currentUrl.set(url);
    this.isVisible.set(true);
  }

  // Méthode pour fermer le lecteur
  close() {
    this.isVisible.set(false);
    this.currentUrl.set(null);
  }
}
