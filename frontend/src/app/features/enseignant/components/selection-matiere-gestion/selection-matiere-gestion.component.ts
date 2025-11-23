// Fichier : src/app/features/enseignant/components/selection-matiere-gestion/selection-matiere-gestion.component.ts

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

// On importe le service qui contient la méthode 'getMesMatieres'
// Adaptez le nom et le chemin si nécessaire
import { RecommandationTestService, MatiereSelection } from '../../../../services/recommandation-test.service';

@Component({
  selector: 'app-selection-matiere-gestion',
  templateUrl: './selection-matiere-gestion.component.html',
  styleUrls: ['./selection-matiere-gestion.component.scss']
})
export class SelectionMatiereGestionComponent implements OnInit {

  matieres: MatiereSelection[] = [];
  isLoading = true;
  errorMessage: string | null = null;

  constructor(
    private recommandationService: RecommandationTestService, // Adaptez le nom du service
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.chargerMatieres();
  }

  chargerMatieres(): void {
    this.isLoading = true;
    this.recommandationService.getListeMatieres().subscribe({
      next: (data) => {
        this.matieres = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = "Erreur lors du chargement de vos matières.";
        this.toastr.error(this.errorMessage);
        console.error(err);
      }
    });
  }

  // Cette méthode est appelée quand l'enseignant clique sur le bouton "Gérer"
  gererContenu(matiereId: number): void {
    // On navigue vers la page de gestion en passant l'ID de la matière
    this.router.navigate(['/app/enseignant/gestion-contenu', matiereId]);
  }
}
