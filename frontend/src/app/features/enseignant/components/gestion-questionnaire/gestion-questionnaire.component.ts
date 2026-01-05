// Fichier : src/app/features/enseignant/components/gestion-questionnaire/gestion-questionnaire.component.ts
// Version finale adaptée pour la génération depuis la banque

import { Component, OnInit } from '@angular/core';
import { switchMap } from 'rxjs/operators';

// --- IMPORTS DE SERVICES ---
import { QuestionnaireService, QuestionnairePayload, QuestionnaireDetail, GenerationRequestDto } from '../../../../services/questionnaire.service';
import { ElementConstitutifService } from '../../../../services/element-constitutif.service';
import { ChapitreService } from '../../../../services/chapitre.service';
import { TestService } from '../../../../services/test.service';
import { ToastrService } from 'ngx-toastr';
// --- IMPORTS DE MODÈLES ---
import { Chapitre, ElementConstitutifResponse, QuestionnaireManuel, QuestionPourCreation, CreateTestRequest } from '../../../../models/models';

@Component({
  selector: 'app-gestion-questionnaire',
  templateUrl: './gestion-questionnaire.component.html',
  styleUrls: ['./gestion-questionnaire.component.css']
})
export class GestionQuestionnaireComponent implements OnInit {

  // --- PROPRIÉTÉS ---
  questionnaireIdEnModification: number | null = null;
  listeQuestionnaires: QuestionnaireDetail[] = [];
  chargementListe = false;
  modeCreation: 'manuel' | 'automatique' = 'manuel';
  mesMatieres: ElementConstitutifResponse[] = [];
  chapitresDisponibles: Chapitre[] = [];
  isLoadingMatieres = false;
  isLoadingChapitres = false;
  sauvegardeEnCours = false;
  
  questionnaire: QuestionnaireManuel = {
    titre: '', 
    matiereId: null, 
    chapitreId: null, 
    duree: 10, 
    description: '', 
    questions: [],
    typeQuestionnaire: 'TEST',
    
  };
  
  // NOUVEAUX PARAMÈTRES POUR LA GÉNÉRATION DEPUIS BANQUE
  generationRequest: GenerationRequestDto = {
    themes: [],
    nombreQuestions: 10,
    niveau: 'MOYEN'
  };
  
  matiereAutoId: number | null = null;
  generationEnCours = false;
  questionnaireGenere: QuestionnaireDetail | null = null;
  
  // NIVEAUX DISPONIBLES POUR LA GÉNÉRATION
  niveauxDisponibles = [
    { value: 'FACILE', label: 'Facile' },
    { value: 'MOYEN', label: 'Moyen' },
    { value: 'DIFFICILE', label: 'Difficile' }
  ];

  constructor(
    private questionnaireService: QuestionnaireService,
    private ecService: ElementConstitutifService,
    private chapitreService: ChapitreService,
    private testService: TestService
  ) {}

  ngOnInit(): void {
    console.log('🔄 ngOnInit - Chargement initial');
    this.chargerMesMatieres();
    this.chargerQuestionnaires();
  }

  chargerMesMatieres(): void {
    console.log('📥 Chargement des matières...');
    this.isLoadingMatieres = true;
    this.ecService.getMesMatieres().subscribe({
      next: (data) => {
        console.log(`✅ ${data.length} matière(s) chargée(s):`, data.map(m => m.nom));
        this.mesMatieres = data;
        this.isLoadingMatieres = false;
      },
      error: (err) => {
        console.error('❌ Erreur chargement matières:', err);
        this.isLoadingMatieres = false;
      }
    });
  }

