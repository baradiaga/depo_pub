import { Component, OnInit } from '@angular/core';
import { BanqueQuestionService } from '../../../../services/banque-question.service';
import { ElementConstitutifService } from '../../../../services/element-constitutif.service';
import { ChapitreService } from '../../../../services/chapitre.service';
import {
  BanqueQuestionCreation,
  BanqueQuestionDetail,
  TypeQuestion,
  Niveau,
  ElementConstitutifResponse,
  Chapitre
} from '../../../../models/models';

@Component({
  selector: 'app-banque-question-gestion',
  templateUrl: './banque-question-gestion.component.html',
  styleUrls: ['./banque-question-gestion.component.css']
})
export class BanqueQuestionGestionComponent implements OnInit {

  // ------- LISTE -------
  questions: BanqueQuestionDetail[] = [];
  isLoadingList = false;

  // ------- FORMULAIRE -------
  questionForm: BanqueQuestionCreation = this.getInitialQuestionForm();
  isSaving = false;
  isEditMode = false;
  questionIdToEdit: number | null = null;

  // ------- DONNÉES -------
  mesMatieres: ElementConstitutifResponse[] = [];
  chapitresDisponibles: Chapitre[] = [];
  typesQuestion: TypeQuestion[] = ['QCM', 'QCU', 'VRAI_FAUX', 'TEXTE_LIBRE'];
  niveauxGenerique: Niveau[] = ['FACILE', 'INTERMEDIAIRE', 'DIFFICILE'];
  
  // AJOUT: Thèmes suggérés
  themesSuggestions = [
    'Algorithmie',
    'Structures de données',
    'Base de données',
    'Réseaux',
    'Sécurité',
    'Développement web',
    'Développement mobile',
    'Tests',
    'Design patterns'
  ];

  // Propriété locale pour stocker la matière sélectionnée
  selectedMatiereId: number | null = null;

  constructor(
    private banqueQuestionService: BanqueQuestionService,
    private ecService: ElementConstitutifService,
    private chapitreService: ChapitreService
  ) { }

  ngOnInit(): void {
    this.chargerMesMatieres();
    this.chargerQuestions();
  }

  // ---- INITIAL FORM ----
  getInitialQuestionForm(): BanqueQuestionCreation {
    return {
      enonce: '',
      typeQuestion: 'QCU',
      points: 1,
      chapitreId: null!, // Utilisation de l'assertion non-null
      theme: '',
      niveau: 'INTERMEDIAIRE',
      reponses: [
        { texte: '', correcte: true },
        { texte: '', correcte: false }
      ],
      tags: []
    };
  }

  // ---- CHARGEMENTS ----
  chargerMesMatieres(): void {
    this.ecService.getMesMatieres().subscribe(
      data => this.mesMatieres = data
    );
  }

  onMatiereChange(): void {
    const matiereId = this.selectedMatiereId;
    
    this.chapitresDisponibles = [];
    this.questionForm.chapitreId = null!;
    this.questionForm.theme = '';

    if (matiereId && matiereId !== 0) {
      this.chapitreService.getChapitresParMatiere(matiereId).subscribe(
        data => {
          this.chapitresDisponibles = data;
          if (data.length > 0) {
            this.questionForm.theme = data[0].nom;
          }
        }
      );
    }
  }

  onChapitreChange(): void {
    const chapitre = this.chapitresDisponibles.find(c => c.id === this.questionForm.chapitreId);
    if (chapitre) {
      if (!this.questionForm.theme || this.questionForm.theme.includes('chapitre')) {
        this.questionForm.theme = chapitre.nom;
      }
    }
  }

  chargerQuestions(): void {
    this.isLoadingList = true;
    this.banqueQuestionService.getAllQuestions().subscribe({
      next: data => {
        this.questions = data;
        this.isLoadingList = false;
      },
      error: err => {
        console.error("Erreur chargement des questions", err);
        this.isLoadingList = false;
      }
    });
  }

  // ---- RÉPONSES ----
  ajouterReponse(): void {
    if (this.questionForm.typeQuestion !== 'TEXTE_LIBRE') {
      this.questionForm.reponses.push({ texte: '', correcte: false });
    }
  }

  supprimerReponse(index: number): void {
    if (this.questionForm.reponses.length > 1) {
      this.questionForm.reponses.splice(index, 1);
    }
  }

  onTypeQuestionChange(): void {
    const type = this.questionForm.typeQuestion;

    if (type === 'QCU') {
      this.questionForm.reponses = [
        { texte: '', correcte: true },
        { texte: '', correcte: false }
      ];
    }
    else if (type === 'QCM') {
      this.questionForm.reponses = [
        { texte: '', correcte: false },
        { texte: '', correcte: false }
      ];
    }
    else if (type === 'VRAI_FAUX') {
      this.questionForm.reponses = [
        { texte: 'Vrai', correcte: true },
        { texte: 'Faux', correcte: false }
      ];
    }
    else if (type === 'TEXTE_LIBRE') {
      this.questionForm.reponses = [
        { texte: '', correcte: true }
      ];
    }
  }

