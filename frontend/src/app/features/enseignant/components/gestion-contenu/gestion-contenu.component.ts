// Fichier : src/app/features/enseignant/components/gestion-contenu/gestion-contenu.component.ts (Version Finale avec Navigation)

import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router'; // <-- IMPORT DU ROUTER
import { ToastrService } from 'ngx-toastr';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ChapitreContenu, Section } from '../../../../models/gestion-contenu.models';
import { GestionContenuService } from '../../../../services/gestion-contenu.service';
import { FormChapitreComponent } from '../form-chapitre/form-chapitre.component';
import { FormSectionComponent } from '../form-section/form-section.component';
import { FileUploadService } from '../../../../services/file-upload.service';

@Component({
  selector: 'app-gestion-contenu',
  templateUrl: './gestion-contenu.component.html',
  styleUrls: ['./gestion-contenu.component.scss']
})
export class GestionContenuComponent implements OnInit {

  matiereId!: number;
  chapitres: ChapitreContenu[] = [];
  chapitreSelectionne: ChapitreContenu | null = null;
  isLoading = true;
  errorMessage: string | null = null;
  sectionEnEdition: Section | null = null;
  formEditionSection: FormGroup;
  isSaving = false;
  quillConfig = { toolbar: [ /* ... Votre config ... */ ] };
  private editorInstance: any;

  constructor(
    private route: ActivatedRoute,
    private gestionContenuService: GestionContenuService,
    private toastr: ToastrService,
    private modalService: NgbModal,
    private fb: FormBuilder,
    public fileUploadService: FileUploadService,
    private router: Router,
    private cdRef: ChangeDetectorRef
  ) {
    this.formEditionSection = this.fb.group({
      titre: ['', Validators.required],
      contenu: ['']
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.matiereId = +id;
      this.chargerContenu();
    } else {
      this.isLoading = false;
      this.errorMessage = "ID de matière non trouvé dans l'URL.";
      this.toastr.error(this.errorMessage);
    }
  }

  chargerContenu(): void {
    this.isLoading = true;
    this.gestionContenuService.getContenuCompletMatiere(this.matiereId).subscribe({
      next: (data) => {
        this.chapitres = data;
        if (data.length > 0) this.selectionnerChapitre(data[0]);
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = "Erreur lors du chargement du contenu.";
        this.toastr.error(this.errorMessage);
        console.error(err);
      }
    });
  }

  selectionnerChapitre(chapitre: ChapitreContenu): void {
    this.chapitreSelectionne = chapitre;
    this.annulerEdition();
  }

  ajouterChapitre(event: MouseEvent): void {
    event.stopPropagation();
    const modalRef = this.modalService.open(FormChapitreComponent, { centered: true });
    modalRef.componentInstance.matiereId = this.matiereId;
    modalRef.result.then((nouveauChapitre: ChapitreContenu) => {
      if (nouveauChapitre) {
        this.chapitres.push(nouveauChapitre);
        this.toastr.success(`Chapitre "${nouveauChapitre.nom}" créé.`);
        this.selectionnerChapitre(nouveauChapitre);
      }
    }, () => {});
  }

  ajouterSection(event: MouseEvent): void {
    event.stopPropagation();
    if (!this.chapitreSelectionne) return;
    const modalRef = this.modalService.open(FormSectionComponent, { centered: true });
    modalRef.componentInstance.chapitreId = this.chapitreSelectionne.id;
    modalRef.result.then((nouvelleSection: Section) => {
      if (nouvelleSection && this.chapitreSelectionne) {
        this.chapitreSelectionne.sections.push(nouvelleSection);
        this.toastr.success(`Section "${nouvelleSection.titre}" ajoutée.`);
      }
    }, () => {});
  }

  supprimerChapitre(chapitre: ChapitreContenu): void {
    if (confirm(`Êtes-vous sûr de vouloir supprimer le chapitre "${chapitre.nom}" ?`)) {
      this.gestionContenuService.deleteChapitre(chapitre.id).subscribe({
        next: () => {
          this.toastr.success(`Chapitre "${chapitre.nom}" supprimé.`);
          this.chapitres = this.chapitres.filter(c => c.id !== chapitre.id);
          if (this.chapitreSelectionne?.id === chapitre.id) this.chapitreSelectionne = null;
        },
        error: (err) => this.toastr.error("Erreur lors de la suppression du chapitre.")
      });
    }
  }

  supprimerSection(section: Section): void {
    if (!this.chapitreSelectionne) return;
    if (confirm(`Êtes-vous sûr de vouloir supprimer la section "${section.titre}" ?`)) {
      this.gestionContenuService.deleteSection(section.id).subscribe({
        next: () => {
          this.toastr.success(`Section "${section.titre}" supprimée.`);
          if (this.chapitreSelectionne) {
            this.chapitreSelectionne.sections = this.chapitreSelectionne.sections.filter(s => s.id !== section.id);
          }
        },
        error: (err) => this.toastr.error("Erreur lors de la suppression de la section.")
      });
    }
  }

  // ====================================================================
  // === MÉTHODE editerSection MODIFIÉE POUR GÉRER LE CAS 'QUIZ'      ===
  // ====================================================================
  editerSection(section: Section): void {
    if (section.typeSection === 'QUIZ') {
    this.toastr.info('Redirection vers le gestionnaire de questionnaires...');
    this.router.navigate(['/app/enseignant/gestion-questionnaire']); 
  } else {
    this.sectionEnEdition = section;
    this.formEditionSection.patchValue({ titre: section.titre, contenu: section.contenu });
  }
  }

  annulerEdition(): void {
    this.sectionEnEdition = null;
    this.formEditionSection.reset();
  }

  sauvegarderSection(): void {
    if (!this.sectionEnEdition || this.formEditionSection.invalid) return;
    this.isSaving = true;
    const sectionId = this.sectionEnEdition.id;
    const updatedData = this.formEditionSection.value;
    this.gestionContenuService.updateSection(sectionId, updatedData).subscribe({
      next: (sectionMiseAJour) => {
        this.toastr.success("Section mise à jour !");
        if (this.chapitreSelectionne) {
          const index = this.chapitreSelectionne.sections.findIndex(s => s.id === sectionId);
          if (index > -1) this.chapitreSelectionne.sections[index] = sectionMiseAJour;
        }
        this.annulerEdition();
      },
      error: (err) => this.toastr.error("Erreur lors de la mise à jour."),
      complete: () => this.isSaving = false
    });
  }

  getEditorInstance(editorInstance: any) { this.editorInstance = editorInstance; }
  undo() { if (this.editorInstance) this.editorInstance.history.undo(); }
  redo() { if (this.editorInstance) this.editorInstance.history.redo(); }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) {
      return;
    }

    const file = input.files[0];
    this.isSaving = true;
    this.toastr.info('Upload du fichier en cours...', 'Veuillez patienter');

    this.fileUploadService.upload(file).subscribe({
      next: (response) => {
        this.formEditionSection.get('contenu')?.setValue(response.filePath);
        this.isSaving = false;
        this.toastr.success('Fichier prêt à être sauvegardé.', 'Upload terminé');
      },
      error: (err) => {
        this.isSaving = false;
        this.toastr.error(err.error?.message || "Erreur lors de l'upload du fichier.", 'Erreur');
      }
    });
  }

  getFileName(filePath: string): string {
    if (!filePath) return '';
    return filePath.split('/').pop() || filePath;
  }
}
