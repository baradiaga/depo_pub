// Fichier : src/app/features/student/components/parcours/parcours.component.ts (Version finale optimisée)

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { ParcoursService } from '../../../../services/parcours.service';
import { ParcoursDto, ParcoursItemDto } from '../../../../dto/parcours.dto';

@Component({
  selector: 'app-parcours',
  templateUrl: './parcours.component.html',
  styleUrls: ['./parcours.component.css']
})
export class ParcoursComponent implements OnInit {
  
  // Le type de parcours actuellement affiché
  typeParcours: 'recommandes' | 'choisis' | 'mixtes' = 'recommandes';
  
  // La liste des chapitres qui est réellement affichée dans le template
  chapitresAffiches: ParcoursItemDto[] = [];
  
  // L'objet qui stocke TOUTES les données reçues de l'API une seule fois
  private parcoursData: ParcoursDto | null = null;

  // La liste de toutes les matières uniques, pour les titres de section
  matieres: string[] = [];
  
  isLoading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private parcoursService: ParcoursService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    // 1. On charge les données depuis l'API une seule fois au démarrage du composant.
    this.loadAllParcoursData();

    // 2. On s'abonne aux changements de l'URL pour mettre à jour l'affichage SANS refaire d'appel API.
    this.route.queryParams.subscribe(params => {
      const type = params['type'];
      if (type === 'choisis' || type === 'mixtes') {
        this.typeParcours = type;
      } else {
        this.typeParcours = 'recommandes';
      }
      
      // On met à jour la liste affichée en utilisant les données déjà chargées.
      this.updateChapitresAffiches();
    });
  }

  /**
   * Charge TOUTES les données de parcours depuis l'API et les stocke.
   * Cette méthode n'est appelée qu'une seule fois.
   */
  private loadAllParcoursData(): void {
    this.isLoading = true;
    this.parcoursService.getMesParcours().subscribe({
      next: (data: ParcoursDto) => {
        this.parcoursData = data;
        
        // On extrait la liste unique des matières à partir de la liste 'mixtes' (qui contient tout).
        if (this.parcoursData.mixtes) {
            this.matieres = Array.from(new Set(this.parcoursData.mixtes.map(c => c.matiereNom)));
        }

        // On déclenche une première mise à jour de l'affichage avec les données fraîchement chargées.
        this.updateChapitresAffiches();
        
        this.isLoading = false;
      },
      error: (err: any) => {
        this.toastr.error("Impossible de charger vos parcours.");
        console.error(err);
        this.isLoading = false;
      }
    });
  }

  /**
   * Met à jour la liste 'chapitresAffiches' en fonction du 'typeParcours' actuel.
   * Cette méthode est très rapide car elle ne fait pas d'appel réseau.
   */
  private updateChapitresAffiches(): void {
    if (!this.parcoursData) {
      // Si les données ne sont pas encore chargées, on ne fait rien.
      return;
    }

    switch (this.typeParcours) {
      case 'recommandes':
        this.chapitresAffiches = this.parcoursData.recommandes || [];
        break;
      case 'choisis':
        this.chapitresAffiches = this.parcoursData.choisis || [];
        break;
      case 'mixtes':
        this.chapitresAffiches = this.parcoursData.mixtes || [];
        break;
    }
  }

  // --- Les méthodes utilisées par le template restent les mêmes ---

  getChapitresParMatiere(matiere: string): ParcoursItemDto[] {
    return this.chapitresAffiches.filter(c => c.matiereNom === matiere);
  }

  voirChapitre(id: number): void {
    this.router.navigate(['/app/student/chapitre-detail', id]);
  }

  getCouleurScore(score: number | undefined): string {
    if (score === undefined || score === 0) return 'bg-secondary';
    if (score < 50) return 'bg-danger';
    if (score < 70) return 'bg-warning text-dark';
    return 'bg-success';
  }
}