  onMatiereChange(): void {
    const matiereId = this.modeCreation === 'manuel' 
      ? this.questionnaire.matiereId 
      : this.matiereAutoId;
    
    console.log(`🔄 Changement matière - Mode: ${this.modeCreation}, ID: ${matiereId}`);
    
    this.chapitresDisponibles = [];
    this.generationRequest.themes = []; // Réinitialiser les thèmes
    
    if (this.modeCreation === 'manuel') { 
      this.questionnaire.chapitreId = null; 
    }
    
    if (!matiereId) {
      console.log('⚠️ Aucune matière sélectionnée');
      return;
    }
    
    this.isLoadingChapitres = true;
    this.chapitreService.getChapitresParMatiere(matiereId).subscribe({
      next: (data) => {
        console.log(`✅ ${data.length} chapitre(s) chargé(s) pour matière ${matiereId}`);
        this.chapitresDisponibles = data;
        this.isLoadingChapitres = false;
      },
      error: (err) => {
        console.error(`❌ Erreur chargement chapitres pour matière ${matiereId}:`, err);
        this.isLoadingChapitres = false;
      }
    });
  }

  chargerQuestionnaires(): void {
    console.log('📥 Chargement des questionnaires...');
    this.chargementListe = true;
    this.questionnaireService.getQuestionnaires().subscribe({
      next: (data) => { 
        console.log(`✅ ${data.length} questionnaire(s) chargé(s)`);
        this.listeQuestionnaires = data; 
        this.chargementListe = false; 
      },
      error: (err) => { 
        console.error('❌ Erreur chargement questionnaires:', err); 
        this.chargementListe = false; 
      }
    });
  }

  changerMode(event: Event): void {
    const nouveauMode = (event.target as HTMLSelectElement).value as 'manuel' | 'automatique';
    console.log(`🔄 Changement mode: ${this.modeCreation} → ${nouveauMode}`);
    this.modeCreation = nouveauMode;
    this.chapitresDisponibles = [];
    this.generationRequest.themes = []; // Réinitialiser les thèmes
  }

  reinitialiserFormulaireManuel(): void {
    console.log('🔄 Réinitialisation formulaire manuel');
    this.questionnaire = { 
      titre: '', 
      matiereId: null, 
      chapitreId: null, 
      duree: 0, 
      description: '', 
      questions: [] ,
      typeQuestionnaire: 'EXERCICE'
    };
    this.chapitresDisponibles = [];
  }

  questionnaireValide(): boolean {
    const valide = !!(this.questionnaire.titre && 
                     this.questionnaire.matiereId && 
                     this.questionnaire.chapitreId && 
                     this.questionnaire.questions.length > 0);
    console.log(`📋 Validation questionnaire: ${valide ? '✅' : '❌'}`, {
      titre: this.questionnaire.titre,
      matiereId: this.questionnaire.matiereId,
      chapitreId: this.questionnaire.chapitreId,
      nbQuestions: this.questionnaire.questions.length
    });
    return valide;
  }
   modifierQuestionnaire(q: QuestionnaireDetail): void {
  console.log('🔄 Modification questionnaire:', q);

  // 1️⃣ Pré-remplir le formulaire manuel
  this.modeCreation = 'manuel';

  // Assigner les propriétés principales
  this.questionnaire.titre = q.titre;
  this.questionnaire.description = q.description ?? '';
  this.questionnaire.duree = q.duree ?? 0;
  this.questionnaire.matiereId = q.matiereId ?? null;
  this.questionnaire.chapitreId = q.chapitreId ?? null;
  this.questionnaireIdEnModification = q.id;

  // Si tu veux gérer le type de questionnaire (test, quiz, exercice)
  this.questionnaire.typeQuestionnaire = (q as any).typeQuestionnaire ?? 'test'; // par défaut

  // 2️⃣ Charger les chapitres pour la matière
  if (this.questionnaire.matiereId) {
    this.onMatiereChange();
  }

  // 3️⃣ Pré-remplir les questions
  const typeMap: { [key: string]: 'qcm' | 'qcu' | 'vrai_faux' | 'texte_libre' } = {
    'QCM': 'qcm',
    'QCU': 'qcu',
    'VRAI_FAUX': 'vrai_faux',
    'TEXTE_LIBRE': 'texte_libre'
  };

  this.questionnaire.questions = q.questions.map((question: any) => ({
    type: typeMap[question.type] ?? 'qcm',
    enonce: question.enonce,
    points: question.points ?? 1,
    difficulte: question.difficulte ?? 'moyen',
    reponses: question.reponses?.map((r: any) => ({
      texte: r.texte ?? '',
      correcte: r.correcte ?? false
    })) ?? [],
    reponseVraiFaux: question.reponseVraiFaux ?? null
  }));

  console.log('✅ Formulaire pré-rempli:', this.questionnaire);
}

