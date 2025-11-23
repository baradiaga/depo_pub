// Fichier : gestion-chapitre.component.ts (Version corrigée avec ngx-quill)

import { Component, OnInit } from '@angular/core';
import { forkJoin, Observable } from 'rxjs';
import { ToastrService } from 'ngx-toastr';

// --- Services ---
import { ChapitreService } from '../../../services/chapitre.service';
import { SectionService } from '../../../services/section.service';
import { ElementConstitutifService } from '../../../services/element-constitutif.service';

// --- Modèles ---
import { Chapitre, Section, ElementConstitutifResponse } from '../../../models/models';

@Component({
  selector: 'app-gestion-chapitre',
  templateUrl: './gestion-chapitre.component.html',
  styleUrls: ['./gestion-chapitre.component.css']
})
export class GestionChapitreComponent implements OnInit {
  
  matieres$!: Observable<ElementConstitutifResponse[]>;
  niveaux: number[] = [1, 2, 3, 4, 5];

  selectedMatiereNom: string = '';
  selectedNiveau: number | null = null;
  chapitreCharge: Chapitre | null = null;

  isLoading = false;

  // La propriété 'editorConfig' pour @kolkov/angular-editor a été supprimée.
  // ngx-quill n'en a pas besoin pour une configuration simple.

  constructor(
    private ecService: ElementConstitutifService,
    private chapitreService: ChapitreService,
    private sectionService: SectionService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.matieres$ = this.ecService.findAll();
  }

  chargerStructure(): void {
    if (!this.selectedMatiereNom || this.selectedNiveau === null) {
      this.chapitreCharge = null;
      return;
    }

    this.isLoading = true;
    this.chapitreService.findChapitreByMatiereAndNiveau(this.selectedMatiereNom, this.selectedNiveau)
      .subscribe({
        next: (chapitre: Chapitre) => {
          this.chapitreCharge = chapitre;
          this.chapitreCharge.sections = this.chapitreCharge.sections || [];
          this.chapitreCharge.sections.forEach(s => s.contenu = s.contenu || '');
          this.isLoading = false;
        },
        error: (err: any) => {
          this.chapitreCharge = null;
          this.isLoading = false;
          this.toastr.error(`Aucune structure de chapitre trouvée pour ${this.selectedMatiereNom} - Niveau ${this.selectedNiveau}.`);
          console.error(err);
        }
      });
  }

  enregistrerChapitreComplet(): void {
    if (!this.chapitreCharge) {
      this.toastr.warning("Aucun chapitre n'est chargé.");
      return;
    }

    const updateObservables = this.chapitreCharge.sections.map((section: Section) => 
      this.sectionService.updateContenu(section)
    );

    forkJoin(updateObservables).subscribe({
      next: () => {
        this.toastr.success('Toutes les sections du chapitre ont été sauvegardées !');
      },
      error: (err: any) => {
        this.toastr.error('Une erreur est survenue lors de la sauvegarde.');
        console.error('Erreur lors de la sauvegarde groupée :', err);
      }
    });
  }

  onFileSelected(event: any, sectionId: number): void {
    const file = event.target.files[0];
    if (file) {
      console.log(`Fichier '${file.name}' sélectionné pour la section ${sectionId}. La logique d'upload est à implémenter.`);
    }
  }
}
