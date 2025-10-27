import { Component, OnInit } from '@angular/core';
// --- AJOUT ---
import { ProgressionService, MatiereStatut } from '../../../../services/progression.service'; // Adaptez le chemin

@Component({
  selector: 'app-matieres-reprendre',
  templateUrl: './matieres-reprendre.component.html',
  styleUrls: ['./matieres-reprendre.component.css']
})
export class MatieresReprendreComponent implements OnInit {

  // --- MODIFICATION ---
  // On utilise notre nouvelle interface MatiereStatut
  matieres: MatiereStatut[] = [];
  filteredMatieres: MatiereStatut[] = [];
  searchTerm: string = '';

  // --- MODIFICATION ---
  // On injecte le nouveau ProgressionService
  constructor(private progressionService: ProgressionService) {}

  ngOnInit(): void {
    // --- MODIFICATION ---
    // On appelle la nouvelle méthode du service
    this.progressionService.getMesMatieres().subscribe({
      next: (data) => {
        console.log('Matières à reprendre reçues :', data);
        this.matieres = data;
        this.filteredMatieres = data;
      },
      error: (err) => {
        console.error("Erreur lors du chargement des matières de l'étudiant", err);
      }
    });
  }

  filterMatieres(): void {
    const term = this.searchTerm ? this.searchTerm.toLowerCase() : '';
    if (!term) {
      this.filteredMatieres = this.matieres;
      return;
    }
    
    this.filteredMatieres = this.matieres.filter(m =>
      m.ue.toLowerCase().includes(term) ||
      m.ec.toLowerCase().includes(term) ||
      m.statut.toLowerCase().includes(term)
    );
  }
}
