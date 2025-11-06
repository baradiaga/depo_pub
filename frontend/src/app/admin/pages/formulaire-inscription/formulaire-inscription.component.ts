// Fichier : src/app/admin/pages/formulaire-inscription/formulaire-inscription.component.ts (Corrigé)

import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { EtudiantService } from '../../../services/etudiant.service';
import { ElementConstitutifService } from '../../../services/element-constitutif.service';
// On importe l'interface depuis le bon fichier central
import { ElementConstitutifResponse } from '../../../services/models';

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

  constructor(
    private fb: FormBuilder,
    private etudiantService: EtudiantService,
    private ecService: ElementConstitutifService,
    private router: Router,
    private toastr: ToastrService
  ) {
    // Le constructeur est correct et reste inchangé
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
      motDePasse: ['', [Validators.required, Validators.minLength(6)]],
      anneeAcademique: ['2024-2025', Validators.required],
      filiere: ['', Validators.required],
      
      
      matiereIds: this.fb.array([], [Validators.required, Validators.minLength(1)])
    });
  }

  ngOnInit(): void {
    this.loadMatieres();
  }

  loadMatieres(): void {
    this.isLoading = true;
    this.ecService.findAll().subscribe({
      next: (data) => {
        this.matieresDisponibles = data;
        this.isLoading = false;
      },
      error: (err: any) => { // On type 'err'
        this.toastr.error('Impossible de charger la liste des matières.', 'Erreur');
        this.isLoading = false;
        console.error(err);
      }
    });
  }

  onMatiereChange(event: Event): void {
    const matieresArray: FormArray = this.inscriptionForm.get('matiereIds') as FormArray;
    const target = event.target as HTMLInputElement;
    const matiereId = Number(target.value);
    if (target.checked) {
      matieresArray.push(this.fb.control(matiereId));
    } else {
      const index = matieresArray.controls.findIndex(x => x.value === matiereId);
      if (index !== -1) {
        matieresArray.removeAt(index);
      }
    }
  }

  enregistrer(): void {
    this.inscriptionForm.markAllAsTouched();
    if (this.inscriptionForm.invalid) {
      this.toastr.warning('Veuillez corriger les erreurs.', 'Formulaire Invalide');
      return;
    }
    this.isSaving = true;
    const formData = this.inscriptionForm.value;
    this.etudiantService.inscrireNouvelEtudiant(formData).subscribe({
      next: () => {
        this.toastr.success('Nouvel étudiant inscrit avec succès !');
        this.isSaving = false;
        this.router.navigate(['/admin/gestiondesinscription']);
      },
      error: (err: any) => { // On type 'err'
        const errorMessage = err.error?.message || 'Une erreur inconnue est survenue.';
        this.toastr.error(errorMessage, 'Échec de l\'Inscription');
        this.isSaving = false;
        console.error(err);
      }
    });
  }

  annuler(): void {
    this.router.navigate(['/admin/gestiondesinscription']);
  }
}