  // NOUVELLE MÉTHODE DE VALIDATION POUR LA GÉNÉRATION
  generationRequestValide(): boolean {
    const valide = !!(this.matiereAutoId && 
                     this.generationRequest.themes.length > 0 && 
                     this.generationRequest.nombreQuestions > 0 &&
                     this.generationRequest.niveau);
    console.log(`📋 Validation génération: ${valide ? '✅' : '❌'}`, this.generationRequest);
    return valide;
  }

  ajouterQuestion(): void {
    const nouvelleQuestion: QuestionPourCreation = { 
      type: 'qcm', 
      enonce: '', 
      points: 1, 
      difficulte: 'moyen', 
      reponses: [
        { texte: '', correcte: false }, 
        { texte: '', correcte: false }
      ] 
    };
    
    this.questionnaire.questions.push(nouvelleQuestion);
    console.log(`➕ Question ajoutée. Total: ${this.questionnaire.questions.length}`);
    console.log('Dernière question:', nouvelleQuestion);
  }

  supprimerQuestion(index: number): void {
    console.log(`🗑️ Suppression question index ${index}`);
    this.questionnaire.questions.splice(index, 1);
    console.log(`📊 Questions restantes: ${this.questionnaire.questions.length}`);
  }

  changerTypeQuestion(i: number): void {
  const q = this.questionnaire.questions[i];
  if (!q) return;
  
  console.log(`🔄 Changement type question ${i}: ${q.type}`);
  
  // 1. On réinitialise toujours le tableau pour éviter les mélanges
  q.reponses = [];

  if (q.type === 'qcm' || q.type === 'qcu') {
    q.reponses = [{ texte: '', correcte: false }, { texte: '', correcte: false }];
  } 
  else if (q.type === 'texte_libre') {
    q.reponses = [{ texte: '', correcte: true }];
  } 
  else if (q.type === 'vrai_faux') {
    // CORRECTION ICI : On crée les deux options que le Backend attend
    q.reponses = [
      { texte: 'Vrai', correcte: true },  // Par défaut Vrai est correct
      { texte: 'Faux', correcte: false }
    ];
    console.log(`📝 Question ${i}: vrai/faux, 2 options générées`);
  }
}
// À ajouter dans le fichier .ts
setVraiFauxCorrect(question: any, estVrai: boolean): void {
  if (question.reponses && question.reponses.length >= 2) {
    // Si estVrai est true, l'index 0 (Vrai) devient correct, l'index 1 (Faux) devient faux
    question.reponses[0].correcte = estVrai;
    question.reponses[1].correcte = !estVrai;
  }
}


  ajouterReponse(i: number): void {
    const question = this.questionnaire.questions[i];
    if (!question || !question.reponses) return;
    
    question.reponses.push({ texte: '', correcte: false });
    console.log(`➕ Réponse ajoutée à question ${i}. Total: ${question.reponses.length}`);
  }

  supprimerReponse(i: number, j: number): void {
    const reponses = this.questionnaire.questions[i]?.reponses;
    if (reponses && reponses.length > 2) {
      console.log(`🗑️ Suppression réponse ${j} de question ${i}`);
      reponses.splice(j, 1);
      console.log(`📊 Réponses restantes: ${reponses.length}`);
    } else {
      console.log(`⚠️ Impossible de supprimer (min 2 réponses)`);
    }
  }

