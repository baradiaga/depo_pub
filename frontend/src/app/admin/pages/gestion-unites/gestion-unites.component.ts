import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http'; // <-- Import pour typer les erreurs

import { FormationService } from '../../../services/formation.service';
import { ElementConstitutifService } from '../../../services/element-constitutif.service'; // Ajout du service pour les éléments constitutifs
import { UniteEnseignement, UniteEnseignementService } from '../../../services/unite-enseignement.service';
import { ElementConstitutifResponse } from '../../../models/models'; // Ajout de l'interface pour les éléments constitutifs (supposée)
import { AuthService } from '../../../services/auth.service';
// ==========================================================
import { FormationDetail } from '../../../models/models';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-gestion-unites',
  templateUrl: './gestion-unites.component.html',
  styleUrls: ['./gestion-unites.component.css']
} )
export class GestionUnitesComponent implements OnInit {
  
  uniteList: UniteEnseignement[] = [];
  ueEnCours: UniteEnseignement = this.initUe();
  formations: FormationDetail[] = [];
  tousLesElementsConstitutifs: ElementConstitutifResponse[] = []; // Ajout de la liste des éléments constitutifs
  afficherFormulaire = false;
  isLoading = true;
  isEditing = false;

  constructor(
    private ueService: UniteEnseignementService,
    private formationService: FormationService,
    private elementConstitutifService: ElementConstitutifService, // Ajout du service

    public authService: AuthService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadUnites();
    this.loadFormations();
    this.chargerElementsConstitutifs(); // Chargement des éléments constitutifs
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
  // ===============================
// === Chargement des formations
// ===============================
  loadFormations(): void {
    this.formationService.getAllFormations().subscribe({
      next: (data) => {
        this.formations = data;
      },
      error: (err: HttpErrorResponse) => {
        this.toastr.error("Erreur lors du chargement des formations.", "Erreur");
        console.error(err);
      }
    });
  }

  // ===============================
  // === Chargement des éléments constitutifs
  // ===============================
  chargerElementsConstitutifs(): void {
    this.elementConstitutifService.findAll().subscribe({
      next: data => this.tousLesElementsConstitutifs = data,
      error: err => this.toastr.error("Erreur lors du chargement des éléments constitutifs.", "Erreur")
    });
  }

  initUe(): UniteEnseignement {
    // Mise à jour de la structure de l'UE pour inclure les volumes horaires et les éléments constitutifs
    return {
      nom: '',
      code: '',
      description: '',
      ects: 0,
      semestre: 1,
      objectifs: '',
      formationId: null,
      volumeHoraireCours: 0, // Nouveau champ
      volumeHoraireTD: 0, // Nouveau champ
      volumeHoraireTP: 0, // Nouveau champ
      elementConstitutifIds: [] // Nouveau champ
    };
  }

  nouvelleUe(): void {
    this.isEditing = false;
    this.ueEnCours = this.initUe();
    this.afficherFormulaire = true;
  }

  // Méthode pour préparer le payload avant l'envoi
  private getPayload(): UniteEnseignement {
  const payload = { ...this.ueEnCours };

  if (!payload.formationId || payload.formationId === "") {
    this.toastr.error("Veuillez sélectionner une formation.", "Erreur de validation");
    throw new Error("Formation obligatoire");
  }

  payload.formationId = Number(payload.formationId);

  return payload;
}


  modifierUnite(ue: UniteEnseignement): void {
    this.isEditing = true;
    this.ueEnCours = { ...ue };
    this.afficherFormulaire = true;
  }

  onSubmit(form: any): void { 
      console.log("Valeur formationId :", this.ueEnCours.formationId);
  console.log("Type formationId :", typeof this.ueEnCours.formationId);
// Utilisation de 'any' car nous n'avons pas l'import de NgForm
    if (form.invalid) {
      this.toastr.error('Veuillez remplir tous les champs obligatoires.', 'Erreur de validation');
      return;
    }

    console.log('UE envoyée :', this.ueEnCours);
    if (this.isEditing && this.ueEnCours.id) {
      this.ueService.update(this.ueEnCours.id, this.getPayload()).subscribe({
        next: () => {
          this.toastr.success('Unité mise à jour avec succès !', 'Succès');
          this.onReset();
          this.loadUnites();
        },
        error: (err: HttpErrorResponse) => this.toastr.error('Erreur lors de la mise à jour.', 'Erreur')
      });
    } else {
      this.ueService.create(this.getPayload()).subscribe({
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
