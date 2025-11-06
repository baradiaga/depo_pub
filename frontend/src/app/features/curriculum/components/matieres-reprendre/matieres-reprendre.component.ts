// Fichier : src/app/pages/matieres-reprendre/matieres-reprendre.component.ts (Version Corrigée)

import { Component, OnInit } from '@angular/core';
import { ProgressionService } from '../../../../services/progression.service';
import { ElementConstitutifResponse } from '../../../../services/models'; // On utilise l'interface standard

@Component({
  selector: 'app-matieres-reprendre',
  templateUrl: './matieres-reprendre.component.html',
  styleUrls: ['./matieres-reprendre.component.css']
})
export class MatieresReprendreComponent implements OnInit {

  matieres: ElementConstitutifResponse[] = [];
  filteredMatieres: ElementConstitutifResponse[] = [];
  searchTerm: string = '';
  isLoading = true; // On ajoute un indicateur de chargement

  constructor(private progressionService: ProgressionService) {}

  ngOnInit(): void {
    this.isLoading = true;
    this.progressionService.getMesMatieresInscrites().subscribe({
      next: (data) => {
        this.matieres = data;
        this.filteredMatieres = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error("Erreur lors du chargement des matières de l'étudiant", err);
        this.isLoading = false;
      }
    });
  }

  filterMatieres(): void {
    const term = this.searchTerm ? this.searchTerm.toLowerCase() : '';
    if (!term) {
      this.filteredMatieres = this.matieres;
      return;
    }
    
    // On filtre sur les champs qui existent vraiment
    this.filteredMatieres = this.matieres.filter(m =>
      m.nom.toLowerCase().includes(term) ||
      m.code.toLowerCase().includes(term)
    );
  }
}
