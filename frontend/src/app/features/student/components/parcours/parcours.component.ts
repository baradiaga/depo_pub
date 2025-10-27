import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ParcoursService, ParcoursItem, ParcoursData } from '../../../../services/parcours.service';

@Component({
  selector: 'app-parcours',
  templateUrl: './parcours.component.html',
  styleUrls: ['./parcours.component.css']
})
export class ParcoursComponent implements OnInit {
  typeParcours: string = 'RECOMMANDE';
  chapitresFiltres: ParcoursItem[] = [];
  matieres: string[] = [];
  isLoading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private parcoursService: ParcoursService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.typeParcours = params['type'] || 'RECOMMANDE';
      this.loadParcours();
    });
  }

  loadParcours() {
    this.isLoading = true;

    // ====================================================================
    // ===> CORRECTION FINALE : RÉCUPÉRER L'ID DYNAMIQUEMENT <===
    // ====================================================================
    // 1. On lit l'ID de l'utilisateur depuis le localStorage.
    const etudiantIdString = localStorage.getItem('user_id');

    // 2. On vérifie si l'ID existe et n'est pas vide.
    if (!etudiantIdString) {
      console.error("Erreur critique : Impossible de trouver 'user_id' dans le localStorage. L'utilisateur est-il bien connecté ?");
      this.isLoading = false;
      alert("Une erreur est survenue. Veuillez vous reconnecter.");
      // Optionnel : rediriger vers la page de connexion
      // this.router.navigate(['/login']);
      return; // On arrête l'exécution de la méthode
    }

    // 3. On convertit l'ID (qui est une chaîne de caractères) en nombre.
    const etudiantId = Number(etudiantIdString);

    // 4. On appelle le service avec l'ID dynamique de l'utilisateur connecté.
    this.parcoursService.getParcoursEtudiant(etudiantId).subscribe({
      next: (data: ParcoursData) => {
        if (this.typeParcours === 'RECOMMANDE') {
          this.chapitresFiltres = data.recommandes;
        } else if (this.typeParcours === 'CHOISI') {
          this.chapitresFiltres = data.choisis;
        } else if (this.typeParcours === 'MIXTE') {
          this.chapitresFiltres = data.mixtes;
        }

        this.matieres = Array.from(new Set(this.chapitresFiltres.map(c => c.matiereNom)));
        this.isLoading = false;
      },
      error: (err) => {
        console.error("Erreur lors du chargement des parcours", err);
        this.isLoading = false;
        alert("Impossible de charger les parcours.");
      }
    });
  }

  getChapitresParMatiere(matiere: string): ParcoursItem[] {
    return this.chapitresFiltres.filter(c => c.matiereNom === matiere);
  }

  voirChapitre(id: number) {
    this.router.navigate(['/etudiant/chapitre', id]);
  }

  getCouleurScore(score: number): string {
    if (score < 50) return 'score-faible';
    if (score < 70) return 'score-moyen';
    return 'score-eleve';
  }
}
