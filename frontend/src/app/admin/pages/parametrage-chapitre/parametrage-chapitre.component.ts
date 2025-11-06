import { Component, OnInit } from '@angular/core';
import { ChapitreService } from '../../../services/chapitre.service'; // On importe seulement le service
import { ChapitrePayload } from '../../../services/models'; // On importe le payload depuis models.ts
import { ElementConstitutifService } from '../../../services/element-constitutif.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-parametrage-chapitre',
  templateUrl: './parametrage-chapitre.component.html',
  styleUrls: ['./parametrage-chapitre.component.css']
})
export class ParametrageChapitreComponent implements OnInit {
  niveau: number = 1;
  matiereSelectionnee: string = '';
  matieres: string[] = [];
  titre: string = '';
  objectif: string = '';
  sections: { titre: string }[] = [{ titre: '' }];
  afficherTableau: boolean = false;
  isLoading: boolean = false;

  constructor(
    private chapitreService: ChapitreService,
    private ecService: ElementConstitutifService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.chargerMatieres();
  }

  chargerMatieres(): void {
    this.ecService.findAllNoms().subscribe({
      next: (nomsMatieres) => {
        this.matieres = nomsMatieres;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des matières :', err);
        this.toastr.error('Impossible de charger la liste des matières depuis le serveur.');
      }
    });
  }

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
    if (!this.matiereSelectionnee || !this.titre.trim()) {
      this.toastr.warning('Veuillez sélectionner une matière et saisir un titre pour le chapitre.');
      return;
    }
    this.isLoading = true;

    const chapitrePayload: ChapitrePayload = {
      matiere: this.matiereSelectionnee,
      titre: this.titre,
      niveau: this.niveau,
      objectif: this.objectif,
      sections: this.sections.filter(s => s.titre && s.titre.trim() !== '')
    };

    this.chapitreService.creerChapitre(chapitrePayload).subscribe({
      next: (response) => {
        this.toastr.success('Chapitre enregistré avec succès !');
        this.isLoading = false;
        this.reinitialiserFormulaire();
      },
      error: (err) => {
        this.isLoading = false;
        this.toastr.error(err.error?.message || 'Une erreur est survenue lors de l\'enregistrement.');
        console.error('Erreur lors de l\'enregistrement du chapitre:', err);
      }
    });
  }

  reinitialiserFormulaire(): void {
    this.titre = '';
    this.objectif = '';
    this.sections = [{ titre: '' }];
    this.matiereSelectionnee = '';
    this.niveau = 1;
  }

  toggleTableau() {
    this.afficherTableau = !this.afficherTableau;
  }

  trackByIndex(index: number): number {
    return index;
  }
}
