import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
} )
export class SectionService {
  private apiUrl = 'http://localhost:8080/api/sections';

  constructor(private http: HttpClient ) { }

  /**
   * Met à jour le contenu d'une section.
   * @param sectionId L'ID de la section à mettre à jour.
   * @param contenu Le nouveau contenu (texte, HTML...).
   */
  updateContenu(sectionId: number, contenu: string): Observable<void> {
    const payload = { contenu: contenu };
    return this.http.put<void>(`${this.apiUrl}/${sectionId}`, payload );
  }
}
