// Fichier : src/app/features/student/components/resultats/resultats.component.ts (Version mise à jour)

import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { TestService, HistoriqueResultat } from '../../../../services/test.service'; // <-- IMPORT MIS À JOUR

@Component({
  selector: 'app-resultats',
  templateUrl: './resultats.component.html',
  styleUrls: ['./resultats.component.css']
})
export class ResultatsComponent implements OnInit {

  // On utilise un Observable pour une gestion plus propre des données asynchrones
  public historique$!: Observable<HistoriqueResultat[]>;
  public isLoading = true;

  constructor(private testService: TestService) { }

  ngOnInit(): void {
    // On appelle le service pour récupérer l'historique
    this.historique$ = this.testService.getMonHistorique();
    
    // On peut désactiver le spinner une fois que les données sont reçues (géré par le pipe async)
    // Pour un contrôle plus fin, on pourrait utiliser .subscribe()
    this.historique$.subscribe(() => this.isLoading = false);
  }

  /**
   * Retourne une classe CSS (Bootstrap) en fonction du pourcentage de réussite.
   * @param pourcentage Le score en pourcentage.
   * @returns Une chaîne de caractères représentant la classe CSS.
   */
  getBadgeClass(pourcentage: number): string {
    if (pourcentage >= 75) {
      return 'bg-success'; // Vert pour un bon score
    } else if (pourcentage >= 50) {
      return 'bg-warning text-dark'; // Orange pour un score moyen
    } else {
      return 'bg-danger'; // Rouge pour un score faible
    }
  }
}
