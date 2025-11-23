// Fichier : src/app/features/enseignant/components/form-section/form-section.component.ts (Version Finale Corrigée)

import { Component, Input } from '@angular/core'; // On retire EventEmitter et Output, on ajoute Input
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastrService } from 'ngx-toastr';
import { GestionContenuService } from '../../../../services/gestion-contenu.service';
import { Section } from '../../../../models/gestion-contenu.models';

// On définit les types de section possibles, comme dans notre Enum Java
const SECTION_TYPES = ['TEXTE', 'VIDEO', 'FICHIER', 'QUIZ'] as const;
type SectionType = typeof SECTION_TYPES[number];

@Component({
  selector: 'app-form-section',
  templateUrl: './form-section.component.html',
  styleUrls: ['./form-section.component.scss']
})
export class FormSectionComponent {
  
  @Input() chapitreId!: number; // <-- On reçoit l'ID du chapitre du parent
  sectionForm: FormGroup;
  isSubmitting = false;
  sectionTypes: ReadonlyArray<SectionType> = SECTION_TYPES;

  constructor(
    public activeModal: NgbActiveModal,
    private fb: FormBuilder,
    // ====================================================================
    // === CORRECTION : INJECTION DES SERVICES NÉCESSAIRES              ===
    // ====================================================================
    private gestionContenuService: GestionContenuService,
    private toastr: ToastrService
  ) {
    this.sectionForm = this.fb.group({
      titre: ['', [Validators.required, Validators.maxLength(255)]],
      typeSection: ['TEXTE' as SectionType, Validators.required] // On sélectionne 'TEXTE' par défaut
    });
  }

  onSubmit(): void {
    if (this.sectionForm.invalid) {
      this.toastr.warning('Veuillez remplir tous les champs obligatoires.');
      return;
    }
    if (!this.chapitreId) {
      this.toastr.error("Erreur critique : l'ID du chapitre est manquant.", "Erreur");
      return;
    }

    this.isSubmitting = true;
    const payload = this.sectionForm.value;

    // ====================================================================
    // === CORRECTION : ON FAIT L'APPEL AU SERVICE DIRECTEMENT ICI      ===
    // ====================================================================
    this.gestionContenuService.createSection(this.chapitreId, payload).subscribe({
      next: (nouvelleSection: Section) => {
        this.toastr.success('Section créée avec succès !');
        // On ferme la modale et on renvoie la nouvelle section au parent
        this.activeModal.close(nouvelleSection);
      },
      error: (err) => {
        this.toastr.error(err.error?.message || 'Une erreur est survenue lors de la création de la section.', 'Erreur');
        this.isSubmitting = false; // Très important : on arrête le spinner en cas d'erreur
      }
    });
  }
}
