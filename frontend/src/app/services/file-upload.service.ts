// Fichier : src/app/shared/services/file-upload.service.ts (Nouveau Fichier)

import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
} )
export class FileUploadService {

  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient ) { }

  /**
   * Envoie un fichier à l'endpoint d'upload du backend.
   * @param file Le fichier à uploader.
   */
  upload(file: File): Observable<{ filePath: string }> {
    const formData = new FormData();
    formData.append('file', file, file.name);

    // Note : Pour les requêtes FormData, Angular définit le Content-Type automatiquement.
    // Il ne faut PAS le définir manuellement, sinon le 'boundary' manquera.
    return this.http.post<{ filePath: string }>(`${this.apiUrl}/upload`, formData );
  }
}
