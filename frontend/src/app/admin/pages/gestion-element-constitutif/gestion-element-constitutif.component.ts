// Fichier : src/app/features/admin/pages/gestion-element-constitutif/gestion-element-constitutif.component.ts

import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs';

import { UniteEnseignement, UniteEnseignementService } from '../../../services/unite-enseignement.service';
import { ElementConstitutifService, ElementConstitutifResponse, ElementConstitutifRequest } from '../../../services/element-constitutif.service';
import { Utilisateur, UtilisateurService } from '../../../services/utilisateur.service';

@Component({
  selector: 'app-gestion-element-constitutif',
  templateUrl: './gestion-element-constitutif.component.html',
  styleUrls: ['./gestion-element-constitutif.component.css']
})
export class GestionElementConstitutifComponent implements OnInit {

  // --- Observables avec assertion de non-nullité ---
  unites$!: Observable<UniteEnseignement[]>;
  enseignants$!: Observable<Utilisateur[]>;

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
      // =======================================================
      // === CORRECTION : On utilise 'credit' pour le formulaire ===
      // =======================================================
      credit: [0, [Validators.required, Validators.min(0)]],
      description: [''],
      enseignantId: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    this.unites$ = this.ueService.getAll();
    this.enseignants$ = this.utilisateurService.getEnseignants();
  }

  onSelectUnite(ueId: string): void {
    const id = Number(ueId);
    this.selectedUniteId = id;
    this.isFormVisible = false;

    if (id) {
      this.loadElementsForUnite(id);
    } else {
      this.elements = [];
    }
  }

  loadElementsForUnite(ueId: number): void {
    this.isLoadingElements = true;
    this.ecService.getElementsForUnite(ueId).subscribe({
      next: data => {
        this.elements = data;
        this.isLoadingElements = false;
      },
      error: () => {
        this.toastr.error("Erreur lors du chargement des éléments.");
        this.isLoadingElements = false;
      }
    });
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
      // =======================================================
      // === CORRECTION : On patch 'credit' et non 'credits' ===
      // =======================================================
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
    if (!this.selectedUniteId) return;

    const formData: ElementConstitutifRequest = this.elementForm.value;
    console.log('Données envoyées au back-end :', formData);

    const action$ = this.isEditing
      ? this.ecService.update(formData.id, formData)
      : this.ecService.create(this.selectedUniteId, formData);

    action$.subscribe({
      next: () => {
        const message = this.isEditing ? 'Élément mis à jour avec succès !' : 'Élément créé avec succès !';
        this.toastr.success(message);
        this.isFormVisible = false;
        this.loadElementsForUnite(this.selectedUniteId!);
      },
      error: err => {
        console.error(err);
        this.toastr.error("Une erreur est survenue. Détails dans la console.");
      }
    });
  }

  onDelete(ecId: number | undefined): void {
    if (!ecId || !this.selectedUniteId) return;

    if (confirm('Êtes-vous sûr de vouloir supprimer cet élément ?')) {
      this.ecService.delete(ecId).subscribe({
        next: () => {
          this.toastr.info('Élément supprimé.');
          this.loadElementsForUnite(this.selectedUniteId!);
        },
        error: () => this.toastr.error("Erreur lors de la suppression.")
      });
    }
  }
}
