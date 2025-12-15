import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';

import { FormationService } from '../../../services/formation.service';
import { ElementConstitutifService } from '../../../services/element-constitutif.service';
import { UniteEnseignementService, UniteEnseignement } from '../../../services/unite-enseignement.service';
import { FormationDetail, ElementConstitutifResponse } from '../../../models/models';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-gestion-unites',
  templateUrl: './gestion-unites.component.html',
  styleUrls: ['./gestion-unites.component.css']
})
export class GestionUnitesComponent implements OnInit {

  // Listes et objets
  uniteList: UniteEnseignement[] = [];
  ueEnCours: UniteEnseignement = this.initUe();
  formations: FormationDetail[] = [];
  tousLesElementsConstitutifs: ElementConstitutifResponse[] = [];

  // Etats du formulaire
  afficherFormulaire = false;
  isEditing = false;
  isLoading = true;

  // Sélections
  niveauFormationSelectionnee: string = '';
  niveauxDisponibles: string[] = ['LICENCE', 'BACHELOR', 'MASTER', 'MS', 'CERTIFICAT', 'DOCTORAT'];
  anneesDisponibles: { value: number, label: string }[] = [];
  anneeSelectionnee: number = 1;

  constructor(
    private ueService: UniteEnseignementService,
    private formationService: FormationService,
    private elementConstitutifService: ElementConstitutifService,
    public authService: AuthService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadFormations(); // Charger les formations d’abord
    this.chargerElementsConstitutifs();
  }

  // ============================
  //  CHARGEMENT DES DONNEES
  // ============================

  loadFormations(): void {
    this.formationService.getAllFormations().subscribe({
      next: (data: FormationDetail[]) => {
        this.formations = data;
        this.loadUnites(); // Charger les UE après avoir les formations
      },
      error: (err: HttpErrorResponse) => {
        this.toastr.error("Erreur lors du chargement des formations.", "Erreur");
        console.error(err);
      }
    });
  }

  loadUnites(): void {
    this.isLoading = true;
    this.ueService.getAll().subscribe({
      next: (data: UniteEnseignement[]) => {
        this.uniteList = data.map(ue => {
          const formation = this.formations.find(f => f.id === ue.formationId);
          return {
            ...ue,
            niveauEtude: formation ? formation.niveauEtude : 'N/A' // ajouter le niveau pour le tableau
          };
        });
        this.isLoading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading = false;
        this.toastr.error('Erreur lors du chargement des unités.', 'Erreur');
        console.error(err);
      }
    });
  }

  chargerElementsConstitutifs(): void {
    this.elementConstitutifService.findAll().subscribe({
      next: (data) => this.tousLesElementsConstitutifs = data,
      error: (err) => this.toastr.error("Erreur lors du chargement des éléments constitutifs.", "Erreur")
    });
  }

  // ============================
  //  FORMULAIRE
  // ============================

  initUe(): UniteEnseignement {
    return {
      id: undefined,
      nom: '',
      code: '',
      description: '',
      objectifs: '',
      ects: 0,
      semestre: 1,
      formationId: null,
      elementConstitutifIds: [],
      volumeHoraireCours: 0,
      volumeHoraireTD: 0,
      volumeHoraireTP: 0
    };
  }

  nouvelleUe(): void {
    this.isEditing = false;
    this.ueEnCours = this.initUe();
    this.afficherFormulaire = true;
    this.niveauFormationSelectionnee = '';
    this.anneesDisponibles = [];
    this.anneeSelectionnee = 1;
  }

  modifierUnite(ue: UniteEnseignement): void {
    this.isEditing = true;
    this.ueEnCours = { ...ue };
    this.afficherFormulaire = true;

    const formation = this.formations.find(f => f.id === ue.formationId);
    if (formation) {
      this.niveauFormationSelectionnee = formation.niveauEtude;
      this.updateAnneesDisponibles(this.niveauFormationSelectionnee);
    }
  }

  onReset(): void {
    this.afficherFormulaire = false;
    this.isEditing = false;
    this.ueEnCours = this.initUe();
    this.niveauFormationSelectionnee = '';
    this.anneesDisponibles = [];
    this.anneeSelectionnee = 1;
  }

  onFormationChange(): void {
    const formation = this.formations.find(f => f.id === Number(this.ueEnCours.formationId));
    if (formation) {
      this.niveauFormationSelectionnee = formation.niveauEtude;
      this.updateAnneesDisponibles(this.niveauFormationSelectionnee);
    } else {
      this.niveauFormationSelectionnee = '';
      this.anneesDisponibles = [];
      this.anneeSelectionnee = 1;
    }
  }

  updateAnneesDisponibles(niveau: string | undefined): void {
    switch (niveau) {
      case 'LICENCE':
      case 'BACHELOR':
        this.anneesDisponibles = [
          { value: 1, label: 'LICENCE 1' },
          { value: 2, label: 'LICENCE 2' },
          { value: 3, label: 'LICENCE 3' }
        ];
        break;
      case 'MASTER':
      case 'MS':
        this.anneesDisponibles = [
          { value: 1, label: 'MASTER 1' },
          { value: 2, label: 'MASTER 2' }
        ];
        break;
      case 'CERTIFICAT':
        this.anneesDisponibles = [{ value: 1, label: 'Certificat' }];
        break;
      case 'DOCTORAT':
        this.anneesDisponibles = [
          { value: 1, label: 'Année 1' },
          { value: 2, label: 'Année 2' },
          { value: 3, label: 'Année 3' }
        ];
        break;
      default:
        this.anneesDisponibles = [{ value: 1, label: 'Niveau 1' }];
    }
    this.anneeSelectionnee = this.anneesDisponibles[0]?.value ?? 1;
  }

  // ============================
  //  SUBMIT
  // ============================

  getPayload(): UniteEnseignement {
    if (!this.ueEnCours.formationId) {
      this.toastr.error("Veuillez sélectionner une formation.", "Erreur de validation");
      throw new Error("Formation obligatoire");
    }
    return { ...this.ueEnCours, formationId: Number(this.ueEnCours.formationId) };
  }

  onSubmit(form: any): void {
    if (form.invalid) {
      this.toastr.error('Veuillez remplir tous les champs obligatoires.', 'Erreur de validation');
      return;
    }

    const payload = this.getPayload();

    if (this.isEditing && payload.id) {
      this.ueService.update(payload.id, payload).subscribe({
        next: () => {
          this.toastr.success('Unité mise à jour avec succès !', 'Succès');
          this.onReset();
          this.loadUnites();
        },
        error: (err: HttpErrorResponse) => this.toastr.error('Erreur lors de la mise à jour.', 'Erreur')
      });
    } else {
      this.ueService.create(payload).subscribe({
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

  // ============================
  //  UTILS
  // ============================

  getNiveauEtude(ue: UniteEnseignement): string {
    const formation = this.formations.find(f => f.id === ue.formationId);
    return formation?.niveauEtude ?? 'N/A';
  }
}
