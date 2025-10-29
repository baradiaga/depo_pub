import { Component, OnInit } from '@angular/core';
import { ChapitreService, ChapitrePayload, ChapitreDetail } from '../../../services/chapitre.service';
import { MatiereService } from '../../../services/matiere.service'; // <-- 1. IMPORTER LE NOUVEAU SERVICE
import { HttpErrorResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-parametrage-chapitre',
  templateUrl: './parametrage-chapitre.component.html',
  styleUrls: ['./parametrage-chapitre.component.css']
})
export class ParametrageChapitreComponent implements OnInit {
  chapitres: ChapitreDetail[] = [];
  chapitreEnCours: ChapitreDetail | null = null;
  isEditing: boolean = false;
  
  // Champs du formulaire de création/modification
  matiereSelectionnee: string = '';
  matieres: string[] = [];
  titre: string = '';
  niveau: number = 1;
  objectif: string = '';
  sections: { titre: string, contenu: string }[] = [{ titre: '', contenu: '' }];

  afficherFormulaire: boolean = false;

  // 2. INJECTER MatiereService dans le constructeur
  constructor(
    private chapitreService: ChapitreService,
    private matiereService: MatiereService,
    private toastr: ToastrService
  ) {}

  // 3. UTILISER le service dans ngOnInit pour charger les données
  ngOnInit(): void {
    this.chargerMatieres();
    this.chargerChapitres();
  }

  chargerChapitres(): void {
    this.chapitreService.getAllChapitres().subscribe({
      next: (data) => {
        this.chapitres = data;
      },
      error: (err: HttpErrorResponse) => {
        this.toastr.error('Erreur lors du chargement des chapitres.', 'Erreur');
        console.error(err);
      }
    });
  }

  /**
   * Appelle le service pour récupérer la liste des noms de matières
   * et met à jour la variable locale 'matieres'.
   */
  chargerMatieres(): void {
    this.matiereService.getNomsMatieres().subscribe({
      next: (nomsMatieres) => {
        this.matieres = nomsMatieres;
      },
      error: (err) => {
        this.toastr.error('Impossible de charger la liste des matières.', 'Erreur');
        console.error(err);
      }
    });
  }

  // ... toutes les autres méthodes (majStructure, enregistrerChapitre, etc.) restent inchangées ...

  majStructure() {
    // Si on est en mode création, on peut générer la structure
    if (!this.isEditing) {
      this.sections = Array(this.niveau).fill(null).map(() => ({ titre: '', contenu: '' }));
    }
  }

  ajouterSection() {
    this.sections.push({ titre: '', contenu: '' });
  }

  supprimerSection(index: number) {
    if (this.sections.length > 1) {
      this.sections.splice(index, 1);
    }
  }

  nouveauChapitre(): void {
    this.isEditing = false;
    this.chapitreEnCours = null;
    this.resetForm();
    this.afficherFormulaire = true;
  }

  modifierChapitre(chapitre: ChapitreDetail): void {
    this.isEditing = true;
    this.chapitreEnCours = chapitre;
    this.afficherFormulaire = true;
    
    // Remplir le formulaire avec les données existantes
    this.matiereSelectionnee = chapitre.matiereNom;
    this.titre = chapitre.titre;
    this.niveau = chapitre.niveau;
    this.objectif = chapitre.objectif;
    
    // NOTE: Pour l'update, nous n'avons pas implémenté la gestion des sections dans le backend (trop complexe pour un DTO simple).
    // Nous allons donc ignorer les sections dans le formulaire d'édition pour l'instant.
    this.sections = [{ titre: 'Sections non éditables', contenu: 'Veuillez utiliser le composant "gestion-chapitre" pour l\'édition du contenu des sections.' }];
  }

  onSubmit(): void {
    const chapitrePayload: ChapitrePayload = {
      matiere: this.matiereSelectionnee,
      titre: this.titre,
      niveau: this.niveau,
      objectif: this.objectif,
      sections: this.sections
    };

    const action$ = this.isEditing && this.chapitreEnCours?.id
      ? this.chapitreService.updateChapitre(this.chapitreEnCours.id, chapitrePayload)
      : this.chapitreService.creerChapitre(chapitrePayload);

    action$.subscribe({
      next: () => {
        this.toastr.success(`Chapitre ${this.isEditing ? 'mis à jour' : 'créé'} avec succès !`);
        this.onReset();
        this.chargerChapitres(); // Recharger la liste
      },
      error: (err: HttpErrorResponse) => {
        this.toastr.error('Erreur lors de l\'enregistrement du chapitre.', 'Erreur');
        console.error(err);
      }
    });
  }

  supprimerChapitre(id: number | undefined): void {
    if (id && confirm('Êtes-vous sûr de vouloir supprimer ce chapitre ?')) {
      this.chapitreService.deleteChapitre(id).subscribe({
        next: () => {
          this.toastr.info('Chapitre supprimé.');
          this.chargerChapitres();
        },
        error: (err: HttpErrorResponse) => {
          this.toastr.error('Erreur lors de la suppression.', 'Erreur');
          console.error(err);
        }
      });
    }
  }

  onReset(): void {
    this.afficherFormulaire = false;
    this.isEditing = false;
    this.chapitreEnCours = null;
    this.resetForm();
  }

  resetForm(): void {
    this.titre = '';
    this.objectif = '';
    this.niveau        this.sections = [{ titre: '', contenu: '' }];contenu: '' }];
    this.matiereSelectionnee = '';
  }

  trackByIndex(index: number): number {
    return index;
  }
}
