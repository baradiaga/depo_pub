// Fichier : src/app/features/etudiant/components/vue-cours/vue-cours.component.ts (Version Modale)

import { Component, OnInit, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap'; // <-- 1. Import pour la modale
import { ToastrService } from 'ngx-toastr';
import { ChapitreContenu, Section } from '../../../../models/gestion-contenu.models';
import { GestionContenuService } from '../../../../services/gestion-contenu.service';

@Component({
  selector: 'app-vue-cours',
  templateUrl: './vue-cours.component.html',
  styleUrls: ['./vue-cours.component.scss']
})
export class VueCoursComponent implements OnInit {

  // 2. On reçoit l'ID de la matière via un @Input, pas depuis l'URL
  @Input() matiereId!: number; 

  chapitres: ChapitreContenu[] = [];
  chapitreSelectionne: ChapitreContenu | null = null;
  sectionSelectionnee: Section | null = null;

  isLoading = true;
  errorMessage: string | null = null;

  constructor(
    public activeModal: NgbActiveModal, // <-- 3. Injection pour contrôler la modale
    private gestionContenuService: GestionContenuService,
    private toastr: ToastrService
  ) { }

  ngOnInit(): void {
    // 4. La logique est déplacée de l'URL vers l'Input
    if (this.matiereId) {
      this.chargerContenu();
    } else {
      this.isLoading = false;
      this.errorMessage = "Aucun ID de matière n'a été fourni au composant.";
      this.toastr.error(this.errorMessage);
    }
  }

  chargerContenu(): void {
    this.isLoading = true;
    this.errorMessage = null;
    this.gestionContenuService.getContenuCompletMatiere(this.matiereId).subscribe({
      next: (data) => {
        this.chapitres = data;
        if (data.length > 0) {
          this.selectionnerChapitre(data[0]);
          if (data[0].sections.length > 0) {
            this.selectionnerSection(data[0].sections[0]);
          }
        }
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = "Erreur lors du chargement du contenu du cours.";
        this.toastr.error(this.errorMessage);
        console.error(err);
      }
    });
  }

  selectionnerChapitre(chapitre: ChapitreContenu): void {
    this.chapitreSelectionne = chapitre;
    if (chapitre.sections.length > 0) {
      this.selectionnerSection(chapitre.sections[0]);
    } else {
      this.sectionSelectionnee = null;
    }
  }

  selectionnerSection(section: Section): void {
    this.sectionSelectionnee = section;
  }
}
