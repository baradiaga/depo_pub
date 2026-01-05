// Fichier : src/app/pages/matieres-reprendre/matieres-reprendre.component.ts

import { Component, OnInit } from '@angular/core';
import { ProgressionService, MatiereInscrite } from '../../../../services/progression.service';
import { AuthService } from '../../../../services/auth.service';

@Component({
  selector: 'app-matieres-reprendre',
  templateUrl: './matieres-reprendre.component.html',
  styleUrls: ['./matieres-reprendre.component.css']
})
export class MatieresReprendreComponent implements OnInit {

  toutesLesMatieres: MatiereInscrite[] = [];
  matieresFiltrees: MatiereInscrite[] = [];
  
  isLoading = true;
  isPendingValidation = false; // Flag pour l'affichage du message d'attente
  errorMessage: string | null = null;
  etudiantNom: string | null = null;

  constructor(
    private progressionService: ProgressionService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.chargerDonnees();
  }

  chargerDonnees(): void {
    this.isLoading = true;
    this.etudiantNom = this.authService.getUserFullName();

    this.progressionService.getMesMatieres().subscribe({
      next: (data) => {
        this.toutesLesMatieres = data;
        this.matieresFiltrees = data;
        
        // LOGIQUE PRO : Si la liste est vide, on considère l'inscription en attente
        this.isPendingValidation = data.length === 0;
        
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = "Impossible de charger vos inscriptions.";
        this.isLoading = false;
      }
    });
  }

  onSearch(event: Event): void {
    const searchTerm = (event.target as HTMLInputElement).value.toLowerCase();
    this.matieresFiltrees = this.toutesLesMatieres.filter(matiere =>
      matiere.nomEc.toLowerCase().includes(searchTerm) ||
      matiere.codeEc.toLowerCase().includes(searchTerm) ||
      matiere.nomUe.toLowerCase().includes(searchTerm)
    );
  }
}
