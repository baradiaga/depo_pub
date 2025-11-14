// Fichier : resultat-diagnostic.component.ts

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-resultat-diagnostic',
  templateUrl: './resultat-diagnostic.component.html',
  styleUrls: ['./resultat-diagnostic.component.scss']
})
export class ResultatDiagnosticComponent implements OnInit {

  resultat: any; // Pour stocker les données de résultat

  constructor(private router: Router) {
    // On récupère les données passées via l'état de navigation
    const navigation = this.router.getCurrentNavigation();
    this.resultat = navigation?.extras?.state?.['resultat'];
  }

  ngOnInit(): void {
    // Si l'utilisateur arrive sur cette page directement sans passer par un test,
    // les données de résultat n'existeront pas. On le redirige.
    if (!this.resultat) {
      console.warn("Aucun résultat trouvé, redirection vers le tableau de bord.");
      this.router.navigate(['/app/curriculum/matieres']);
    } else {
      console.log("Résultats affichés:", this.resultat);
    }
  }

  // Méthode pour obtenir une classe de couleur en fonction du score
  getScoreColorClass(score: number): string {
    if (score < 50) return 'text-danger';
    if (score < 75) return 'text-warning';
    return 'text-success';
  }
}
