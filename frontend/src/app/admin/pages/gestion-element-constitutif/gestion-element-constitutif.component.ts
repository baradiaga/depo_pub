// Fichier : src/app/features/admin/pages/gestion-element-constitutif/gestion-element-constitutif.component.ts

import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs';

import { UniteEnseignement, UniteEnseignementService } from '../../../services/unite-enseignement.service';
import { ElementConstitutifService } from '../../../services/element-constitutif.service';
import { ElementConstitutifResponse, ElementConstitutifRequest } from '../../../models/models';
import { UtilisateurService, UserResponseDto } from '../../../services/utilisateur.service';

@Component({
  selector: 'app-gestion-element-constitutif',
  templateUrl: './gestion-element-constitutif.component.html',
  styleUrls: ['./gestion-element-constitutif.component.css']
})
export class GestionElementConstitutifComponent implements OnInit {

  unites$!: Observable<UniteEnseignement[]>;
  enseignants$!: Observable<UserResponseDto[]>;

  elements: ElementConstitutifResponse[] = [];
  selectedUniteId: number | null = null;
  isLoadingElements = false;
  isFormVisible = false;
  isEditing = false;

  elementForm: FormGroup;

  constructor(
    private ueService: UniteEnseignementService,
    private ecService: ElementConstitutifService,
    private utilisateurService: UtilisateurService,
    private fb: FormBuilder,
    private toastr: ToastrService
  ) {
    this.elementForm = this.fb.group({
      id: [null],
      nom: ['', [Validators.required, Validators.minLength(3)]],
      code: ['', Validators.required],
      credit: [0, [Validators.required, Validators.min(0)]],
      description: [''],
      enseignantId: [null, Validators.required],
      volumeHoraireCours: [0, [Validators.required, Validators.min(0)]],
      volumeHoraireTD: [0, [Validators.required, Validators.min(0)]],
      volumeHoraireTP: [0, [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    this.unites$ = this.ueService.getAll();
    this.enseignants$ = this.utilisateurService.getEnseignants();
    this.loadAllElements();
  }

  loadAllElements(): void {
    this.isLoadingElements = true;
    this.ecService.findAll().subscribe({
      next: data => {
        this.elements = data;
        this.isLoadingElements = false;
      },
      error: (err) => {
        this.toastr.error("Erreur lors du chargement des matières.");
        console.error(err);
        this.isLoadingElements = false;
      }
    });
  }

  onSelectUnite(ueId: any): void {
  this.selectedUniteId = Number(ueId);
}


  showNewForm(): void {
    this.isEditing = false;
    this.isFormVisible = true;
    this.elementForm.reset();
  }

  showEditForm(element: ElementConstitutifResponse): void {
    this.isEditing = true;
    this.isFormVisible = true;
    this.elementForm.patchValue({
      id: element.id,
      nom: element.nom,
      code: element.code,
      credit: element.credit,
      description: element.description,
      enseignantId: element.enseignant ? element.enseignant.id : null,
      volumeHoraireCours: element.volumeHoraireCours || 0,
      volumeHoraireTD: element.volumeHoraireTD || 0,
      volumeHoraireTP: element.volumeHoraireTP || 0
    });
  }

  onCancel(): void {
    this.isFormVisible = false;
  }

  onSubmit(): void {
    

    if (this.elementForm.invalid) {
      this.toastr.warning('Veuillez remplir tous les champs obligatoires.');
      return;
    }

    const formData: ElementConstitutifRequest = this.elementForm.value;
    
    if (this.isEditing) {
      const id = this.elementForm.get('id')?.value;
      if (id) {
        this.ecService.update(id, formData).subscribe({
          next: () => {
            this.toastr.success('Matière mise à jour avec succès !');
            this.isFormVisible = false;
            this.loadAllElements();
          },
          error: err => {
            this.toastr.error("Une erreur est survenue lors de la mise à jour.");
            console.error(err);
          }
        });
      } else {
        this.toastr.error("Erreur critique : ID manquant pour la mise à jour.");
      }
    } else {
      if (!this.selectedUniteId) {
        this.toastr.error("Veuillez d'abord sélectionner une Unité d'Enseignement.");
        return;
      }
      this.ecService.create(this.selectedUniteId, formData).subscribe({
        next: () => {
          this.toastr.success('Matière créée avec succès !');
          this.isFormVisible = false;
          this.loadAllElements();
        },
        error: err => {
          this.toastr.error("Une erreur est survenue lors de la création.");
          console.error(err);
        }
      });
    }
  }

  onDelete(ecId: number | undefined): void {
    if (!ecId) return;

    if (confirm('Êtes-vous sûr de vouloir supprimer cette matière ?')) {
      this.ecService.delete(ecId).subscribe({
        next: () => {
          this.toastr.info('Matière supprimée.');
          this.loadAllElements();
        },
        error: (err) => {
          if (err.status === 409) {
            this.toastr.error("Impossible de supprimer cette matière car elle est utilisée ailleurs.");
          } else {
            this.toastr.error("Une erreur est survenue lors de la suppression.");
          }
          console.error(err);
        }
      });
    }
  }

  // Calcul du volume horaire total d'une UE
  getVolumeHoraireTotal(ueId: number | undefined): number {
    if (!ueId) return 0;
    const elements = this.elements.filter(e => e.uniteEnseignementId === ueId);
    return elements.reduce((sum, e) => sum + (e.volumeHoraireCours || 0) + (e.volumeHoraireTD || 0) + (e.volumeHoraireTP || 0), 0);
  }

}
