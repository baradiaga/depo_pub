import { Component, OnInit } from '@angular/core';
import { AngularEditorConfig } from '@kolkov/angular-editor';
import { ChapitreService, ChapitreDetail } from '../../../services/chapitre.service';
import { SectionService } from '../../../services/section.service';
import { MatiereService } from '../../../services/matiere.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-gestion-chapitre',
  templateUrl: './gestion-chapitre.component.html',
  styleUrls: ['./gestion-chapitre.component.css']
})
export class GestionChapitreComponent implements OnInit {
  matieres: string[] = [];
  niveaux: number[] = [1, 2, 3, 4, 5];
  matiereSelectionnee: string = '';
  niveauSelectionne: number | null = null;
  chapitreCharge: ChapitreDetail | null = null;
  editorConfig: AngularEditorConfig = {   editable: true,
    spellcheck: true,
    
    // --- Paramètres pour agrandir l'éditeur ---
    height: '350px',        // Hauteur par défaut
    minHeight: '200px',       // Hauteur minimale
    maxHeight: '600px',       // Hauteur maximale avant d'afficher une barre de défilement
    
    placeholder: 'Saisissez le contenu détaillé de la section ici...',
    
    // --- Autres options utiles ---
    translate: 'no', // 'yes' peut causer des problèmes, 'no' est plus sûr
    enableToolbar: true,
    showToolbar: true,
    defaultParagraphSeparator: 'p',
    defaultFontName: 'Arial',
    toolbarHiddenButtons: [
      // Vous pouvez masquer certains boutons si la barre d'outils est trop chargée
      // Par exemple : ['insertImage', 'insertVideo', 'toggleEditorMode']
    ]};

  constructor(
    private matiereService: MatiereService,
    private chapitreService: ChapitreService,
    private sectionService: SectionService
  ) {}

  ngOnInit(): void {
    this.matiereService.getNomsMatieres().subscribe((data: string[]) => this.matieres = data);
  }

  chargerStructure(): void {
    if (!this.matiereSelectionnee || this.niveauSelectionne === null) {
      this.chapitreCharge = null;
      return;
    }

    this.chapitreService.findChapitreByMatiereAndNiveau(this.matiereSelectionnee, this.niveauSelectionne)
      .subscribe({
        // CORRECTION : Ajout du type 'ChapitreDetail'
        next: (chapitre: ChapitreDetail) => {
          this.chapitreCharge = chapitre;
          // CORRECTION : Ajout du type pour 's'
          this.chapitreCharge.sections.forEach(s => s.contenu = s.contenu || '');
          console.log('Structure chargée depuis le backend :', this.chapitreCharge);
        },
        // CORRECTION : Ajout du type 'any' pour l'erreur
        error: (err: any) => {
          this.chapitreCharge = null;
          alert(`Aucune structure de chapitre trouvée pour ${this.matiereSelectionnee} - Niveau ${this.niveauSelectionne}.`);
          console.error(err);
        }
      });
  }

  enregistrerChapitreComplet(): void {
    if (!this.chapitreCharge) {
      alert("Aucun chapitre n'est chargé.");
      return;
    }

    // CORRECTION : Ajout du type pour 'section'
    const updateObservables = this.chapitreCharge.sections.map(section => 
      this.sectionService.updateContenu(section.id, section.contenu || '') // Ajout de '|| ''' pour garantir une string
    );

    forkJoin(updateObservables).subscribe({
      next: () => {
        alert('Toutes les sections du chapitre ont été sauvegardées avec succès !');
        console.log('Chapitre complet sauvegardé :', this.chapitreCharge);
      },
      // CORRECTION : Ajout du type 'any' pour l'erreur
      error: (err: any) => {
        alert('Une erreur est survenue lors de la sauvegarde d\'une ou plusieurs sections.');
        console.error('Erreur lors de la sauvegarde groupée :', err);
      }
    });
  }

  onFileSelected(event: any, sectionId: number) {
    const file = event.target.files[0];
    if (file) {
      console.log(`Fichier '${file.name}' sélectionné pour la section ${sectionId}. La logique d'upload est à implémenter.`);
    }
  }
}