  marquerReponseCorrecte(question: QuestionPourCreation, reponseIndex: number): void {
    if (!question.reponses) { 
      console.log('⚠️ Aucune réponse à marquer');
      return; 
    }
    
    console.log(`🎯 Marquage réponse ${reponseIndex} comme correcte (type: ${question.type})`);
    
    if (question.type === 'qcu') {
      question.reponses.forEach((rep, idx) => {
        rep.correcte = (idx === reponseIndex);
        console.log(`  Réponse ${idx}: ${rep.correcte ? '✓' : '✗'}`);
      });
    } else if (question.type === 'qcm') {
      const reponse = question.reponses[reponseIndex];
      if (reponse) { 
        reponse.correcte = !reponse.correcte;
        console.log(`  Réponse ${reponseIndex}: ${reponse.correcte ? '✓' : '✗'}`);
      }
    }
  }

  // NOUVELLE MÉTHODE POUR TOGGLE LES THÈMES
  toggleTheme(chapitreId: number): void {
    const chapitre = this.chapitresDisponibles.find(c => c.id === chapitreId);
    if (!chapitre) return;
    
    const themeIndex = this.generationRequest.themes.indexOf(chapitre.nom);
    
    if (themeIndex > -1) {
      this.generationRequest.themes.splice(themeIndex, 1);
      console.log(`➖ Thème "${chapitre.nom}" retiré. Total: ${this.generationRequest.themes.length}`);
    } else {
      this.generationRequest.themes.push(chapitre.nom);
      console.log(`➕ Thème "${chapitre.nom}" ajouté. Total: ${this.generationRequest.themes.length}`);
    }
  }

  // VÉRIFIE SI UN CHAPITRE EST SÉLECTIONNÉ COMME THÈME
  isThemeSelected(chapitreId: number): boolean {
    const chapitre = this.chapitresDisponibles.find(c => c.id === chapitreId);
    return chapitre ? this.generationRequest.themes.includes(chapitre.nom) : false;
  }

  supprimerQuestionnaire(id: number, titre: string): void {
    if (confirm(`Supprimer le questionnaire "${titre}" ?`)) {
      console.log(`🗑️ Suppression questionnaire ID: ${id}`);
      this.questionnaireService.supprimerQuestionnaire(id).subscribe({
        next: () => { 
          console.log(`✅ Questionnaire ${id} supprimé`);
          alert("Questionnaire supprimé !"); 
          this.chargerQuestionnaires(); 
        },
        error: (err) => { 
          console.error('❌ Erreur suppression:', err); 
          alert("Erreur lors de la suppression."); 
        }
      });
    }
  }

  // NOUVELLE MÉTHODE DE GÉNÉRATION ADAPTÉE
  genererQuestionnaireDepuisBanque(): void {
    if (!this.generationRequestValide()) { 
      alert("Veuillez sélectionner une matière, au moins un thème, et spécifier le nombre de questions et le niveau."); 
      return; 
    }
    
    console.log('🚀 Lancement génération depuis banque...');
    console.log('Paramètres:', this.generationRequest);
    
    this.generationEnCours = true;
    this.questionnaireGenere = null;
    
    this.questionnaireService.genererQuestionnaireDepuisBanque(this.generationRequest).subscribe({
      next: (questionnaire) => {
        console.log("Request génération:", this.generationRequest);

        console.log('✅ Questionnaire généré:', questionnaire);
        this.questionnaireGenere = questionnaire;
        this.generationEnCours = false;
        alert(`Questionnaire généré : "${questionnaire.titre}" !`);
        this.chargerQuestionnaires();
        
        // Réinitialiser les paramètres après génération
        this.generationRequest = {
          themes: [],
          nombreQuestions: 10,
          niveau: 'MOYEN'
        };
      },
      error: (err) => {
        console.error('❌ Erreur génération:', err);
        console.error('Détails erreur:', err.error);
        alert("Erreur: " + (err.error?.message || "Inconnue."));
        this.generationEnCours = false;
      }
    });
  }

