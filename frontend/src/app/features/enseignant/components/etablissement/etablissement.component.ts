import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Etablissement } from '../../../../models/etablissement.model';
import { EtablissementService, EtablissementSearchParams } from '../../../../services/etablissement.service';

@Component({
  selector: 'app-etablissement',
  templateUrl: './etablissement.component.html',
  styleUrls: ['./etablissement.component.css']
})
export class EtablissementComponent implements OnInit {
  etablissements: Etablissement[] = [];
  filteredEtablissements: Etablissement[] = [];
  Math = Math;
  etablissementForm: FormGroup;
  filterForm: FormGroup;
  
  etablissementId: number | null = null;
  isEditMode = false;
  isLoading = false;
  
  // Variables pour le logo
  selectedLogoFile: File | null = null;
  currentLogoName: string | null = null;
  logoPreviewUrl: string | null = null;
  
  // Pagination
  currentPage: number = 1;
  itemsPerPage: number = 10;
  totalItems: number = 0;
  totalPages: number = 0;
  
  // Search mode
  useServerSideSearch: boolean = false;

  constructor(
    private fb: FormBuilder,
    private etablissementService: EtablissementService
  ) {
    this.etablissementForm = this.fb.group({
      nom: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      sigle: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(20)]],
      adresse: ['', [Validators.required, Validators.maxLength(200)]],
      contact: ['', [Validators.required, Validators.maxLength(100)]],
      logo: [''], // Stockera l'URL après upload ou le nom du fichier
      lien: ['', [Validators.pattern('https?://.+')]]
    });
    
    this.filterForm = this.fb.group({
      search: [''],
      sortBy: ['nom'],
      sortOrder: ['asc']
    });
    
    this.filterForm.valueChanges.subscribe(() => {
      if (this.useServerSideSearch) {
        this.searchEtablissements();
      } else {
        this.applyFilters();
      }
    });
  }

  ngOnInit(): void {
    this.loadEtablissements();
  }

  // ========== GESTION DU LOGO ==========
  
  onLogoSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      // Validation de la taille (2MB max)
      if (file.size > 2 * 1024 * 1024) {
        alert('Le fichier est trop volumineux. Taille maximale : 2MB');
        this.resetLogoInput();
        return;
      }
      
      // Validation du type
      const allowedTypes = [
        'image/jpeg', 
        'image/png', 
        'image/gif', 
        'image/svg+xml',
        'image/webp'
      ];
      const allowedExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.svg', '.webp'];
      
      const fileExtension = '.' + file.name.split('.').pop()?.toLowerCase();
      
      if (!allowedTypes.includes(file.type) && !allowedExtensions.includes(fileExtension)) {
        alert('Format de fichier non supporté. Utilisez JPG, PNG, GIF, SVG ou WebP.');
        this.resetLogoInput();
        return;
      }
      
      // Stocker le fichier
      this.selectedLogoFile = file;
      
      // Créer une prévisualisation
      this.createLogoPreview(file);
      
      // Nettoyer le champ logo du formulaire (sera mis à jour après upload)
      this.etablissementForm.patchValue({ logo: '' });
    }
  }
  
  createLogoPreview(file: File): void {
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.logoPreviewUrl = e.target.result;
    };
    reader.readAsDataURL(file);
  }
  
  getFileSize(bytes?: number): string {
    if (bytes === undefined || bytes === null) {
      return '0 bytes';
    }
    
    if (bytes < 1024) {
      return bytes + ' bytes';
    } else if (bytes < 1048576) {
      return (bytes / 1024).toFixed(1) + ' KB';
    } else {
      return (bytes / 1048576).toFixed(1) + ' MB';
    }
  }
  
  removeCurrentLogo(): void {
    this.currentLogoName = null;
    this.logoPreviewUrl = null;
    this.etablissementForm.patchValue({ logo: '' });
  }
  
  resetLogoInput(): void {
    this.selectedLogoFile = null;
    this.logoPreviewUrl = null;
    const logoInput = document.getElementById('logo') as HTMLInputElement;
    if (logoInput) {
      logoInput.value = '';
    }
  }

  // ========== CRUD OPERATIONS ==========
  
  editEtablissement(etablissement: Etablissement): void {
    this.etablissementId = etablissement.id ?? null;
    this.isEditMode = true;
    
    // Stocker le nom du logo actuel
    this.currentLogoName = etablissement.logo ? this.extractFileName(etablissement.logo) : null;
    
    this.etablissementForm.patchValue({
      nom: etablissement.nom,
      sigle: etablissement.sigle,
      adresse: etablissement.adresse,
      contact: etablissement.contact,
      logo: etablissement.logo || '',
      lien: etablissement.lien || ''
    });
    
    // Réinitialiser la sélection de nouveau logo
    this.resetLogoInput();
    
    document.getElementById('form-etablissement')?.scrollIntoView({ 
      behavior: 'smooth' 
    });
  }
  
  extractFileName(url: string): string {
    if (!url) return '';
    // Extraire le nom du fichier de l'URL
    const parts = url.split('/');
    return parts[parts.length - 1];
  }

  saveEtablissement(): void {
    if (this.etablissementForm.invalid) {
      this.markFormGroupTouched(this.etablissementForm);
      return;
    }

    this.isLoading = true;

    // Si un nouveau logo a été sélectionné
    if (this.selectedLogoFile) {
      this.uploadLogoAndSave();
    } else {
      // Sinon, sauvegarder directement
      this.saveEtablissementData();
    }
  }
  
  uploadLogoAndSave(): void {
    if (!this.selectedLogoFile) {
      this.saveEtablissementData();
      return;
    }

    // Solution temporaire : stocker juste le nom du fichier
    // Vous pourrez implémenter l'upload plus tard
    const etablissementData = {
      ...this.etablissementForm.value,
      logo: this.selectedLogoFile.name
    };
    
    this.saveEtablissementWithData(etablissementData);
  }
  
  saveEtablissementData(): void {
    this.saveEtablissementWithData(this.etablissementForm.value);
  }
  
  saveEtablissementWithData(etablissementData: any): void {
    if (this.isEditMode && this.etablissementId) {
      this.etablissementService.update(this.etablissementId, etablissementData)
        .subscribe({
          next: () => {
            this.handleSaveSuccess();
          },
          error: (error: any) => {
            this.handleSaveError(error);
          }
        });
    } else {
      this.etablissementService.create(etablissementData)
        .subscribe({
          next: () => {
            this.handleSaveSuccess();
          },
          error: (error: any) => {
            this.handleSaveError(error);
          }
        });
    }
  }
  
  handleSaveSuccess(): void {
    this.isLoading = false;
    this.searchEtablissements();
    this.resetForm();
    alert('Établissement sauvegardé avec succès !');
  }
  
  handleSaveError(error: any): void {
    this.isLoading = false;
    console.error('Erreur sauvegarde:', error);
    
    let errorMessage = 'Erreur lors de la sauvegarde';
    if (error.error?.message) {
      errorMessage += ` : ${error.error.message}`;
    } else if (error.message) {
      errorMessage += ` : ${error.message}`;
    }
    
    alert(errorMessage);
  }

  resetForm(): void {
    this.etablissementForm.reset({
      nom: '',
      sigle: '',
      adresse: '',
      contact: '',
      logo: '',
      lien: ''
    });
    
    this.markFormGroupPristine(this.etablissementForm);
    
    this.etablissementId = null;
    this.isEditMode = false;
    
    // Réinitialiser le logo
    this.selectedLogoFile = null;
    this.currentLogoName = null;
    this.logoPreviewUrl = null;
    this.resetLogoInput();
  }
  
  markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }
  
  markFormGroupPristine(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsPristine();
      control.markAsUntouched();
      if (control instanceof FormGroup) {
        this.markFormGroupPristine(control);
      }
    });
  }

  // ========== MÉTHODES EXISTANTES ==========
  
  loadEtablissements(): void {
    this.isLoading = true;
    this.etablissementService.getAll().subscribe({
      next: (data) => {
        this.etablissements = data;
        this.filteredEtablissements = [...data];
        this.totalItems = data.length;
        this.totalPages = Math.ceil(this.totalItems / this.itemsPerPage);
        this.isLoading = false;
        this.applyFilters();
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement:', error);
        this.isLoading = false;
        alert('Erreur lors du chargement des établissements');
      }
    });
  }

  searchEtablissements(): void {
    this.isLoading = true;
    
    const params: EtablissementSearchParams = {
      search: this.filterForm.get('search')?.value || undefined,
      page: this.currentPage,
      size: this.itemsPerPage,
      sortBy: this.filterForm.get('sortBy')?.value,
      sortOrder: this.filterForm.get('sortOrder')?.value
    };
    
    this.etablissementService.search(params).subscribe({
      next: (result: any) => {
        this.etablissements = result.content;
        this.filteredEtablissements = result.content;
        this.totalItems = result.totalElements;
        this.totalPages = result.totalPages;
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Erreur lors de la recherche:', error);
        this.isLoading = false;
        this.useServerSideSearch = false;
        this.loadEtablissements();
      }
    });
  }

  applyFilters(): void {
    const filters = this.filterForm.value;
    
    let filtered = [...this.etablissements];
    
    if (filters.search) {
      const searchTerm = filters.search.toLowerCase();
      filtered = filtered.filter(etab => 
        etab.nom.toLowerCase().includes(searchTerm) ||
        etab.sigle.toLowerCase().includes(searchTerm) ||
        etab.adresse.toLowerCase().includes(searchTerm) ||
        etab.contact.toLowerCase().includes(searchTerm) ||
        (etab.lien && etab.lien.toLowerCase().includes(searchTerm))
      );
    }
    
    filtered = this.sortData(filtered, filters.sortBy, filters.sortOrder);
    
    this.filteredEtablissements = filtered;
    this.totalItems = filtered.length;
    this.totalPages = Math.ceil(this.totalItems / this.itemsPerPage);
    this.currentPage = 1;
  }
  
  resetFilters(): void {
    this.filterForm.reset({
      search: '',
      sortBy: 'nom',
      sortOrder: 'asc'
    });
  }
  
  private sortData(data: Etablissement[], field: string, direction: 'asc' | 'desc'): Etablissement[] {
    return [...data].sort((a, b) => {
      let valueA = (a as any)[field];
      let valueB = (b as any)[field];
      
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

  get paginatedEtablissements(): Etablissement[] {
    if (this.useServerSideSearch) {
      return this.filteredEtablissements;
    }
    
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    return this.filteredEtablissements.slice(startIndex, endIndex);
  }
  
  changePage(page: number): void {
    if (page >= 1 && page <= this.totalPages && !this.isLoading) {
      this.currentPage = page;
      
      if (this.useServerSideSearch) {
        this.searchEtablissements();
      }
      
      setTimeout(() => {
        const tableElement = document.getElementById('etablissements-table');
        if (tableElement) {
          tableElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
      }, 100);
    }
  }
  
  changeItemsPerPage(event: any): void {
    const items = parseInt(event?.target?.value || event, 10);
    if (!isNaN(items) && items > 0 && !this.isLoading) {
      this.itemsPerPage = items;
      this.currentPage = 1;
      
      if (this.useServerSideSearch) {
        this.searchEtablissements();
      } else {
        this.applyFilters();
      }
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

  deleteEtablissement(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cet établissement ?') && !this.isLoading) {
      this.isLoading = true;
      this.etablissementService.delete(id).subscribe({
        next: () => {
          this.isLoading = false;
          this.searchEtablissements();
          alert('Établissement supprimé avec succès !');
        },
        error: (error: any) => {
          this.isLoading = false;
          console.error('Erreur lors de la suppression:', error);
          alert('Erreur lors de la suppression de l\'établissement');
        }
      });
    }
  }
  
  exportToCSV(): void {
    try {
      const headers = ['ID', 'Nom', 'Sigle', 'Adresse', 'Contact', 'Logo', 'Site Web'];
      const data = this.filteredEtablissements.map(etab => [
        etab.id,
        etab.nom,
        etab.sigle,
        etab.adresse,
        etab.contact,
        etab.logo || '',
        etab.lien || ''
      ]);
      
      const csvContent = [
        headers.join(','),
        ...data.map(row => row.map(cell => {
          const cellStr = String(cell).replace(/"/g, '""');
          return `"${cellStr}"`;
        }).join(','))
      ].join('\n');
      
      const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
      const link = document.createElement('a');
      const url = URL.createObjectURL(blob);
      
      link.setAttribute('href', url);
      link.setAttribute('download', `etablissements_${new Date().toISOString().slice(0,10)}.csv`);
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);
      
      alert('Export CSV réussi !');
    } catch (error: any) {
      console.error('Erreur lors de l\'export CSV:', error);
      alert('Erreur lors de l\'export CSV');
    }
  }
  
  toggleSearchMode(): void {
    if (this.isLoading) return;
    
    this.useServerSideSearch = !this.useServerSideSearch;
    this.currentPage = 1;
    
    if (this.useServerSideSearch) {
      this.searchEtablissements();
    } else {
      this.loadEtablissements();
    }
  }
}