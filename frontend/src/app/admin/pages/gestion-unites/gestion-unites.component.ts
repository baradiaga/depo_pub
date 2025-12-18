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

  uniteList: UniteEnseignement[] = [];
  ueEnCours: UniteEnseignement = this.initUe();
  formations: FormationDetail[] = [];
  tousLesElementsConstitutifs: ElementConstitutifResponse[] = [];

  afficherFormulaire = false;
  isEditing = false;
  isLoading = true;

  niveauFormationSelectionnee: string = '';
  niveauxDisponibles: string[] = ['LICENCE', 'BACHELOR', 'MASTER', 'MS', 'CERTIFICAT', 'DOCTORAT'];
  anneesDisponibles: { value: number, label: string }[] = [];

  constructor(
    private ueService: UniteEnseignementService,
    private formationService: FormationService,
    private elementConstitutifService: ElementConstitutifService,
    public authService: AuthService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadFormations();
    this.chargerElementsConstitutifs();
  }

  // ============================
  //  CHARGEMENT DES DONNEES
  // ============================

  loadFormations(): void {
    this.formationService.getAllFormations().subscribe({
      next: (data: FormationDetail[]) => {
        this.formations = data;
        this.loadUnites();
      },
      error: (err: HttpErrorResponse) => {
        this.toastr.error("Erreur lors du chargement des formations.", "Erreur");
      }
    });
  }

  loadUnites(): void {
    this.isLoading = true;
    this.ueService.getAll().subscribe({
      next: (data: UniteEnseignement[]) => {
        this.uniteList = data.map(ue => ({
          ...ue,
          anneeCycle: ue.anneeCycle ?? 1
        }));
        this.isLoading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading = false;
        this.toastr.error('Erreur lors du chargement des unités.', 'Erreur');
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
      anneeCycle: 1,
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
  }

  onFormationChange(): void {
    const formation = this.formations.find(f => f.id === this.ueEnCours.formationId);
    
    if (formation) {
      this.niveauFormationSelectionnee = formation.niveauEtude;
      this.updateAnneesDisponibles(this.niveauFormationSelectionnee);
      
      if (this.anneesDisponibles.length > 0) {
        this.ueEnCours.anneeCycle = this.anneesDisponibles[0].value;
      }
    } else {
      this.niveauFormationSelectionnee = '';
      this.anneesDisponibles = [];
    }
  }

  updateAnneesDisponibles(niveau: string): void {
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
  }

  // ============================
  //  SUBMIT - SOLUTION LOCALE
  // ============================

  getPayload(): UniteEnseignement {
    if (!this.ueEnCours.formationId) {
      this.toastr.error("Veuillez sélectionner une formation.", "Erreur de validation");
      throw new Error("Formation obligatoire");
    }
    
    return { 
      ...this.ueEnCours,
      anneeCycle: this.ueEnCours.anneeCycle ?? 1
    };
  }

  onSubmit(form: any): void {
    if (form.invalid) {
      this.toastr.error('Veuillez remplir tous les champs obligatoires.', 'Erreur de validation');
      return;
    }

    const payload = this.getPayload();

    if (this.isEditing && payload.id) {
      // Mise à jour
      this.ueService.update(payload.id, payload).subscribe({
        next: (response) => {
          // Utiliser les données que NOUS AVONS ENVOYÉES car le backend ne renvoie pas anneeCycle
          const updatedUe = {
            ...response,
            anneeCycle: payload.anneeCycle // Utilisez la valeur du payload
          };
          
          // Mettre à jour localement
          const index = this.uniteList.findIndex(u => u.id === updatedUe.id);
          if (index !== -1) {
            this.uniteList[index] = updatedUe;
            this.uniteList = [...this.uniteList]; // Nouvelle référence
          }
          
          this.toastr.success('Unité mise à jour avec succès !', 'Succès');
          this.onReset();
        },
        error: (err: HttpErrorResponse) => {
          this.toastr.error('Erreur lors de la mise à jour.', 'Erreur');
        }
      });
    } else {
      // Création
      this.ueService.create(payload).subscribe({
        next: (response) => {
          // Utiliser les données que NOUS AVONS ENVOYÉES
          const newUe = {
            ...response,
            anneeCycle: payload.anneeCycle // Utilisez la valeur du payload
          };
          
          // Ajouter localement
          this.uniteList = [...this.uniteList, newUe];
          
          this.toastr.success('Unité créée avec succès !', 'Succès');
          this.onReset();
        },
        error: (err: HttpErrorResponse) => {
          this.toastr.error('Erreur lors de la création.', 'Erreur');
        }
      });
    }
  }

  supprimerUnite(id: number | undefined): void {
    if (id && confirm('Êtes-vous sûr de vouloir supprimer cette unité ?')) {
      this.ueService.delete(id).subscribe({
        next: () => {
          this.uniteList = this.uniteList.filter(u => u.id !== id);
          this.toastr.info('Unité supprimée.', 'Information');
        },
        error: (err: HttpErrorResponse) => {
          this.toastr.error('Erreur lors de la suppression.', 'Erreur');
        }
      });
    }
  }

  // ============================
  //  UTILS
  // ============================

  getNiveauEtude(ue: UniteEnseignement): string {
    const formation = this.formations.find(f => f.id === ue.formationId);
    if (!formation) return 'N/A';
    
    const niveau = formation.niveauEtude;
    const annee = ue.anneeCycle ?? 1;
    
    // Si c'est un certificat, pas d'année
    if (niveau === 'CERTIFICAT') {
      return niveau;
    }
    
    // Pour DOCTORAT, format spécial
    if (niveau === 'DOCTORAT') {
      return `DOCTORAT Année ${annee}`;
    }
    
    // Pour les autres: LICENCE, MASTER, etc.
    return `${niveau} ${annee}`;
  }

  getNomFormation(formationId: number | null): string {
    if (!formationId) return 'Non assignée';
    const formation = this.formations.find(f => f.id === formationId);
    return formation ? formation.nom : 'Inconnue';
  }
}