  // ====================================================================
  // === MÉTHODE DE SAUVEGARDE AVEC LOGS COMPLETS ===
  // ====================================================================
    sauvegarderQuestionnaire(): void {
    console.log('=== 🚀 DÉBUT SAUVEGARDE QUESTIONNAIRE ===');

    // 1️⃣ Validation formulaire (utilise votre méthode existante)
    if (!this.questionnaireValide()) {
      console.error('❌ Validation échouée');
      alert("Veuillez remplir tous les champs obligatoires (titre, matière, chapitre) et ajouter au moins une question.");
      return;
    }

    // 2️⃣ Préparation du payload (Mapping vers QuestionnairePayload du service)
    const questionsTransformees = this.questionnaire.questions.map(q => ({
      enonce: q.enonce,
      type: this.mapTypeQuestionBackend(q.type),
      points: q.points || 1,
      reponses: q.reponses?.map(r => ({
        texte: r.texte,
        correcte: r.correcte
      })) || []
    }));

    const payload: QuestionnairePayload = {
      id: this.questionnaireIdEnModification || undefined,
      titre: this.questionnaire.titre,
      description: this.questionnaire.description || '',
      chapitreId: Number(this.questionnaire.chapitreId),
      duree: this.questionnaire.duree || 0,
      type: (this.questionnaire.typeQuestionnaire as any) || 'EXERCICE',
      questions: questionsTransformees
    };

    this.sauvegardeEnCours = true;

    // 3️⃣ Appel au service (utilise la méthode sauvegarderQuestionnaire du service)
    this.questionnaireService.sauvegarderQuestionnaire(payload).subscribe({
      next: (res: any) => {
        console.log('✅ Opération réussie');
        const message = this.questionnaireIdEnModification ? "Le questionnaire a été modifié." : "Le questionnaire a été créé.";
        alert(message);
        
        // 4️⃣ Reset et rafraîchissement
        this.sauvegardeEnCours = false;
        this.questionnaireIdEnModification = null;
        this.reinitialiserFormulaireManuel();
        this.chargerQuestionnaires();
      },
      error: (err: any) => {
        console.error('❌ Erreur lors de la sauvegarde:', err);
        this.sauvegardeEnCours = false;
        alert("Une erreur est survenue lors de l'enregistrement sur le serveur.");
      }
    });
  }

  /**
   * Helper pour mapper les types de questions vers les constantes Backend
   */
  private mapTypeQuestionBackend(typeFront: string): 'QCM' | 'QCU' | 'VRAI_FAUX' | 'TEXTE_LIBRE' {
    const mapping: { [key: string]: any } = {
      'qcm': 'QCM',
      'qcu': 'QCU',
      'vrai_faux': 'VRAI_FAUX',
      'texte_libre': 'TEXTE_LIBRE'
    };
    return mapping[typeFront] || 'QCM';
  }


  // ====================================================================
  // === MÉTHODES UTILITAIRES ===
  // ====================================================================
  
  /**
   * Convertit les types de questions du frontend vers le backend
   */
  private mapTypeQuestion(typeFront: string): string {
    const mapping: {[key: string]: string} = {
      'qcm': 'QCM',
      'qcu': 'QCU',
      'vrai_faux': 'VRAI_FAUX',
      'texte_libre': 'TEXTE_LIBRE'
    };
    
    const typeBackend = mapping[typeFront] || typeFront.toUpperCase();
    console.log(`🔄 Mapping type: ${typeFront} → ${typeBackend}`);
    return typeBackend;
  }
 
  /**
   * Méthode de debug pour afficher l'état complet
   */
  debugEtatComplet(): void {
    console.log('=== 🐛 DEBUG ÉTAT COMPLET ===');
    console.log('Mode:', this.modeCreation);
    console.log('Questionnaire manuel:', this.questionnaire);
    console.log('Questions:', this.questionnaire.questions);
    console.log('Matières disponibles:', this.mesMatieres.length);
    console.log('Chapitres disponibles:', this.chapitresDisponibles.length);
    console.log('Generation Request:', this.generationRequest);
    console.log('Questionnaires chargés:', this.listeQuestionnaires.length);
    console.log('=== FIN DEBUG ===');
  }
}