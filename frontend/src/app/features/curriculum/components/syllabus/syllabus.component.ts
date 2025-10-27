import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatiereService, MatiereAvecDetails, ChapitrePourListe } from '../../../../services/matiere.service'; // Adaptez le chemin si nécessaire

@Component({
  selector: 'app-syllabus',
  templateUrl: './syllabus.component.html',
  styleUrls: ['./syllabus.component.css']
})
export class SyllabusComponent implements OnInit {

  // Variables que le template HTML utilise pour s'afficher
  matiere: MatiereAvecDetails | null = null;
  chapitres: ChapitrePourListe[] = [];
  slug: string = ''; // Pour le titre "Syllabus : ..."
  isLoading: boolean = true; // La page commence en mode chargement

  constructor(
    private route: ActivatedRoute, // Pour lire les paramètres de l'URL
    private router: Router,        // Pour la navigation (ex: lancer un test)
    private matiereService: MatiereService // Le service qui communique avec l'API
  ) { }

  ngOnInit(): void {
    // Cette méthode est appelée automatiquement par Angular quand le composant est créé.
    
    // --- CORRECTION FINALE ---
    // On lit le paramètre 'slug' depuis l'URL, pour correspondre à la configuration de la route
    // dans app-routing.module.ts qui est : { path: 'matieres/:slug', ... }
    const slugParam = this.route.snapshot.paramMap.get('slug');
    
    // On vérifie si le paramètre 'slug' a bien été trouvé dans l'URL
    if (slugParam) {
      // On convertit le slug (qui est l'ID de la matière dans notre cas) en nombre
      const matiereId = +slugParam; 

      console.log(`[SyllabusComponent] Paramètre 'slug' trouvé : ${slugParam}. Lancement de l'appel API pour la matière ID : ${matiereId}`);
      
      // On lance l'appel au service pour récupérer les données de la matière
      this.matiereService.getMatiereById(matiereId).subscribe({
        
        // Si l'appel réussit et que le back-end renvoie des données
        next: (data: MatiereAvecDetails) => {
          console.log('[SyllabusComponent] Données reçues avec succès :', data);
          
          // On met à jour les variables du composant avec les données reçues
          this.matiere = data;
          this.chapitres = data.chapitres;
          this.slug = data.nom; // On utilise le nom de la matière pour le titre
          this.isLoading = false; // On arrête le chargement, la page va s'afficher
        },
        
        // Si l'appel échoue (erreur 401, 404, 500, etc.)
        error: (err: any) => {
          console.error('[SyllabusComponent] Erreur lors du chargement du syllabus :', err);
          this.isLoading = false; // On arrête aussi le chargement pour afficher un message d'erreur
          alert("Une erreur est survenue lors du chargement des données du syllabus.");
        }
      });
    } else {
      // Si aucun paramètre 'slug' n'est trouvé dans l'URL, on affiche une erreur claire.
      console.error("[SyllabusComponent] Aucun paramètre 'slug' trouvé dans l'URL. Vérifiez la configuration de la route et le [routerLink].");
      this.isLoading = false;
      alert("Impossible de déterminer quelle matière afficher. L'URL semble incorrecte.");
    }
  }

  /**
   * Méthode appelée lorsque l'utilisateur clique sur le bouton "Faire le test".
   * @param chapitre Le chapitre pour lequel lancer le test.
   */
  lancerTest(chapitre: ChapitrePourListe): void {
    console.log(`[SyllabusComponent] Lancement du test pour le chapitre ID : ${chapitre.id}`);
    // Redirige l'utilisateur vers la page pour passer le test.
    // Assurez-vous que cette route existe dans votre app-routing.module.ts
    this.router.navigate(['/app/student/test/passer', chapitre.id]);
  }

  /**
   * Méthode pour le bouton "..." (à implémenter).
   * @param chapitre Le chapitre à modifier.
   */
  modifierChapitre(chapitre: ChapitrePourListe): void {
    console.log("[SyllabusComponent] Action 'modifier' pour le chapitre :", chapitre.id);
    // Logique de modification à venir...
  }
}