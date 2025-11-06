// Fichier : src/app/features/student/components/parcours/parcours.component.ts (Corrigé)

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

// --- IMPORTS STANDARDISÉS ---
import { ParcoursService, Parcours } from '../../../../services/parcours.service';
import { Chapitre } from '../../../../services/models'; // <-- CORRECTION

// Interface simplifiée pour correspondre à votre template
interface ChapitreParcours {
  chapitreId: number;
  chapitreNom: string;
  matiereNom: string;
  dernierScore?: number; // Le score est optionnel
}

@Component({
  selector: 'app-parcours',
  templateUrl: './parcours.component.html',
  styleUrls: ['./parcours.component.css']
})
export class ParcoursComponent implements OnInit {
  
  typeParcours: string = 'RECOMMANDE'; // Gardé pour le titre
  
  chapitresFiltres: ChapitreParcours[] = [];
  matieres: string[] = [];
  
  isLoading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private parcoursService: ParcoursService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.typeParcours = params['type'] || 'RECOMMANDE';
      this.loadParcours();
    });
  }

  loadParcours(): void {
    this.isLoading = true;
    
    // Note: Assurez-vous que la méthode 'getMesParcours' existe dans votre service.
    this.parcoursService.getMesParcours().subscribe({
      next: (parcoursList: Parcours[]) => {
        const chapitresSimples = this.transformerParcoursEnListeSimple(parcoursList);
        
        this.chapitresFiltres = chapitresSimples;
        this.matieres = Array.from(new Set(this.chapitresFiltres.map(c => c.matiereNom)));
        
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
   * Transforme la liste de Parcours complexes en une liste simple de ChapitreParcours.
   */
  private transformerParcoursEnListeSimple(parcoursList: Parcours[]): ChapitreParcours[] {
    const tousLesChapitres: ChapitreParcours[] = [];
    parcoursList.forEach(parcours => {
      // On suppose que 'parcours.chapitres' est un tableau de 'Chapitre'
      parcours.chapitres.forEach((chapitre: Chapitre) => {
        tousLesChapitres.push({
          chapitreId: chapitre.id,
          // ==========================================================
          // === CORRECTIONS APPLIQUÉES ICI                           ===
          // ==========================================================
          chapitreNom: chapitre.nom, // On utilise 'nom'
          matiereNom: chapitre.elementConstitutifNom || 'Non définie', // On utilise 'elementConstitutifNom'
          dernierScore: chapitre.score
        });
      });
    });
    return tousLesChapitres;
  }

  /**
   * Méthode utilisée par le template pour filtrer les chapitres.
   */
  getChapitresParMatiere(matiere: string): ChapitreParcours[] {
    return this.chapitresFiltres.filter(c => c.matiereNom === matiere);
  }

  voirChapitre(id: number): void {
    this.router.navigate(['/app/student/chapitre-detail', id]);
  }

  getCouleurScore(score: number | undefined): string {
    if (score === undefined) return 'bg-secondary';
    if (score < 50) return 'bg-danger';
    if (score < 70) return 'bg-warning text-dark';
    return 'bg-success';
  }
}
