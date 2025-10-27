// Fichier : src/app/components/recommendation/recommendation.component.ts

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ChapitreService, ChapitreInfo } from '../../../../services/chapitre.service';
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
  chapitreActuel: ChapitreInfo | undefined;
  private _tousLesChapitresDeLaMatiere: ChapitreInfo[] = [];
  chapitresPourSelection: ChapitreInfo[] = [];
  chapitresChoisis: number[] = [];
  parcoursValide: boolean = false;

  // Note : L'ID de l'étudiant n'est plus nécessaire ici pour l'enregistrement,
  // mais peut être utile pour d'autres appels (comme getParcoursEtudiant).
  etudiantId: number = 1; 

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private chapitreService: ChapitreService,
    private parcoursService: ParcoursService 
  ) {}

  ngOnInit() {
    console.log('[Recommendation] ngOnInit: Le composant est en cours d\'initialisation.');
    this.route.queryParams.subscribe(params => {
      console.log('[Recommendation] ngOnInit: Paramètres reçus de l\'URL :', params);

      this.scoreObtenu = Number(params['score']) || 0;
      this.totalPointsPossible = Number(params['total']) || 0;
      this.chapitreIdActuel = Number(params['chapitreId']) || 0;
      this.matiereId = Number(params['matiereId']) || 0;

      console.log(`[Recommendation] ngOnInit: Valeurs lues -> score: ${this.scoreObtenu}, total: ${this.totalPointsPossible}, chapitreId: ${this.chapitreIdActuel}, matiereId: ${this.matiereId}`);

      this.pourcentage = (this.totalPointsPossible > 0) ? (this.scoreObtenu / this.totalPointsPossible) * 100 : 0;
      console.log(`[Recommendation] ngOnInit: Pourcentage calculé = ${this.pourcentage}%`);
      
      if (this.matiereId > 0) {
        console.log('[Recommendation] ngOnInit: matiereId est valide. Appel de chargerDonneesInitiales().');
        this.chargerDonneesInitiales();
      } else {
        console.error("[Recommendation] ngOnInit: ID de matière manquant ou invalide ! Impossible de charger les chapitres.");
        this.isLoading = false;
      }
    });
  }

  chargerDonneesInitiales(): void {
    this.isLoading = true;
    console.log(`[Recommendation] chargerDonnees: Appel du service pour récupérer les chapitres de la matière ${this.matiereId}`);
    this.chapitreService.getChapitresParMatiere(this.matiereId).subscribe({
      next: (chapitres) => {
        console.log('[Recommendation] chargerDonnees: Chapitres reçus :', chapitres);
        this._tousLesChapitresDeLaMatiere = chapitres;
        this.chapitreActuel = this._tousLesChapitresDeLaMatiere.find(c => c.id === this.chapitreIdActuel);
        if (this.pourcentage > 66) { this.aReussi = true; this.chapitresPourSelection = this._tousLesChapitresDeLaMatiere.filter(c => c.id !== this.chapitreIdActuel); } 
        else { this.aReussi = false; this.chapitresPourSelection = [...this._tousLesChapitresDeLaMatiere]; if (this.pourcentage < 33 && this.chapitreActuel) { this.chapitresChoisis.push(this.chapitreIdActuel); } }
        this.isLoading = false;
        console.log('[Recommendation] chargerDonnees: Fin du chargement. État final :', { aReussi: this.aReussi, chapitresChoisis: this.chapitresChoisis });
      },
      error: (err) => {
        console.error("[Recommendation] ERREUR lors du chargement des chapitres", err);
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

  // ====================================================================
  // ===> MÉTHODE validerParcours() CORRIGÉE <===
  // ====================================================================
  validerParcours(): void {
    if (this.chapitresChoisis.length === 0) {
      alert("Veuillez sélectionner au moins un chapitre pour votre parcours.");
      return;
    }

    console.log(`[Recommendation] Validation du parcours avec les chapitres :`, this.chapitresChoisis);

    // CORRECTION : On appelle la nouvelle version de la méthode du service,
    // sans passer l'ID de l'étudiant.
    this.parcoursService.enregistrerParcours(this.chapitresChoisis).subscribe({
      next: (response) => {
        console.log('[Recommendation] Succès : Le parcours a été enregistré.', response);
        this.parcoursValide = true;
        
        // Redirection vers la page des parcours pour voir le résultat.
        this.router.navigate(['/app/student/parcours']); // Assurez-vous que '/parcours' est la bonne route.
      },
      error: (err) => {
        console.error('[Recommendation] ERREUR lors de l\'enregistrement du parcours :', err);
        alert("Une erreur est survenue lors de l'enregistrement de votre parcours. Veuillez réessayer.");
      }
    });
  }
}
