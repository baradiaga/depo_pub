import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http'; // <-- Import pour typer les erreurs

// ==========================================================
// === CORRECTION : CHEMINS D'IMPORT PROBABLEMENT CORRECTS ===
// Si votre composant est dans 'src/app/pages/gestion-unites', ce chemin devrait être bon.
// Sinon, ajustez-le.
import { UniteEnseignement, UniteEnseignementService } from '../../../services/unite-enseignement.service';
import { AuthService } from '../../../services/auth.service';
// ==========================================================

import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-gestion-unites',
  templateUrl: './gestion-unites.component.html',
  styleUrls: ['./gestion-unites.component.css']
} )
export class GestionUnitesComponent implements OnInit {
  
  uniteList: UniteEnseignement[] = [];
  ueEnCours: UniteEnseignement = this.initUe();
  afficherFormulaire = false;
  isLoading = true;
  isEditing = false;

  constructor(
    private ueService: UniteEnseignementService,
    public authService: AuthService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadUnites();
  }

  loadUnites(): void {
    this.isLoading = true;
    // ==========================================================
    // === CORRECTION : TYPAGE DES PARAMÈTRES data ET err ===
    this.ueService.getAll().subscribe({
      next: (data: UniteEnseignement[]) => { // <-- Type ajouté ici
        this.uniteList = data;
        this.isLoading = false;
      },
      error: (err: HttpErrorResponse) => { // <-- Type ajouté ici
        this.isLoading = false;
        this.toastr.error('Erreur lors du chargement des unités.', 'Erreur');
        console.error(err);
      }
    });
    // ==========================================================
  }

  initUe(): UniteEnseignement {
    return { nom: '', code: '', description: '', credit: 0, semestre: 1, objectifs: '' };
  }

  nouvelleUe(): void {
    this.isEditing = false;
    this.ueEnCours = this.initUe();
    this.afficherFormulaire = true;
  }

  modifierUnite(ue: UniteEnseignement): void {
    this.isEditing = true;
    this.ueEnCours = { ...ue };
    this.afficherFormulaire = true;
  }

  onSubmit(): void {
    if (this.isEditing && this.ueEnCours.id) {
      this.ueService.update(this.ueEnCours.id, this.ueEnCours).subscribe({
        next: () => {
          this.toastr.success('Unité mise à jour avec succès !', 'Succès');
          this.onReset();
          this.loadUnites();
        },
        error: (err: HttpErrorResponse) => this.toastr.error('Erreur lors de la mise à jour.', 'Erreur')
      });
    } else {
      this.ueService.create(this.ueEnCours).subscribe({
        next: () => {
          this.toastr.success('Unité créée avec succès !', 'Succès');
          this.onReset();
          this.loadUnites();
        },
        error: (err: HttpErrorResponse) => this.toastr.error('Erreur lors de la création.', 'Erreur')
      });
    }
  }

  supprimerUnite(id: number | undefined): void {
    if (id && confirm('Êtes-vous sûr de vouloir supprimer cette unité ?')) {
      this.ueService.delete(id).subscribe({
        next: () => {
          this.toastr.info('Unité supprimée.', 'Information');
          this.loadUnites();
        },
        error: (err: HttpErrorResponse) => this.toastr.error('Erreur lors de la suppression.', 'Erreur')
      });
    }
  }

  onReset(): void {
    this.ueEnCours = this.initUe();
    this.afficherFormulaire = false;
    this.isEditing = false;
  }
}
