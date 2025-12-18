import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Departement } from '../../../../models/departement.model';
import { Uefr } from '../../../../models/uefr.model';
import { DepartementService, DepartementSearchParams } from '../../../../services/departement.service';
import { UefrService } from '../../../../services/uefr.service';

@Component({
  selector: 'app-departement',
  templateUrl: './departement.component.html',
  styleUrls: ['./departement.component.css']
})
export class DepartementComponent implements OnInit {
  departements: Departement[] = [];
  filteredDepartements: Departement[] = [];
  uefrs: Uefr[] = [];
  Math = Math;
  
  departementForm: FormGroup;
  filterForm: FormGroup;
  
  departementId: number | null = null;
  isEditMode = false;
  isLoading = false;
  
  // Pagination
  currentPage: number = 1;
  itemsPerPage: number = 10;
  totalItems: number = 0;
  totalPages: number = 0;
  
  // Search mode (client-side or server-side)
  useServerSideSearch: boolean = false;

  constructor(
    private fb: FormBuilder,
    private departementService: DepartementService,
    private uefrService: UefrService
  ) {
    // Initialisation des formulaires
    this.departementForm = this.fb.group({
      nom: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      sigle: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(10)]],
      adresse: ['', [Validators.required, Validators.maxLength(200)]],
      contact: ['', [Validators.required, Validators.maxLength(50)]],
      logo: [''],
      lien: [''],
      uefrId: [0, [Validators.required, Validators.min(1)]]
    });
    
    this.filterForm = this.fb.group({
      search: [''],
      uefrId: [0],
      sortBy: ['nom'],
      sortOrder: ['asc']
    });
    
    // Écouter les changements de filtres pour recherche en temps réel
    this.filterForm.valueChanges.subscribe(() => {
      if (this.useServerSideSearch) {
        this.searchDepartements();
      } else {
        this.applyFilters();
      }
    });
  }

  ngOnInit(): void {
    this.loadUefrs();
    this.loadDepartements(); // AJOUTER CETTE LIGNE
  }

  // AJOUTER CETTE MÉTHODE
  loadDepartements(): void {
    this.isLoading = true;
    this.departementService.getAll().subscribe({
      next: (data) => {
        this.departements = data;
        this.filteredDepartements = [...data];
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

  loadUefrs(): void {
    this.uefrService.getAll().subscribe({
      next: (data) => {
        this.uefrs = data;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des UEFRs:', error);
      }
    });
  }

  // ========== SERVER-SIDE SEARCH ==========
  searchDepartements(): void {
    this.isLoading = true;
    
    const params: DepartementSearchParams = {
      search: this.filterForm.get('search')?.value || undefined,
      uefrId: this.filterForm.get('uefrId')?.value || undefined,
      page: this.currentPage,
      size: this.itemsPerPage,
      sortBy: this.filterForm.get('sortBy')?.value,
      sortOrder: this.filterForm.get('sortOrder')?.value
    };
    
    this.departementService.search(params).subscribe({
      next: (result) => {
        this.departements = result.content;
        this.filteredDepartements = result.content;
        this.totalItems = result.totalElements;
        this.totalPages = result.totalPages;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erreur lors de la recherche:', error);
        this.isLoading = false;
        // Fallback to client-side if server search fails
        this.useServerSideSearch = false;
        this.loadDepartements(); // CORRECTION ICI
      }
    });
  }

  // ========== CLIENT-SIDE FILTERS ==========
  applyFilters(): void {
    const filters = this.filterForm.value;
    
    let filtered = [...this.departements];
    
    // Filtre par recherche texte
    if (filters.search) {
      const searchTerm = filters.search.toLowerCase();
      filtered = filtered.filter(dept => 
        dept.nom.toLowerCase().includes(searchTerm) ||
        dept.sigle.toLowerCase().includes(searchTerm) ||
        dept.adresse.toLowerCase().includes(searchTerm) ||
        dept.contact.toLowerCase().includes(searchTerm)
      );
    }
    
    // Filtre par UEFR
    if (filters.uefrId && filters.uefrId > 0) {
      filtered = filtered.filter(dept => dept.uefrId === filters.uefrId);
    }
    
    // Tri
    filtered = this.sortData(filtered, filters.sortBy, filters.sortOrder);
    
    this.filteredDepartements = filtered;
    this.totalItems = filtered.length;
    this.totalPages = Math.ceil(this.totalItems / this.itemsPerPage);
    this.currentPage = 1; // Retour à la première page
  }
  
  resetFilters(): void {
    this.filterForm.reset({
      search: '',
      uefrId: 0,
      sortBy: 'nom',
      sortOrder: 'asc'
    });
  }
  
  private sortData(data: Departement[], field: string, direction: 'asc' | 'desc'): Departement[] {
    return [...data].sort((a, b) => {
      let valueA = (a as any)[field];
      let valueB = (b as any)[field];
      
      if (field === 'uefrId') {
        valueA = this.getUefrName(a.uefrId);
        valueB = this.getUefrName(b.uefrId);
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
  get paginatedDepartements(): Departement[] {
    if (this.useServerSideSearch) {
      return this.filteredDepartements;
    }
    
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    return this.filteredDepartements.slice(startIndex, endIndex);
  }
  
  changePage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      
      if (this.useServerSideSearch) {
        this.searchDepartements();
      }
      
      // Scroll vers le haut du tableau
      setTimeout(() => {
        document.getElementById('departements-table')?.scrollIntoView({ 
          behavior: 'smooth', 
          block: 'start' 
        });
      }, 100);
    }
  }
  
  changeItemsPerPage(event: any): void {
    const items = parseInt(event?.target?.value || event, 10);
    if (!isNaN(items)) {
      this.itemsPerPage = items;
      this.currentPage = 1;
      
      if (this.useServerSideSearch) {
        this.searchDepartements();
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

  // ========== UTILITY METHODS ==========
  getUefrName(uefrId: number): string {
    if (!this.uefrs || this.uefrs.length === 0) return 'Chargement...';
    
    const uefr = this.uefrs.find(u => u.id === uefrId);
    return uefr ? `${uefr.nom} (${uefr.sigle})` : `UEFR #${uefrId}`;
  }
  
  getUefrById(id: number): Uefr | undefined {
    return this.uefrs.find(u => u.id === id);
  }

  // ========== CRUD OPERATIONS ==========
  editDepartement(departement: Departement): void {
    this.departementId = departement.id ?? null;
    this.isEditMode = true;
    
    this.departementForm.patchValue({
      nom: departement.nom,
      sigle: departement.sigle,
      adresse: departement.adresse,
      contact: departement.contact,
      logo: departement.logo || '',
      lien: departement.lien || '',
      uefrId: departement.uefrId
    });
    
    document.getElementById('form-departement')?.scrollIntoView({ 
      behavior: 'smooth' 
    });
  }

  saveDepartement(): void {
    if (this.departementForm.invalid) {
      Object.keys(this.departementForm.controls).forEach(key => {
        const control = this.departementForm.get(key);
        control?.markAsTouched();
      });
      return;
    }

    const departementData: any = this.departementForm.value;

    if (this.isEditMode && this.departementId) {
      this.departementService.update(this.departementId, departementData)
        .subscribe({
          next: () => {
            this.searchDepartements();
            this.resetForm();
            alert('Département mis à jour avec succès !');
          },
          error: (error) => {
            console.error('Erreur lors de la mise à jour:', error);
            alert('Erreur: ' + (error.error?.message || error.message));
          }
        });
    } else {
      this.departementService.create(departementData)
        .subscribe({
          next: () => {
            this.searchDepartements();
            this.resetForm();
            alert('Département créé avec succès !');
          },
          error: (error) => {
            console.error('Erreur lors de la création:', error);
            alert('Erreur: ' + (error.error?.message || error.message));
          }
        });
    }
  }

  resetForm(): void {
    this.departementForm.reset({
      nom: '',
      sigle: '',
      adresse: '',
      contact: '',
      logo: '',
      lien: '',
      uefrId: 0
    });
    this.departementId = null;
    this.isEditMode = false;
  }

  deleteDepartement(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce département ?')) {
      this.departementService.delete(id).subscribe({
        next: () => {
          this.searchDepartements();
          alert('Département supprimé avec succès !');
        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
          alert('Erreur: ' + (error.error?.message || error.message));
        }
      });
    }
  }
  
  exportToCSV(): void {
    const headers = ['ID', 'Nom', 'Sigle', 'Adresse', 'Contact', 'UEFR'];
    const data = this.filteredDepartements.map(dept => [
      dept.id,
      dept.nom,
      dept.sigle,
      dept.adresse,
      dept.contact,
      this.getUefrName(dept.uefrId)
    ]);
    
    const csvContent = [
      headers.join(','),
      ...data.map(row => row.map(cell => `"${cell}"`).join(','))
    ].join('\n');
    
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    
    link.setAttribute('href', url);
    link.setAttribute('download', `departements_${new Date().toISOString().slice(0,10)}.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
  
  toggleSearchMode(): void {
    this.useServerSideSearch = !this.useServerSideSearch;
    this.currentPage = 1;
    
    if (this.useServerSideSearch) {
      this.searchDepartements();
    } else {
      this.loadDepartements(); // CORRECTION ICI
    }
  }
  onFileSelected(event: any): void {
  const file = event.target.files[0];
  if (file) {
    // Limiter la taille à 2MB
    if (file.size > 2 * 1024 * 1024) {
      alert('Le fichier est trop volumineux. Taille maximale : 2MB');
      this.departementForm.get('logo')?.setValue('');
      return;
    }
    
    // Vérifier le type de fichier
    const allowedTypes = ['image/jpeg', 'image/png', 'image/gif'];
    if (!allowedTypes.includes(file.type)) {
      alert('Format de fichier non supporté. Utilisez JPG, PNG ou GIF.');
      this.departementForm.get('logo')?.setValue('');
      return;
    }
    
    // Ici vous pouvez traiter le fichier (upload, conversion, etc.)
    // Pour l'instant, on garde juste le nom du fichier
    this.departementForm.get('logo')?.setValue(file.name);
  }
}
}