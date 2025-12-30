import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ElementConstitutifService } from '../../../../services/element-constitutif.service';
import { ChapitreService, ChapitreAvecSections } from '../../../../services/chapitre.service';
import { ElementConstitutifResponse } from '../../../../models/models';

@Component({
  selector: 'app-sequences',
  templateUrl: './sequences.component.html',
  styleUrls: ['./sequences.component.css']
})
export class SequencesComponent implements OnInit {
  
  matieres: ElementConstitutifResponse[] = [];
  loading = true;
  loadingSequences = false;

  matiereSelectionnee: any = null;
  chapitreSelectionne: ChapitreAvecSections | null = null;
  vueActive: 'matieres' | 'chapitres' | 'sequence' = 'matieres';

  constructor(
    private ecService: ElementConstitutifService,
    private chapitreService: ChapitreService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.chargerDonneesInitiales();
  }

  chargerDonneesInitiales() {
    this.loading = true;
    this.ecService.getElementsConstitutifsAvecDetails().subscribe({
      next: (data) => {
        this.matieres = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur chargement matières', err);
        this.loading = false;
      }
    });
  }

  selectionnerMatiere(matiere: any): void {
    this.matiereSelectionnee = matiere;
    this.vueActive = 'chapitres';
  }

  selectionnerChapitre(chapitre: any): void {
    this.loadingSequences = true;
    this.vueActive = 'sequence';
    
    // Appel au service Chapitre pour récupérer les sections réelles
    this.chapitreService.getChapitreComplet(chapitre.id).subscribe({
      next: (chapitreComplet) => {
        this.chapitreSelectionne = chapitreComplet;
        this.loadingSequences = false;
      },
      error: (err) => {
        console.error('Erreur récupération séquences', err);
        this.loadingSequences = false;
      }
    });
  }

  /**
   * CONNECTEUR : Envoie l'étudiant vers le lecteur d'apprentissage
   */
  demarrerApprentissage(chapitreId: number) {
    this.router.navigate(['app/curriculum/apprendre', chapitreId]);
  }

  // --- LOGIQUE DE RETOUR ---
  revenirAuxMatieres() {
    this.vueActive = 'matieres';
    this.matiereSelectionnee = null;
  }

  revenirAuxChapitres() {
    this.vueActive = 'chapitres';
    this.chapitreSelectionne = null;
  }
}
