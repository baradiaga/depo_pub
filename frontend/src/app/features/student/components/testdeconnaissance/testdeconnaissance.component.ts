// Fichier : src/app/features/student/components/testdeconnaissance/testdeconnaissance.component.ts (Version mise à jour)

import { Component, OnInit } from '@angular/core';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';

// On va supposer que ces interfaces viennent de votre service.
// On simplifie pour n'avoir besoin que de la liste des matières.
import { RecommandationTestService, MatiereSelection } from '../../../../services/recommandation-test.service';

@Component({
  selector: 'app-testdeconnaissance',
  templateUrl: './testdeconnaissance.component.html',
  styleUrls: ['./testdeconnaissance.component.css']
})
export class TestdeconnaissanceComponent implements OnInit {

  matieres$!: Observable<MatiereSelection[]>;
  
  // On garde juste une trace de l'ID sélectionné.
  selectedMatiereId: number | null = null;
  
  isLoading = true;

  constructor(
    private recoTestService: RecommandationTestService,
    private toastr: ToastrService,
    private router: Router
  ) { }

  ngOnInit(): void {
    // On charge la liste des matières disponibles pour l'étudiant.
    this.matieres$ = this.recoTestService.getListeMatieres().pipe(
      catchError(err => {
        this.toastr.error("Impossible de charger la liste des matières.", "Erreur Réseau");
        this.isLoading = false;
        return of([]);
      })
    );

    this.matieres$.subscribe(() => {
      this.isLoading = false;
    });
  }

  // Met à jour l'ID de la matière sélectionnée.
  onSelectMatiere(event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    this.selectedMatiereId = selectElement.value ? Number(selectElement.value) : null;
  }

  // ====================================================================
  // === NOUVELLE LOGIQUE POUR LANCER LE TEST GLOBAL                  ===
  // ====================================================================
  /**
   * Redirige l'utilisateur vers la page de test en passant l'ID de la matière.
   */
  lancerTestDeConnaissance(): void {
    if (!this.selectedMatiereId) {
      this.toastr.warning("Veuillez d'abord sélectionner une matière.");
      return;
    }

    this.toastr.info("Préparation du test de connaissance...", "Lancement");
    
    // On navigue vers une nouvelle route qui sera dédiée à l'affichage du test de connaissance.
    // On passe l'ID de la matière dans l'URL.
    this.router.navigate(['/app/student/test-connaissance/passer', this.selectedMatiereId]);
  }
}
