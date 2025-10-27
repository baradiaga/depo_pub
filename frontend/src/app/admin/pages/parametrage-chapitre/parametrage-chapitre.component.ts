import { Component, OnInit } from '@angular/core';
import { ChapitreService, ChapitrePayload } from '../../../services/chapitre.service';
import { MatiereService } from '../../../services/matiere.service'; // <-- 1. IMPORTER LE NOUVEAU SERVICE

@Component({
  selector: 'app-parametrage-chapitre',
  templateUrl: './parametrage-chapitre.component.html',
  styleUrls: ['./parametrage-chapitre.component.css']
})
export class ParametrageChapitreComponent implements OnInit {
  niveau: number = 1;
  matiereSelectionnee: string = '';
  matieres: string[] = []; // <-- Initialisé comme un tableau vide
  titre: string = '';
  objectif: string = '';
  sections: { titre: string }[] = [{ titre: '' }];
  afficherTableau: boolean = false;

  // 2. INJECTER MatiereService dans le constructeur
  constructor(
    private chapitreService: ChapitreService,
    private matiereService: MatiereService
  ) {}

  // 3. UTILISER le service dans ngOnInit pour charger les données
  ngOnInit(): void {
    this.chargerMatieres();
  }

  /**
   * Appelle le service pour récupérer la liste des noms de matières
   * et met à jour la variable locale 'matieres'.
   */
  chargerMatieres(): void {
    this.matiereService.getNomsMatieres().subscribe({
      next: (nomsMatieres) => {
        this.matieres = nomsMatieres;
        console.log('Matières chargées depuis le backend :', this.matieres);
      },
      error: (err) => {
        console.error('Erreur lors du chargement des matières :', err);
        // En cas d'erreur, on peut mettre une liste par défaut pour ne pas bloquer l'utilisateur
        this.matieres = ['Mathématiques (défaut)', 'Physique (défaut)'];
        alert('Impossible de charger la liste des matières depuis le serveur.');
      }
    });
  }

  // ... toutes les autres méthodes (majStructure, enregistrerChapitre, etc.) restent inchangées ...

  majStructure() {
    this.sections = Array(this.niveau).fill(null).map(() => ({ titre: '' }));
  }

  ajouterSection() {
    this.sections.push({ titre: '' });
  }

  supprimerSection(index: number) {
    if (this.sections.length > 1) {
      this.sections.splice(index, 1);
    }
  }

  enregistrerChapitre() {
    const chapitrePayload: ChapitrePayload = {
      matiere: this.matiereSelectionnee,
      titre: this.titre,
      niveau: this.niveau,
      objectif: this.objectif,
      sections: this.sections
    };

    this.chapitreService.creerChapitre(chapitrePayload).subscribe({
      next: (response) => {
        console.log('Chapitre enregistré avec succès via l\'API:', response);
        alert('Chapitre enregistré avec succès !');
        this.titre = '';
        this.objectif = '';
        this.sections = [{ titre: '' }];
        this.matiereSelectionnee = '';
        this.niveau = 1;
      },
      error: (err) => {
        console.error('Erreur lors de l\'enregistrement du chapitre:', err);
        alert('Une erreur est survenue. Vérifiez la console pour plus de détails.');
      }
    });
  }

  toggleTableau() {
    this.afficherTableau = !this.afficherTableau;
  }

  trackByIndex(index: number): number {
    return index;
  }
}
