// Fichier : src/app/pages/matieres-reprendre/matieres-reprendre.component.ts

import { Component, OnInit } from '@angular/core';
import { ProgressionService, MatiereInscrite } from '../../../../services/progression.service';
import { AuthService } from '../../../../services/auth.service'; // Pour le nom de l'étudiant

@Component({
  selector: 'app-matieres-reprendre',
  templateUrl: './matieres-reprendre.component.html',
  styleUrls: ['./matieres-reprendre.component.css']
})
export class MatieresReprendreComponent implements OnInit {

  toutesLesMatieres: MatiereInscrite[] = [];
  matieresFiltrees: MatiereInscrite[] = [];
  
  isLoading = true;
  errorMessage: string | null = null;
  
  etudiantNom: string | null = null; // Pour le titre

  constructor(
    private progressionService: ProgressionService,
    private authService: AuthService // Injecter AuthService
  ) {}

  ngOnInit(): void {
    this.isLoading = true;
    this.etudiantNom = this.authService.getUserFullName(); // Récupérer le nom complet

    this.progressionService.getMesMatieres().subscribe({
      next: (data) => {
        this.toutesLesMatieres = data;
        this.matieresFiltrees = data; // Au début, on affiche tout
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = "Une erreur est survenue lors du chargement.";
        this.isLoading = false;
      }
    });
  }

  // Fonction de recherche
  onSearch(event: Event): void {
    const searchTerm = (event.target as HTMLInputElement).value.toLowerCase();
    if (!searchTerm) {
      this.matieresFiltrees = this.toutesLesMatieres;
      return;
    }
    this.matieresFiltrees = this.toutesLesMatieres.filter(matiere =>
      matiere.nomEc.toLowerCase().includes(searchTerm) ||
      matiere.codeEc.toLowerCase().includes(searchTerm) ||
      matiere.nomUe.toLowerCase().includes(searchTerm)
    );
  }
}
