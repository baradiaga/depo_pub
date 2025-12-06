import { Component, OnInit } from '@angular/core';
import { QuestionnaireService, QuestionnaireDetail } from '../../../../services/questionnaire.service';
import { ElementConstitutifService } from '../../../../services/element-constitutif.service';
import { ChapitreService } from '../../../../services/chapitre.service';
import { Chapitre, ElementConstitutifResponse } from '../../../../models/models';

@Component({
  selector: 'app-questionnaire-list-component',
  templateUrl: './questionnaire-list-component.component.html',
  styleUrls: ['./questionnaire-list-component.component.css']
})
export class QuestionnaireListComponent implements OnInit {

  questionnaires: QuestionnaireDetail[] = [];
  matieres: ElementConstitutifResponse[] = [];
  chapitres: Chapitre[] = [];

  selectedMatiere: number | null = null;
  selectedChapitre: number | null = null;

  constructor(
    private questionnaireService: QuestionnaireService,
    private elementService: ElementConstitutifService,
    private chapitreService: ChapitreService
  ) {}

  ngOnInit(): void {
    this.loadMatieres();
    this.loadQuestionnaires();
  }

  // --------------------------
  // Chargement matières
  // --------------------------
  loadMatieres(): void {
    this.elementService.getMesMatieres().subscribe({
      next: (data) => this.matieres = data,
      error: (err) => console.error('Erreur chargement matières', err)
    });
  }

  // --------------------------
  // Chargement chapitres selon matière
  // --------------------------
  loadChapitres(matiereId: number): void {
    this.chapitreService.getChapitresParMatiere(matiereId).subscribe({
      next: (data) => {
        this.chapitres = data;
        this.selectedChapitre = null; // réinitialiser le chapitre sélectionné
      },
      error: (err) => console.error('Erreur chargement chapitres', err)
    });
  }

  onMatiereChange(): void {
    if (this.selectedMatiere) {
      this.loadChapitres(this.selectedMatiere);
    } else {
      this.chapitres = [];
      this.selectedChapitre = null;
    }
  }

  // --------------------------
  // Chargement questionnaires
  // --------------------------
  loadQuestionnaires(): void {
    this.questionnaireService.getQuestionnaires().subscribe({
      next: (data) => {
        console.log('Questionnaires reçus :', data);
        this.questionnaires = data;
      },
      error: (err) => console.error('Erreur chargement questionnaires', err)
    });
  }

  // --------------------------
  // Actions CRUD
  // --------------------------
  selectQuestionnaire(q: QuestionnaireDetail) {
    console.log('Questionnaire sélectionné :', q);
  }

  deleteQuestionnaire(id: number): void {
    if (!confirm('Voulez-vous vraiment supprimer ce questionnaire ?')) return;
    this.questionnaireService.supprimerQuestionnaire(id).subscribe({
      next: () => this.loadQuestionnaires(),
      error: (err) => console.error('Erreur lors de la suppression', err)
    });
  }

  // --------------------------
  // Filtrage
  // --------------------------
 filteredQuestionnaires(): QuestionnaireDetail[] {
  return this.questionnaires.filter(q => {
    const matiereOk = !this.selectedMatiere || q.nomMatiere === this.getMatiereNomById(this.selectedMatiere);
    const chapitreOk = !this.selectedChapitre || q.chapitreId === this.selectedChapitre;
    return matiereOk && chapitreOk;
  });
}

// Fonction auxiliaire pour retrouver le nom de la matière à partir de l'id
getMatiereNomById(id: number): string | undefined {
  const matiere = this.matieres.find(m => m.id === id);
  return matiere ? matiere.nom : undefined;
}



}
