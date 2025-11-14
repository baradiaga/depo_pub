// Fichier : src/app/admin/pages/formulaire-inscription/formulaire-inscription.component.ts (Version finale avec matières)

import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators, AbstractControl } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { forkJoin } from 'rxjs';

import { EtudiantService, EtudiantPayload } from '../../../services/etudiant.service';
import { ElementConstitutifService } from '../../../services/element-constitutif.service';
import { ElementConstitutifResponse } from '../../../models/models';

@Component({
  selector: 'app-formulaire-inscription',
  templateUrl: './formulaire-inscription.component.html',
  styleUrls: ['./formulaire-inscription.component.css']
})
export class FormulaireInscriptionComponent implements OnInit {
  
  inscriptionForm: FormGroup;
  matieresDisponibles: ElementConstitutifResponse[] = [];
  isLoading = true;
  isSaving = false;
  isEditMode = false;
  etudiantId: number | null = null;
  pageTitle = "Nouveau Dossier d'Inscription Étudiant";

  constructor(
    private fb: FormBuilder,
    private etudiantService: EtudiantService,
    private ecService: ElementConstitutifService,
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
      motDePasse: [''], // Mot de passe optionnel par défaut
      anneeAcademique: ['2024-2025', Validators.required],
      filiere: ['', Validators.required],
      matiereIds: this.fb.array([], Validators.required) // Le FormArray pour les matières
    });
  }

  // Getter pratique pour accéder facilement au FormArray depuis le template
  get matiereIds(): FormArray {
    return this.inscriptionForm.get('matiereIds') as FormArray;
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    
    if (idParam) {
      // --- MODE ÉDITION ---
      this.isEditMode = true;
      this.etudiantId = +idParam;
      this.pageTitle = "Modifier les Informations de l'Étudiant";
      // Le mot de passe n'est pas obligatoire en mode édition
      this.inscriptionForm.get('motDePasse')?.clearValidators();
      this.loadDataForEditMode();
    } else {
      // --- MODE CRÉATION ---
      this.isEditMode = false;
      // Le mot de passe est obligatoire en mode création
      this.inscriptionForm.get('motDePasse')?.setValidators([Validators.required, Validators.minLength(6)]);
      this.loadDataForCreateMode();
    }
  }

  loadDataForCreateMode(): void {
    this.isLoading = true;
    this.ecService.findAll().subscribe({
      next: (matieres) => {
        this.matieresDisponibles = matieres;
        this.isLoading = false;
      },
      error: (err) => this.handleError(err, 'Impossible de charger la liste des matières.')
    });
  }

  loadDataForEditMode(): void {
    if (!this.etudiantId) return;
    this.isLoading = true;

    // On charge en parallèle les données de l'étudiant ET la liste de toutes les matières
    forkJoin({
      etudiant: this.etudiantService.getEtudiantById(this.etudiantId),
      matieres: this.ecService.findAll()
    }).subscribe({
      next: ({ etudiant, matieres }) => {
        this.matieresDisponibles = matieres;
        this.inscriptionForm.patchValue(etudiant);

        // On pré-coche les matières auxquelles l'étudiant est déjà inscrit
        if (etudiant.matiereIds) {
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
      // Si la case est cochée, on ajoute l'ID au FormArray
      this.matiereIds.push(this.fb.control(matiereId));
    } else {
      // Si la case est décochée, on trouve son index et on le retire
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
        this.router.navigate(['/admin/inscriptions']);
      },
      error: (err) => {
        this.isSaving = false;
        this.toastr.error(err.error?.message || 'Une erreur est survenue.');
      }
    });
  }

  annuler(): void {
    this.router.navigate(['/admin/inscriptions']);
  }

  private handleError(error: any, message: string): void {
    this.isLoading = false;
    this.toastr.error(message, 'Erreur');
    console.error(error);
  }
}
