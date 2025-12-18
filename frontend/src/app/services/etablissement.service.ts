import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Etablissement } from '../models/etablissement.model';

export interface EtablissementSearchParams {
  search?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
}

export interface EtablissementSearchResult {
  content: Etablissement[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class EtablissementService {
  private apiUrl = 'http://localhost:8080/etablissements';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Etablissement[]> {
    return this.http.get<Etablissement[]>(this.apiUrl);
  }

  search(params: EtablissementSearchParams): Observable<EtablissementSearchResult> {
    let httpParams = new HttpParams();
    
    if (params.search) {
      httpParams = httpParams.set('search', params.search);
    }
    
    if (params.page) {
      httpParams = httpParams.set('page', (params.page - 1).toString());
    }
    
    if (params.size) {
      httpParams = httpParams.set('size', params.size.toString());
    }
    
    if (params.sortBy) {
      const sort = `${params.sortBy},${params.sortOrder || 'asc'}`;
      httpParams = httpParams.set('sort', sort);
    }
    
    return this.http.get<EtablissementSearchResult>(`${this.apiUrl}/search`, { 
      params: httpParams 
    });
  }

  getById(id: number): Observable<Etablissement> {
    return this.http.get<Etablissement>(`${this.apiUrl}/${id}`);
  }

  create(etablissement: Etablissement): Observable<Etablissement> {
    return this.http.post<Etablissement>(this.apiUrl, etablissement);
  }

  update(id: number, etablissement: Etablissement): Observable<Etablissement> {
    return this.http.put<Etablissement>(`${this.apiUrl}/${id}`, etablissement);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}