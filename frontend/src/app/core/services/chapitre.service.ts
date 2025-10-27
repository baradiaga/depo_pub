import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';

export interface Chapitre {
  id: number;
  nom: string;
  statut: 'non commencé' | 'en cours' | 'terminé';
}

@Injectable({
  providedIn: 'root'
})
export class ChapitreService {

  constructor(private http: HttpClient) { }

  // Exemple : récupération depuis l'API
  getChapitres(carteId: number): Observable<Chapitre[]> {
    // Si tu n'as pas encore l'API, renvoie un mock pour tester
    const mock: Chapitre[] = [
      { id: 1, nom: 'Introduction', statut: 'non commencé' },
      { id: 2, nom: 'Chapitre 1', statut: 'en cours' },
      { id: 3, nom: 'Chapitre 2', statut: 'terminé' }
    ];

    return of(mock); // remplace par appel réel quand API dispo
    // return this.http.get<Chapitre[]>(`/api/chapitres?carteId=${carteId}`);
  }
}
