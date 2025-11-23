// Fichier : src/app/shared/pipes/safe-url.pipe.ts
import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Pipe({
  name: 'safeUrl'
})
export class SafeUrlPipe implements PipeTransform {

  constructor(private sanitizer: DomSanitizer) {}

  transform(url: string): SafeResourceUrl {
    if (!url) {
      return '';
    }
    // On transforme une URL standard en une URL que Angular considère comme sûre pour les iframes.
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }
}