  marquerReponseCorrecte(index: number): void {
    if (this.questionForm.typeQuestion === 'QCU') {
      this.questionForm.reponses.forEach((r, i) => r.correcte = (i === index));
    }
    else if (this.questionForm.typeQuestion === 'QCM') {
      this.questionForm.reponses[index].correcte = !this.questionForm.reponses[index].correcte;
    }
  }

  // ---- TAGS ----
  mettreAJourTags(value: string): void {
    this.questionForm.tags = value
      .split(',')
      .map(t => t.trim())
      .filter(t => t.length > 0);
  }

  // ---- VALIDATION ----
  validerFormulaire(): boolean {
    if (!this.questionForm.enonce.trim()) {
      alert("Veuillez saisir un énoncé.");
      return false;
    }
    if (!this.questionForm.chapitreId) {
      alert("Veuillez choisir un chapitre.");
      return false;
    }
    if (this.questionForm.points <= 0) {
      alert("Les points doivent être supérieurs à 0.");
      return false;
    }
    if (!this.questionForm.theme?.trim()) {
      alert("Veuillez saisir un thème pour la question.");
      return false;
    }
    if (!this.questionForm.niveau) {
      alert("Veuillez sélectionner un niveau de difficulté.");
      return false;
    }
    if (['QCM', 'QCU'].includes(this.questionForm.typeQuestion) &&
      this.questionForm.reponses.length < 2) {
      alert("Les questions QCM/QCU doivent avoir au moins 2 réponses.");
      return false;
    }
    if (this.questionForm.reponses.some(r => !r.texte.trim())) {
      alert("Les réponses ne peuvent pas être vides.");
      return false;
    }
    if (!this.questionForm.reponses.some(r => r.correcte)) {
      alert("Veuillez sélectionner au moins une réponse correcte.");
      return false;
    }

    return true;
  }

  // ---- SAUVEGARDE ----
  sauvegarderQuestion(): void {
    if (!this.validerFormulaire()) return;

    this.isSaving = true;

    const serviceCall = this.isEditMode
      ? this.banqueQuestionService.mettreAJourQuestion(this.questionIdToEdit!, this.questionForm)
      : this.banqueQuestionService.creerQuestion(this.questionForm);

    serviceCall.subscribe({
      next: () => {
        alert(`Question ${this.isEditMode ? 'mise à jour' : 'créée'} avec succès !`);
        this.reinitialiserFormulaire();
        this.chargerQuestions();
      },
      error: err => {
        console.error("Erreur sauvegarde", err);
        alert("Erreur lors de la sauvegarde : " + (err.error?.message || err.message));
      }
    }).add(() => this.isSaving = false);
  }

  // ---- RÉINITIALISATION ----
  reinitialiserFormulaire(): void {
    this.questionForm = this.getInitialQuestionForm();
    this.onTypeQuestionChange();
    this.isEditMode = false;
    this.questionIdToEdit = null;
    this.chapitresDisponibles = [];
    this.selectedMatiereId = null;
  }

  // ---- ÉDITION ----
  editerQuestion(question: BanqueQuestionDetail): void {
    this.isEditMode = true;
    this.questionIdToEdit = question.id;

    // Trouver la matière associée au chapitre
    const matiere = this.mesMatieres.find(m =>
      m.chapitres?.some(c => c.id === question.chapitreId)
    );

    if (matiere) {
      this.selectedMatiereId = matiere.id;

      this.chapitreService.getChapitresParMatiere(matiere.id).subscribe(chaps => {
        this.chapitresDisponibles = chaps;
        this.questionForm.chapitreId = question.chapitreId;
      });
    }

    this.questionForm = {
      enonce: question.enonce,
      typeQuestion: question.typeQuestion,
      points: question.points,
      chapitreId: question.chapitreId,
      theme: question.theme || '',
      niveau: question.niveau || 'INTERMEDIAIRE',
      reponses: question.reponses.map(r => ({ texte: r.texte, correcte: r.correcte })),
      tags: [...(question.tags || [])]
    };
  }

  // ---- SUPPRESSION ----
  supprimerQuestion(id: number, enonce: string): void {
    if (confirm(`Supprimer la question : "${enonce.substring(0, 50)}..." ?`)) {
      this.banqueQuestionService.supprimerQuestion(id).subscribe({
        next: () => {
          alert("Question supprimée.");
          this.chargerQuestions();
        },
        error: err => {
          console.error("Erreur suppression", err);
          alert("Erreur lors de la suppression.");
        }
      });
    }
  }
}