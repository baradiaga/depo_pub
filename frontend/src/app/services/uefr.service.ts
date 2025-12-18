import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Uefr } from '../models/uefr.model';

export interface UefrSearchParams {
  search?: string;
  etablissementId?: number;
  page?: number;
  size?: number;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
}

export interface UefrSearchResult {
  content: Uefr[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class UefrService {
  private apiUrl = 'http://localhost:8080/uefrs';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Uefr[]> {
    return this.http.get<Uefr[]>(this.apiUrl);
  }

  search(params: UefrSearchParams): Observable<UefrSearchResult> {
    let httpParams = new HttpParams();
    
    if (params.search) {
      httpParams = httpParams.set('search', params.search);
    }
    
    if (params.etablissementId && params.etablissementId > 0) {
      httpParams = httpParams.set('etablissementId', params.etablissementId.toString());
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
    
    return this.http.get<UefrSearchResult>(`${this.apiUrl}/search`, { 
      params: httpParams 
    });
  }

  getById(id: number): Observable<Uefr> {
    return this.http.get<Uefr>(`${this.apiUrl}/${id}`);
  }

  create(uefr: Uefr): Observable<Uefr> {
    return this.http.post<Uefr>(this.apiUrl, uefr);
  }

  update(id: number, uefr: Uefr): Observable<Uefr> {
    return this.http.put<Uefr>(`${this.apiUrl}/${id}`, uefr);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}