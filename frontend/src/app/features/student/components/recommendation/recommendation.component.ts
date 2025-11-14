// Fichier : src/app/features/student/components/recommendation/recommendation.component.ts (Corrigé)

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { ChapitreService } from '../../../../services/chapitre.service';
import { Chapitre } from '../../../../models/models'; // <-- CORRECTION
import { ParcoursService } from '../../../../services/parcours.service';

@Component({
  selector: 'app-recommendation',
  templateUrl: './recommendation.component.html',
  styleUrls: ['./recommendation.component.css']
})
export class RecommendationComponent implements OnInit {
  
  scoreObtenu: number = 0;
  totalPointsPossible: number = 0;
  pourcentage: number = 0;
  chapitreIdActuel!: number;
  matiereId!: number;

  isLoading: boolean = true;
  aReussi: boolean = false;
  parcoursValide: boolean = false;

  chapitreActuel: Chapitre | undefined;
  private tousLesChapitresDeLaMatiere: Chapitre[] = [];
  chapitresPourSelection: Chapitre[] = [];
  chapitresChoisis: number[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private chapitreService: ChapitreService,
    private parcoursService: ParcoursService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.scoreObtenu = Number(params['score']) || 0;
      this.totalPointsPossible = Number(params['total']) || 0;
      this.chapitreIdActuel = Number(params['chapitreId']) || 0;
      this.matiereId = Number(params['matiereId']) || 0;

      if (this.totalPointsPossible > 0) {
        this.pourcentage = (this.scoreObtenu / this.totalPointsPossible) * 100;
      }

      if (this.matiereId > 0) {
        this.chargerDonneesInitiales();
      } else {
        this.toastr.error("ID de matière manquant ou invalide ! Impossible de générer une recommandation.");
        this.isLoading = false;
      }
    });
  }

  chargerDonneesInitiales(): void {
    this.isLoading = true;
    this.chapitreService.getChapitresParMatiere(this.matiereId).subscribe({
      next: (chapitres: Chapitre[]) => {
        this.tousLesChapitresDeLaMatiere = chapitres;
        this.chapitreActuel = this.tousLesChapitresDeLaMatiere.find(c => c.id === this.chapitreIdActuel);
        
        this.aReussi = this.pourcentage > 66;

        if (this.aReussi) {
          this.chapitresPourSelection = this.tousLesChapitresDeLaMatiere.filter(c => c.id !== this.chapitreIdActuel);
        } else {
          this.chapitresPourSelection = [...this.tousLesChapitresDeLaMatiere];
          if (this.pourcentage < 33 && this.chapitreActuel) {
            this.chapitresChoisis.push(this.chapitreIdActuel);
          }
        }
        
        this.isLoading = false;
      },
      error: (err: any) => {
        this.toastr.error("Erreur lors du chargement des chapitres pour la recommandation.");
        console.error(err);
        this.isLoading = false;
      }
    });
  }

  toggleChapitre(chapitreId: number, event: Event): void { 
    const input = event.target as HTMLInputElement;
    if (input.checked) {
      if (!this.chapitresChoisis.includes(chapitreId)) {
        this.chapitresChoisis.push(chapitreId);
      }
    } else {
      this.chapitresChoisis = this.chapitresChoisis.filter(id => id !== chapitreId);
    }
  }

  estCoche(chapitreId: number): boolean { 
    return this.chapitresChoisis.includes(chapitreId); 
  }

  validerParcours(): void {
    if (this.chapitresChoisis.length === 0) {
      this.toastr.warning("Veuillez sélectionner au moins un chapitre pour votre parcours.");
      return;
    }

    // ==========================================================
    // === CORRECTION APPLIQUÉE ICI                             ===
    // ==========================================================
    this.parcoursService.enregistrerParcours(this.chapitresChoisis).subscribe({
      next: () => {
        this.toastr.success("Votre parcours personnalisé a été enregistré !");
        this.parcoursValide = true;
        setTimeout(() => {
          this.router.navigate(['/app/student/parcours']);
        }, 2000);
      },
      error: (err: any) => {
        this.toastr.error("Une erreur est survenue lors de l'enregistrement de votre parcours.");
        console.error(err);
      }
    });
  }
}
