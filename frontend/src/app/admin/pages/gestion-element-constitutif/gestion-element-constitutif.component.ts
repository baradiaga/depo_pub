// Fichier : src/app/features/admin/pages/gestion-element-constitutif/gestion-element-constitutif.component.ts

import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs';

import { UniteEnseignement, UniteEnseignementService } from '../../../services/unite-enseignement.service';
// On importe SEULEMENT le service depuis le fichier du service
import { ElementConstitutifService } from '../../../services/element-constitutif.service';

// On importe les INTERFACES depuis le fichier central 'models.ts'
import { ElementConstitutifResponse, ElementConstitutifRequest } from '../../../services/models';
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
      enseignantId: [null, Validators.required]
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

  onSelectUnite(ueId: string): void {
    this.selectedUniteId = Number(ueId);
  }

  showNewForm(): void {
    this.isEditing = false;
    this.isFormVisible = true;
    this.elementForm.reset();
    // On peut pré-sélectionner l'UE si une est déjà choisie
    // this.elementForm.patchValue({ uniteEnseignementId: this.selectedUniteId });
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
      enseignantId: element.enseignant ? element.enseignant.id : null
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
        this.toastr.error("Veuillez d'abord sélectionner une Unité d'Enseignement pour y associer cette nouvelle matière.");
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
          if (err.status === 409) { // Gère le cas où la matière est utilisée ailleurs
            this.toastr.error("Impossible de supprimer cette matière car elle est déjà utilisée (par des chapitres, etc.).");
          } else {
            this.toastr.error("Une erreur est survenue lors de la suppression.");
          }
          console.error(err);
        }
      });
    }
  }
}
