// Fichier : src/app/features/student/pages/chapitre-detail/chapitre-detail.component.ts (Version finale corrigée)

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs';

// --- IMPORTS CORRIGÉS ---
// On importe le service et les nouvelles interfaces que nous avons définies.
import { ChapitreService, ChapitreAvecSections } from '../../../../services/chapitre.service';

@Component({
  selector: 'app-chapitre-detail',
  templateUrl: './chapitre-detail.component.html',
  styleUrls: ['./chapitre-detail.component.css']
})
export class ChapitreDetailComponent implements OnInit {

  // On utilise un Observable pour gérer les données de manière plus moderne et robuste.
  public chapitre$!: Observable<ChapitreAvecSections>;
  public chapitreId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router, // On injecte le Router pour la navigation
    private chapitreService: ChapitreService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    // On récupère l'ID du chapitre depuis l'URL.
    const idParam = this.route.snapshot.paramMap.get('id');
    
    if (idParam) {
      const id = +idParam;
      this.chapitreId = id;
      // On appelle la nouvelle méthode du service et on assigne le résultat à notre Observable.
      this.chapitre$ = this.chapitreService.getChapitreComplet(id);
    } else {
      this.toastr.error("ID de chapitre manquant dans l'URL.", "Erreur de navigation");
      // Si pas d'ID, on redirige l'utilisateur vers une page sûre.
      this.router.navigate(['/student/parcours']); 
    }
  }

  /**
   * Redirige l'utilisateur vers la page du test correspondant à ce chapitre.
   */
  passerLeTest(): void {
    if (this.chapitreId) {
      console.log(`Navigation vers le test du chapitre ${this.chapitreId}`);
      // L'URL correspond à la route que nous avons déjà définie pour le TestComponent.
      this.router.navigate(['/app/student/test/passer', this.chapitreId]);
    } else {
      this.toastr.error("Impossible de lancer le test, l'ID du chapitre est introuvable.", "Erreur");
    }
  }
}
