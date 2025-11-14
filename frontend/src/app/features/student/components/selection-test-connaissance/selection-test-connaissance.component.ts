// Fichier : frontend/src/app/features/student/components/selection-test-connaissance/selection-test-connaissance.component.ts

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router'; // <-- Importer le Router
import { RecommandationTestService } from '../../../../services/recommandation-test.service';
import { ToastrService } from 'ngx-toastr';

// Interface pour typer nos données
interface Matiere {
  id: number;
  nom: string;
  // Ajoutez d'autres propriétés si nécessaire
}

@Component({
  selector: 'app-selection-test-connaissance',
  templateUrl: './selection-test-connaissance.component.html',
  styleUrls: ['./selection-test-connaissance.component.scss']
})
export class SelectionTestConnaissanceComponent implements OnInit {

  matieres: Matiere[] = [];
  isLoading = true;
  matiereSelectionneeId: number | null = null; // <-- NOUVELLE PROPRIÉTÉ pour stocker l'ID

  constructor(
    private recommandationTestService: RecommandationTestService,
    private toastr: ToastrService,
    private router: Router // <-- Injecter le Router
  ) { }

  ngOnInit(): void {
    this.chargerMatieres();
  }

  chargerMatieres(): void {
    this.isLoading = true;
    this.recommandationTestService.getListeMatieres().subscribe({
      next: (data) => {
        this.matieres = data;
        this.isLoading = false;
        console.log('[FRONTEND] Matières chargées avec succès !', data);
      },
      error: (err) => {
        this.isLoading = false;
        this.toastr.error('Erreur lors de la récupération des matières.', 'Erreur');
        console.error(err);
      }
    });
  }

  // ====================================================================
  // === NOUVELLE MÉTHODE POUR DÉMARRER LE TEST                       ===
  // ====================================================================
  demarrerTest(): void {
    if (!this.matiereSelectionneeId) {
      this.toastr.warning('Veuillez d\'abord sélectionner une matière.', 'Action requise');
      return;
    }

    console.log(`[FRONTEND] Démarrage du test pour la matière ID: ${this.matiereSelectionneeId}`);
    this.toastr.info(`Préparation du test pour la matière sélectionnée...`, 'Lancement');

    // Navigation vers la page du test avec l'ID de la matière en paramètre
    this.router.navigate(['app/student/dashboard/test-connaissance', this.matiereSelectionneeId]);
  }
}
