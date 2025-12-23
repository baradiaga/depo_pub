import { Component, OnInit, HostListener } from '@angular/core';
import { ChapitreService } from '../../../services/chapitre.service';
import { ChapitrePayload } from '../../../models/models';
import { ElementConstitutifService } from '../../../services/element-constitutif.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-parametrage-chapitre',
  templateUrl: './parametrage-chapitre.component.html',
  styleUrls: ['./parametrage-chapitre.component.css']
})
export class ParametrageChapitreComponent implements OnInit {
  niveau: number = 1;
  matiereSelectionnee: string = '';
  matieres: string[] = [];
  titre: string = '';
  objectif: string = '';
  sections: { titre: string }[] = [{ titre: '' }];
  isLoading: boolean = false;

  // Stepper dynamique
  etapeActive: number = 1;

  // Nouveaux champs
  typeActiviteListe: string[] = ['TP', 'TD', 'Cours', 'Autre'];
  typeActiviteSelectionne: string = '';
  typeActiviteAutre: string = '';

  prerequis: string = '';

  typeEvaluationListe: string[] = ['Quiz', 'Exercice', 'Autre'];
  typeEvaluationSelectionne: string = '';
  typeEvaluationAutre: string = '';

  constructor(
    private chapitreService: ChapitreService,
    private ecService: ElementConstitutifService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.chargerMatieres();
  }

  chargerMatieres(): void {
    this.ecService.findAllNoms().subscribe({
      next: (nomsMatieres) => {
        this.matieres = nomsMatieres;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des matières :', err);
        this.toastr.error('Impossible de charger la liste des matières depuis le serveur.');
      }
    });
  }

  // ========== MÉTHODES POUR LE STEPPER ==========
  
  allerAEtape(etape: number): void {
    this.etapeActive = etape;
    this.scrollVersEtape(etape);
  }

  scrollVersEtape(etape: number): void {
    setTimeout(() => {
      const elementId = `etape-${etape}`;
      const element = document.getElementById(elementId);
      if (element) {
        element.scrollIntoView({ 
          behavior: 'smooth', 
          block: 'start',
          inline: 'nearest'
        });
      }
    }, 100);
  }

  // Vérifie et met à jour l'étape automatiquement
  @HostListener('input')
  @HostListener('change')
  verifierProgression(): void {
    // Étape 1 : Validée si matière ET titre remplis
    if (this.matiereSelectionnee && this.titre.trim()) {
      if (this.etapeActive === 1) {
        this.etapeActive = 2;
        setTimeout(() => this.scrollVersEtape(2), 300);
      }
    }

    // Étape 2 : Validée si au moins une section a un titre
    if (this.sectionsRemplies() && this.etapeActive === 2) {
      this.etapeActive = 3;
      setTimeout(() => this.scrollVersEtape(3), 300);
    }
  }

  // ========== MÉTHODES DE VÉRIFICATION ==========
  
  sectionsRemplies(): boolean {
    return this.sections.some(s => s.titre && s.titre.trim() !== '');
  }

  sectionTitreExiste(index: number): boolean {
    return this.sections[index]?.titre?.trim() !== '';
  }

  etape2Accessible(): boolean {
    return !!this.matiereSelectionnee && !!this.titre.trim();
  }

  etape3Accessible(): boolean {
    return this.etape2Accessible() && this.sectionsRemplies();
  }

  // ========== MÉTHODES EXISTANTES ==========
  
  majStructure(): void {
    this.sections = Array(this.niveau).fill(null).map(() => ({ titre: '' }));
    this.verifierProgression();
  }

  ajouterSection(): void {
    this.sections.push({ titre: '' });
  }

  supprimerSection(index: number): void {
    if (this.sections.length > 1) {
      this.sections.splice(index, 1);
    }
  }

  enregistrerChapitre(): void {
    if (!this.matiereSelectionnee || !this.titre.trim()) {
      this.toastr.warning('Veuillez sélectionner une matière et saisir un titre pour le chapitre.');
      this.etapeActive = 1;
      this.scrollVersEtape(1);
      return;
    }

    // Vérification des sections
    const sectionsValides = this.sections.filter(s => s.titre && s.titre.trim() !== '');
    if (sectionsValides.length === 0) {
      this.toastr.warning('Veuillez ajouter au moins une section avec un titre.');
      this.etapeActive = 2;
      this.scrollVersEtape(2);
      return;
    }

    this.isLoading = true;

    const chapitrePayload: any = {
      matiere: this.matiereSelectionnee,
      titre: this.titre,
      niveau: this.niveau,
      objectif: this.objectif,
      sections: sectionsValides,
      typeActivite: this.typeActiviteSelectionne === 'Autre' ? this.typeActiviteAutre : this.typeActiviteSelectionne,
      prerequis: this.prerequis,
      typeEvaluation: this.typeEvaluationSelectionne === 'Autre' ? this.typeEvaluationAutre : this.typeEvaluationSelectionne
    };

    this.chapitreService.creerChapitre(chapitrePayload).subscribe({
      next: (response) => {
        this.toastr.success('Chapitre enregistré avec succès !');
        this.isLoading = false;
        this.reinitialiserFormulaire();
      },
      error: (err) => {
        this.isLoading = false;
        this.toastr.error(err.error?.message || 'Une erreur est survenue lors de l\'enregistrement.');
        console.error('Erreur lors de l\'enregistrement du chapitre:', err);
      }
    });
  }

  reinitialiserFormulaire(): void {
    this.titre = '';
    this.objectif = '';
    this.sections = [{ titre: '' }];
    this.matiereSelectionnee = '';
    this.niveau = 1;
    this.typeActiviteSelectionne = '';
    this.typeActiviteAutre = '';
    this.prerequis = '';
    this.typeEvaluationSelectionne = '';
    this.typeEvaluationAutre = '';
    this.etapeActive = 1;
  }

  trackByIndex(index: number): number {
    return index;
  }
}