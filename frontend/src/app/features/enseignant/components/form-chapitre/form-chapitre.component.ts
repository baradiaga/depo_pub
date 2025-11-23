// Fichier : src/app/features/enseignant/components/form-chapitre/form-chapitre.component.ts (Version Finale Corrigée)

import { Component, Input } from '@angular/core'; // On retire EventEmitter et Output, on ajoute Input
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastrService } from 'ngx-toastr';
import { GestionContenuService } from '../../../../services/gestion-contenu.service';
import { ChapitreContenu } from '../../../../models/gestion-contenu.models';

@Component({
  selector: 'app-form-chapitre',
  templateUrl: './form-chapitre.component.html',
  styleUrls: ['./form-chapitre.component.scss']
})
export class FormChapitreComponent {
  
  @Input() matiereId!: number; // <-- On reçoit l'ID de la matière du parent
  chapitreForm: FormGroup;
  isSubmitting = false;

  constructor(
    public activeModal: NgbActiveModal,
    private fb: FormBuilder,
    // ====================================================================
    // === CORRECTION : INJECTION DES SERVICES NÉCESSAIRES              ===
    // ====================================================================
    private gestionContenuService: GestionContenuService,
    private toastr: ToastrService
  ) {
    this.chapitreForm = this.fb.group({
      nom: ['', [Validators.required, Validators.maxLength(255)]],
      objectif: ['', [Validators.maxLength(1000)]]
    });
  }

  // Méthode appelée lors de la soumission du formulaire
  onSubmit(): void {
    if (this.chapitreForm.invalid) {
      this.toastr.warning('Le nom du chapitre est obligatoire.');
      return;
    }
    if (!this.matiereId) {
      this.toastr.error("Erreur critique : l'ID de la matière est manquant.", "Erreur");
      return;
    }

    this.isSubmitting = true;
    const payload = this.chapitreForm.value;

    // ====================================================================
    // === CORRECTION : ON FAIT L'APPEL AU SERVICE DIRECTEMENT ICI      ===
    // ====================================================================
    this.gestionContenuService.createChapitre(this.matiereId, payload).subscribe({
      next: (nouveauChapitre: ChapitreContenu) => {
        this.toastr.success('Chapitre créé avec succès !');
        // On ferme la modale et on renvoie le nouveau chapitre au parent
        this.activeModal.close(nouveauChapitre);
      },
      error: (err) => {
        this.toastr.error(err.error?.message || 'Une erreur est survenue lors de la création.', 'Erreur');
        this.isSubmitting = false; // Très important : on arrête le spinner en cas d'erreur
      }
      // Pas besoin de 'complete', car 'close' termine le cycle de vie.
    });
  }
}
