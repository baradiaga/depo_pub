// Fichier : src/app/admin/pages/formulaire-inscription/formulaire-inscription.component.ts

import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { forkJoin } from 'rxjs';

import { EtudiantService, EtudiantPayload } from '../../../services/etudiant.service';
import { ElementConstitutifService } from '../../../services/element-constitutif.service';
import { FormationService } from '../../../services/formation.service'; // AJOUTÉ
import { ElementConstitutifResponse, FormationDetail } from '../../../models/models'; // AJOUTÉ

@Component({
  selector: 'app-formulaire-inscription',
  templateUrl: './formulaire-inscription.component.html',
  styleUrls: ['./formulaire-inscription.component.css']
})
export class FormulaireInscriptionComponent implements OnInit {
  
  inscriptionForm: FormGroup;
  matieresDisponibles: ElementConstitutifResponse[] = [];
  formationsDisponibles: FormationDetail[] = []; // AJOUTÉ : Pour stocker les formations
  isLoading = true;
  isSaving = false;
  isEditMode = false;
  etudiantId: number | null = null;
  pageTitle = "Nouveau Dossier d'Inscription Étudiant";

  constructor(
    private fb: FormBuilder,
    private etudiantService: EtudiantService,
    private ecService: ElementConstitutifService,
    private formationService: FormationService, // AJOUTÉ
    private router: Router,
    private toastr: ToastrService,
    private route: ActivatedRoute
  ) {
    this.inscriptionForm = this.fb.group({
      nom: ['', Validators.required],
      prenom: ['', Validators.required],
      dateDeNaissance: ['', Validators.required],
      lieuDeNaissance: ['', Validators.required],
      nationalite: ['', Validators.required],
      sexe: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      telephone: ['', Validators.required],
      adresse: ['', Validators.required],
      motDePasse: [''],
      anneeAcademique: ['2025-2026', Validators.required], // Mis à jour pour 2026
      formationId: [null, Validators.required], // MODIFIÉ : filiere devient formationId
      matiereIds: this.fb.array([], Validators.required)
    });
  }

  get matiereIds(): FormArray {
    return this.inscriptionForm.get('matiereIds') as FormArray;
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    
    if (idParam) {
      this.isEditMode = true;
      this.etudiantId = +idParam;
      this.pageTitle = "Modifier les Informations de l'Étudiant";
      this.inscriptionForm.get('motDePasse')?.clearValidators();
      this.loadDataForEditMode();
    } else {
      this.isEditMode = false;
      this.inscriptionForm.get('motDePasse')?.setValidators([Validators.required, Validators.minLength(6)]);
      this.loadDataForCreateMode();
    }
  }

  loadDataForCreateMode(): void {
    this.isLoading = true;
    // On charge en parallèle les matières ET les formations
    forkJoin({
      matieres: this.ecService.findAll(),
      formations: this.formationService.getAllFormations() // AJOUTÉ
    }).subscribe({
      next: (res) => {
        this.matieresDisponibles = res.matieres;
        this.formationsDisponibles = res.formations; // Stockage des formations
        this.isLoading = false;
      },
      error: (err) => this.handleError(err, 'Erreur lors du chargement des référentiels.')
    });
  }

  loadDataForEditMode(): void {
    if (!this.etudiantId) return;
    this.isLoading = true;

    forkJoin({
      etudiant: this.etudiantService.getEtudiantById(this.etudiantId),
      matieres: this.ecService.findAll(),
      formations: this.formationService.getAllFormations() // AJOUTÉ
    }).subscribe({
      next: ({ etudiant, matieres, formations }) => {
        this.matieresDisponibles = matieres;
        this.formationsDisponibles = formations; // Stockage des formations
        this.inscriptionForm.patchValue(etudiant);

        if (etudiant.matiereIds) {
          this.matiereIds.clear(); // Sécurité
          etudiant.matiereIds.forEach(id => {
            this.matiereIds.push(this.fb.control(id));
          });
        }
        this.isLoading = false;
      },
      error: (err) => this.handleError(err, "Impossible de charger les données de l'étudiant.")
    });
  }

  onMatiereChange(event: Event): void {
    const target = event.target as HTMLInputElement;
    const matiereId = Number(target.value);
    if (target.checked) {
      this.matiereIds.push(this.fb.control(matiereId));
    } else {
      const index = this.matiereIds.controls.findIndex(x => x.value === matiereId);
      if (index !== -1) {
        this.matiereIds.removeAt(index);
      }
    }
  }

  enregistrer(): void {
    this.inscriptionForm.markAllAsTouched();
    if (this.inscriptionForm.invalid) {
      this.toastr.warning('Veuillez corriger les erreurs dans le formulaire.', 'Formulaire Invalide');
      return;
    }

    this.isSaving = true;
    const formData: EtudiantPayload = this.inscriptionForm.value;

    const action = this.isEditMode && this.etudiantId
      ? this.etudiantService.updateEtudiant(this.etudiantId, formData)
      : this.etudiantService.inscrireNouvelEtudiant(formData);

    action.subscribe({
      next: () => {
        this.toastr.success(`Étudiant ${this.isEditMode ? 'mis à jour' : 'inscrit'} avec succès !`);
        this.router.navigate(['app/admin/validation-inscriptions']);
      },
      error: (err) => {
        this.isSaving = false;
        this.toastr.error(err.error?.message || 'Une erreur est survenue.');
      }
    });
  }

  annuler(): void {
    this.router.navigate(['app/admin/inscriptions']);
  }

  private handleError(error: any, message: string): void {
    this.isLoading = false;
    this.toastr.error(message, 'Erreur');
    console.error(error);
  }
}
