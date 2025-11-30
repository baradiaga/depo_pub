import { Component, OnInit } from '@angular/core';
import { CategorieService, Categorie } from '../services/categorie.service';
import { EchelleConnaissanceService, EchelleConnaissance } from '../services/echelle-connaissance.service';

@Component({
  selector: 'app-categorie',
  templateUrl: './categorie.component.html',
  styleUrls: ['./categorie.component.css']
})
export class CategorieComponent implements OnInit {
  categories: Categorie[] = [];
  nouvelleCategorie: Categorie = { nom: '', echelleId: 0 };
  editionCategorie: Categorie | null = null;

  echellesConnaissance: EchelleConnaissance[] = [];

  constructor(
    private categorieService: CategorieService,
    private echelleService: EchelleConnaissanceService
  ) {}

  ngOnInit(): void {
    this.loadEchelles();
    this.loadCategories();
  }

  loadEchelles(): void {
    this.echelleService.getAll().subscribe({
      next: (data) => this.echellesConnaissance = data,
      error: (err) => console.error('Erreur chargement échelles', err)
    });
  }

  loadCategories(): void {
    this.categorieService.getAll().subscribe({
      next: (data) => this.categories = data,
      error: (err) => console.error('Erreur chargement catégories', err)
    });
  }

  ajouterCategorie(): void {
    if (!this.nouvelleCategorie.nom.trim() || !this.nouvelleCategorie.echelleId) return;

    this.categorieService.create(this.nouvelleCategorie).subscribe({
      next: (created) => {
        this.categories.push(created);
        this.nouvelleCategorie = { nom: '', echelleId: 0 };
      },
      error: (err) => console.error('Erreur ajout catégorie', err)
    });
  }

  startEdition(categorie: Categorie): void {
    this.editionCategorie = { ...categorie };
  }

  validerModification(): void {
    if (!this.editionCategorie || !this.editionCategorie.id) return;

    this.categorieService.update(this.editionCategorie.id!, this.editionCategorie).subscribe({
      next: (updated) => {
        const index = this.categories.findIndex(c => c.id === updated.id);
        if (index !== -1) this.categories[index] = updated;
        this.editionCategorie = null;
      },
      error: (err) => console.error('Erreur modification catégorie', err)
    });
  }

  annulerModification(): void {
    this.editionCategorie = null;
  }

  supprimerCategorie(id?: number): void {
    if (!id) return;

    this.categorieService.delete(id).subscribe({
      next: () => this.categories = this.categories.filter(c => c.id !== id),
      error: (err) => console.error('Erreur suppression catégorie', err)
    });
  }
}
