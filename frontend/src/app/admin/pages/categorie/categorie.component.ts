import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-categorie',
  templateUrl: './categorie.component.html',
  styleUrls: ['./categorie.component.css']
})
export class CategorieComponent implements OnInit {
  categories: any[] = []; // Liste des catégories
  nouvelleCategorie = { nom: '', echelleConnaissance: '' }; // Formulaire d'ajout
  editionCategorie: any = null; // Catégorie en cours de modification

  // Liste prédéfinie des échelles de connaissance
  echellesConnaissance: string[] = ['[0% - 33%]', '[34% - 66%]', '[67% - 100%]'];

  ngOnInit(): void {
    // Données simulées pour le test initial
    this.categories = [
      { id: 1, nom: 'categorie 1', echelleConnaissance: '[0% - 33%]' },
      { id: 2, nom: 'categorie 2', echelleConnaissance: '[34% - 66%]' },
      { id: 3, nom: 'categorie 3', echelleConnaissance: '[67% - 100%]' }
    ];
  }

  ajouterCategorie(): void {
    if (!this.nouvelleCategorie.nom.trim() || !this.nouvelleCategorie.echelleConnaissance.trim()) {
      return;
    }

    const newId = this.categories.length > 0 ? Math.max(...this.categories.map(c => c.id)) + 1 : 1;
    const nouvelle = {
      id: newId,
      nom: this.nouvelleCategorie.nom.trim(),
      echelleConnaissance: this.nouvelleCategorie.echelleConnaissance.trim()
    };

    this.categories.push(nouvelle);
    this.nouvelleCategorie = { nom: '', echelleConnaissance: '' };
  }

  startEdition(categorie: any): void {
    this.editionCategorie = { ...categorie };
  }

  validerModification(): void {
    const index = this.categories.findIndex(c => c.id === this.editionCategorie.id);
    if (index !== -1) {
      this.categories[index] = this.editionCategorie;
    }
    this.editionCategorie = null;
  }

  annulerModification(): void {
    this.editionCategorie = null;
  }

  supprimerCategorie(id: number): void {
    this.categories = this.categories.filter(c => c.id !== id);
    if (this.editionCategorie?.id === id) {
      this.editionCategorie = null;
    }
  }
}
