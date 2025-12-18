import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Uefr } from '../../../../models/uefr.model';
import { Etablissement } from '../../../../models/etablissement.model';
import { UefrService, UefrSearchParams } from '../../../../services/uefr.service';
import { EtablissementService } from '../../../../services/etablissement.service';

@Component({
  selector: 'app-uefr',
  templateUrl: './uefr.component.html',
  styleUrls: ['./uefr.component.css']
})
export class UefrComponent implements OnInit {
  uefrs: Uefr[] = [];
  filteredUefrs: Uefr[] = [];
  etablissements: Etablissement[] = [];
  
  uefrForm: FormGroup;
  filterForm: FormGroup;
  
  uefrId: number | null = null;
  isEditMode = false;
  isLoading = false;
  
  // Pagination
  currentPage: number = 1;
  itemsPerPage: number = 10;
  totalItems: number = 0;
  totalPages: number = 0;
    Math = Math;
  // Search mode
  useServerSideSearch: boolean = false;

  constructor(
    private fb: FormBuilder,
    private uefrService: UefrService,
    private etablissementService: EtablissementService
  ) {
    this.uefrForm = this.fb.group({
      nom: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      sigle: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(10)]],
      adresse: ['', [Validators.required, Validators.maxLength(200)]],
      contact: ['', [Validators.required, Validators.maxLength(50)]],
      logo: [''],
      lien: [''],
      etablissementId: [0, [Validators.required, Validators.min(1)]]
    });
    
    this.filterForm = this.fb.group({
      search: [''],
      etablissementId: [0],
      sortBy: ['nom'],
      sortOrder: ['asc']
    });
    
    // Écouter les changements de filtres
    this.filterForm.valueChanges.subscribe(() => {
      if (this.useServerSideSearch) {
        this.searchUefrs();
      } else {
        this.applyFilters();
      }
    });
  }

  ngOnInit(): void {
    this.loadEtablissements();
    this.searchUefrs();
  }

  loadUefrs(): void {
    this.isLoading = true;
    this.uefrService.getAll().subscribe({
      next: (data) => {
        this.uefrs = data;
        this.filteredUefrs = [...data];
        this.totalItems = data.length;
        this.totalPages = Math.ceil(this.totalItems / this.itemsPerPage);
        this.isLoading = false;
        this.applyFilters();
      },
      error: (error) => {
        console.error('Erreur lors du chargement:', error);
        this.isLoading = false;
      }
    });
  }

  loadEtablissements(): void {
    this.etablissementService.getAll().subscribe({
      next: (data) => {
        this.etablissements = data;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des établissements:', error);
      }
    });
  }

  // ========== SERVER-SIDE SEARCH ==========
  searchUefrs(): void {
    this.isLoading = true;
    
    const params: UefrSearchParams = {
      search: this.filterForm.get('search')?.value || undefined,
      etablissementId: this.filterForm.get('etablissementId')?.value || undefined,
      page: this.currentPage,
      size: this.itemsPerPage,
      sortBy: this.filterForm.get('sortBy')?.value,
      sortOrder: this.filterForm.get('sortOrder')?.value
    };
    
    this.uefrService.search(params).subscribe({
      next: (result) => {
        this.uefrs = result.content;
        this.filteredUefrs = result.content;
        this.totalItems = result.totalElements;
        this.totalPages = result.totalPages;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erreur lors de la recherche:', error);
        this.isLoading = false;
        this.useServerSideSearch = false;
        this.loadUefrs();
      }
    });
  }

  // ========== CLIENT-SIDE FILTERS ==========
  applyFilters(): void {
    const filters = this.filterForm.value;
    
    let filtered = [...this.uefrs];
    
    // Filtre par recherche texte
    if (filters.search) {
      const searchTerm = filters.search.toLowerCase();
      filtered = filtered.filter(uefr => 
        uefr.nom.toLowerCase().includes(searchTerm) ||
        uefr.sigle.toLowerCase().includes(searchTerm) ||
        uefr.adresse.toLowerCase().includes(searchTerm) ||
        uefr.contact.toLowerCase().includes(searchTerm)
      );
    }
    
    // Filtre par Établissement
    if (filters.etablissementId && filters.etablissementId > 0) {
      filtered = filtered.filter(uefr => uefr.etablissementId === filters.etablissementId);
    }
    
    // Tri
    filtered = this.sortData(filtered, filters.sortBy, filters.sortOrder);
    
    this.filteredUefrs = filtered;
    this.totalItems = filtered.length;
    this.totalPages = Math.ceil(this.totalItems / this.itemsPerPage);
    this.currentPage = 1;
  }
  
  resetFilters(): void {
    this.filterForm.reset({
      search: '',
      etablissementId: 0,
      sortBy: 'nom',
      sortOrder: 'asc'
    });
  }
  
  private sortData(data: Uefr[], field: string, direction: 'asc' | 'desc'): Uefr[] {
    return [...data].sort((a, b) => {
      let valueA = (a as any)[field];
      let valueB = (b as any)[field];
      
      if (field === 'etablissementId') {
        valueA = this.getEtablissementName(a.etablissementId);
        valueB = this.getEtablissementName(b.etablissementId);
      }
      
      if (typeof valueA === 'string') {
        valueA = valueA.toLowerCase();
        valueB = valueB.toLowerCase();
      }
      
      if (valueA < valueB) {
        return direction === 'asc' ? -1 : 1;
      }
      if (valueA > valueB) {
        return direction === 'asc' ? 1 : -1;
      }
      return 0;
    });
  }

  // ========== PAGINATION ==========
  get paginatedUefrs(): Uefr[] {
    if (this.useServerSideSearch) {
      return this.filteredUefrs;
    }
    
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    return this.filteredUefrs.slice(startIndex, endIndex);
  }
  
  changePage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      
      if (this.useServerSideSearch) {
        this.searchUefrs();
      }
      
      setTimeout(() => {
        document.getElementById('uefrs-table')?.scrollIntoView({ 
          behavior: 'smooth', 
          block: 'start' 
        });
      }, 100);
    }
  }
  
  changeItemsPerPage(items: number): void {
    this.itemsPerPage = items;
    this.currentPage = 1;
    
    if (this.useServerSideSearch) {
      this.searchUefrs();
    } else {
      this.applyFilters();
    }
  }
  
  getPageNumbers(): number[] {
    const pages = [];
    const maxVisiblePages = 5;
    
    let startPage = Math.max(1, this.currentPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(this.totalPages, startPage + maxVisiblePages - 1);
    
    if (endPage - startPage + 1 < maxVisiblePages) {
      startPage = Math.max(1, endPage - maxVisiblePages + 1);
    }
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    
    return pages;
  }

  // ========== UTILITY METHODS ==========
  getEtablissementName(etablissementId: number): string {
    if (!this.etablissements || this.etablissements.length === 0) return 'Chargement...';
    
    const etablissement = this.etablissements.find(e => e.id === etablissementId);
    return etablissement ? `${etablissement.nom} (${etablissement.sigle})` : `Établissement #${etablissementId}`;
  }
  
  getEtablissementById(id: number): Etablissement | undefined {
    return this.etablissements.find(e => e.id === id);
  }

  // ========== CRUD OPERATIONS ==========
  editUefr(uefr: Uefr): void {
    this.uefrId = uefr.id ?? null;
    this.isEditMode = true;
    
    this.uefrForm.patchValue({
      nom: uefr.nom,
      sigle: uefr.sigle,
      adresse: uefr.adresse,
      contact: uefr.contact,
      logo: uefr.logo || '',
      lien: uefr.lien || '',
      etablissementId: uefr.etablissementId
    });
    
    document.getElementById('form-uefr')?.scrollIntoView({ 
      behavior: 'smooth' 
    });
  }

  saveUefr(): void {
    if (this.uefrForm.invalid) {
      Object.keys(this.uefrForm.controls).forEach(key => {
        const control = this.uefrForm.get(key);
        control?.markAsTouched();
      });
      return;
    }

    const uefrData: Uefr = this.uefrForm.value;

    if (this.isEditMode && this.uefrId) {
      this.uefrService.update(this.uefrId, uefrData)
        .subscribe({
          next: () => {
            this.searchUefrs();
            this.resetForm();
            alert('UEFR mise à jour avec succès !');
          },
          error: (error) => {
            console.error('Erreur lors de la mise à jour:', error);
            alert('Erreur: ' + (error.error?.message || error.message));
          }
        });
    } else {
      this.uefrService.create(uefrData)
        .subscribe({
          next: () => {
            this.searchUefrs();
            this.resetForm();
            alert('UEFR créée avec succès !');
          },
          error: (error) => {
            console.error('Erreur lors de la création:', error);
            alert('Erreur: ' + (error.error?.message || error.message));
          }
        });
    }
  }

  resetForm(): void {
    this.uefrForm.reset({
      nom: '',
      sigle: '',
      adresse: '',
      contact: '',
      logo: '',
      lien: '',
      etablissementId: 0
    });
    this.uefrId = null;
    this.isEditMode = false;
  }

  deleteUefr(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette UEFR ?')) {
      this.uefrService.delete(id).subscribe({
        next: () => {
          this.searchUefrs();
          alert('UEFR supprimée avec succès !');
        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
          alert('Erreur: ' + (error.error?.message || error.message));
        }
      });
    }
  }
  
  exportToCSV(): void {
    const headers = ['ID', 'Nom', 'Sigle', 'Adresse', 'Contact', 'Établissement'];
    const data = this.filteredUefrs.map(uefr => [
      uefr.id,
      uefr.nom,
      uefr.sigle,
      uefr.adresse,
      uefr.contact,
      this.getEtablissementName(uefr.etablissementId)
    ]);
    
    const csvContent = [
      headers.join(','),
      ...data.map(row => row.map(cell => `"${cell}"`).join(','))
    ].join('\n');
    
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    
    link.setAttribute('href', url);
    link.setAttribute('download', `uefrs_${new Date().toISOString().slice(0,10)}.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
  
  toggleSearchMode(): void {
    this.useServerSideSearch = !this.useServerSideSearch;
    this.currentPage = 1;
    
    if (this.useServerSideSearch) {
      this.searchUefrs();
    } else {
      this.loadUefrs();
    }
  }
}