import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
// On importe bien le service et l'interface
import { ParcoursService, ParcoursItem, ParcoursData } from '../../../../services/parcours.service';

@Component({
  selector: 'app-chapitre-detail',
  templateUrl: './chapitre-detail.component.html',
  styleUrls: ['./chapitre-detail.component.css']
  // CORRECTION 1 : On retire la ligne 'providers'
  // Le service est déjà fourni globalement grâce à "providedIn: 'root'".
})
export class ChapitreDetailComponent implements OnInit {
  
  // La propriété 'chapitre' est de type ParcoursItem ou undefined.
  chapitre?: ParcoursItem;
  isLoading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private parcoursService: ParcoursService
  ) {}

  ngOnInit(): void {
    // On récupère l'ID du chapitre depuis l'URL.
    const chapitreId = Number(this.route.snapshot.paramMap.get('id'));
    const etudiantId = 1; // TODO: Remplacer par l'ID de l'utilisateur authentifié

    if (chapitreId > 0) {
      this.isLoading = true;
      // On appelle le service pour récupérer tous les parcours de l'étudiant.
      this.parcoursService.getParcoursEtudiant(etudiantId).subscribe({
        next: (data: ParcoursData) => {
          // On fusionne toutes les listes de parcours pour trouver le chapitre.
          const allChapitres = [...data.recommandes, ...data.choisis, ...data.mixtes];
          
          // CORRECTION 2 : On utilise 'chapitreId' au lieu de 'id'.
          this.chapitre = allChapitres.find(c => c.chapitreId === chapitreId);
          
          this.isLoading = false;
        },
        error: (err) => {
          console.error("Erreur lors du chargement des données du parcours", err);
          this.isLoading = false;
          alert("Impossible de charger les détails de ce chapitre.");
        }
      });
    } else {
      console.error("ID de chapitre invalide dans l'URL.");
      this.isLoading = false;
    }
    
  }
  // ====================================================================
  // ===> CORRECTION : Ajout de la méthode manquante <====
  // ====================================================================
  /**
   * Retourne une classe CSS en fonction de la valeur du score.
   * @param score Le score du chapitre (en pourcentage).
   * @returns Une chaîne de caractères représentant la classe CSS.
   */
  getCouleurScore(score: number): string {
    if (score < 50) return 'score-faible';
    if (score < 70) return 'score-moyen';
    return 'score-eleve';
  }
}
