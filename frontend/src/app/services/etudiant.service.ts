import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface EtudiantDto {
  id?: number;
  nom: string;
  prenom: string;
  email: string;
  numeroMatricule?: string;
  dateDeNaissance: string;
  lieuDeNaissance: string;
  nationalite: string;
  sexe: string;
  adresse: string;
  telephone: string;
  anneeAcademique: string;
  filiere: string;
  matiereIds: number[];
  enseignantId?: number | null;
  dateInscription?: Date;
  enseignantNom?: string;
}

@Injectable({
  providedIn: 'root'
} )
export class EtudiantService {
  private apiUrl = 'http://localhost:8080/api/etudiants';

  constructor(private http: HttpClient ) { }

  getEtudiants(): Observable<EtudiantDto[]> {
    return this.http.get<EtudiantDto[]>(this.apiUrl );
  }

  inscrireEtudiant(etudiantDto: EtudiantDto): Observable<EtudiantDto> {
    return this.http.post<EtudiantDto>(this.apiUrl, etudiantDto );
  }

  updateEtudiant(id: number, etudiantDto: EtudiantDto): Observable<EtudiantDto> {
    return this.http.put<EtudiantDto>(`${this.apiUrl}/${id}`, etudiantDto );
  }

  deleteEtudiant(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}` );
  }
}
