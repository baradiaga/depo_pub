import { Component, OnInit } from '@angular/core';
// --- CORRECTION : Importer les bons noms depuis le service ---
import { MatiereService, MatiereAvecDetails } from '../../../services/matiere.service';

@Component({
  selector: 'app-liste-matieres',
  templateUrl: './liste-matieres.component.html',
  styleUrls: ['./liste-matieres.component.css']
})
export class ListeMatieresComponent implements OnInit {
  
  // --- CORRECTION : Utiliser le bon type ---
  matieresAvecDetails: MatiereAvecDetails[] = [];
  
  contenuVisible: { [matiereNom: string]: boolean } = {};

  constructor(private matiereService: MatiereService) {}

  ngOnInit(): void {
    this.chargerDonneesDepuisBackend();
  }

  chargerDonneesDepuisBackend(): void {
    // --- CORRECTION : Appeler la bonne méthode du service ---
    this.matiereService.getMatieres().subscribe({
      next: (data) => {
        this.matieresAvecDetails = data;
        console.log('Données chargées depuis le backend :', this.matieresAvecDetails);
      },
      error: (err) => {
        console.error('Erreur lors du chargement des matières et chapitres :', err);
        alert('Impossible de charger les données depuis le serveur.');
      }
    });
  }

  toggleContenu(matiereNom: string): void {
    this.contenuVisible[matiereNom] = !this.contenuVisible[matiereNom];
  }

  isContenuVisible(matiereNom: string): boolean {
    return !!this.contenuVisible[matiereNom];
  }
}
