// Fichier : src/app/features/enseignant/components/gestion-questionnaire/gestion-questionnaire.component.ts
// Version finale avec logs de debug complets

import { Component, OnInit } from '@angular/core';
import { switchMap } from 'rxjs/operators';

// --- IMPORTS DE SERVICES ---
import { QuestionnaireService, QuestionnairePayload, ParametresGeneration, QuestionnaireDetail } from '../../../../services/questionnaire.service';
import { ElementConstitutifService } from '../../../../services/element-constitutif.service';
import { ChapitreService } from '../../../../services/chapitre.service';
import { TestService } from '../../../../services/test.service';

// --- IMPORTS DE MODÈLES ---
import { Chapitre, ElementConstitutifResponse, QuestionnaireManuel, QuestionPourCreation, CreateTestRequest } from '../../../../models/models';

@Component({
  selector: 'app-gestion-questionnaire',
  templateUrl: './gestion-questionnaire.component.html',
  styleUrls: ['./gestion-questionnaire.component.css']
})
export class GestionQuestionnaireComponent implements OnInit {

  // --- PROPRIÉTÉS ---
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
    duree: 0, 
    description: '', 
    questions: []
  };
  
  parametresGeneration: ParametresGeneration = {
    titre: '', 
    nombreQuestions: 10, 
    duree: 0, 
    chapitresIds: []
  };
  
  matiereAutoId: number | null = null;
  generationEnCours = false;
  questionnaireGenere: QuestionnaireDetail | null = null;

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
    
    if (this.modeCreation === 'manuel') { 
      this.questionnaire.chapitreId = null; 
    } else { 
      this.parametresGeneration.chapitresIds = []; 
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
  }

  reinitialiserFormulaireManuel(): void {
    console.log('🔄 Réinitialisation formulaire manuel');
    this.questionnaire = { 
      titre: '', 
      matiereId: null, 
      chapitreId: null, 
      duree: 0, 
      description: '', 
      questions: [] 
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

  parametresGenerationValides(): boolean {
    const valide = !!(this.parametresGeneration.titre && 
                     this.matiereAutoId && 
                     this.parametresGeneration.chapitresIds.length > 0 && 
                     this.parametresGeneration.nombreQuestions > 0);
    console.log(`📋 Validation génération: ${valide ? '✅' : '❌'}`);
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
    
    console.log(`🔄 Changement type question ${i}: ${q.type} → ${q.type}`);
    
    q.reponseVraiFaux = undefined;
    
    if (q.type === 'qcm' || q.type === 'qcu') {
      q.reponses = [{ texte: '', correcte: false }, { texte: '', correcte: false }];
      console.log(`📝 Question ${i}: type ${q.type}, ${q.reponses.length} réponses`);
    } else if (q.type === 'texte_libre') {
      q.reponses = [{ texte: '', correcte: true }];
      console.log(`📝 Question ${i}: texte libre, 1 réponse attendue`);
    } else {
      q.reponses = [];
      console.log(`📝 Question ${i}: vrai/faux, pas de réponses prédéfinies`);
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

  toggleChapitre(id: number): void {
    const idx = this.parametresGeneration.chapitresIds.indexOf(id);
    if (idx > -1) {
      this.parametresGeneration.chapitresIds.splice(idx, 1);
      console.log(`➖ Chapitre ${id} retiré. Total: ${this.parametresGeneration.chapitresIds.length}`);
    } else {
      this.parametresGeneration.chapitresIds.push(id);
      console.log(`➕ Chapitre ${id} ajouté. Total: ${this.parametresGeneration.chapitresIds.length}`);
    }
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

  genererQuestionnaire(): void {
    if (!this.parametresGenerationValides()) { 
      alert("Veuillez remplir tous les champs obligatoires."); 
      return; 
    }
    
    console.log('🚀 Lancement génération automatique...');
    console.log('Paramètres:', this.parametresGeneration);
    
    this.generationEnCours = true;
    this.questionnaireGenere = null;
    
    this.questionnaireService.genererQuestionnaireAutomatique(this.parametresGeneration).subscribe({
      next: (questionnaire) => {
        console.log('✅ Questionnaire généré:', questionnaire);
        this.questionnaireGenere = questionnaire;
        this.generationEnCours = false;
        alert(`Questionnaire généré : "${questionnaire.titre}" !`);
        this.chargerQuestionnaires();
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
    
    // 1. VALIDATION
    if (!this.questionnaireValide()) {
      console.error('❌ Validation échouée');
      alert("Veuillez remplir tous les champs obligatoires (titre, matière, chapitre) et ajouter au moins une question.");
      return;
    }
    
    console.log('✅ Validation réussie');

    // 2. PRÉPARATION DU PAYLOAD AVEC TRANSFORMATION
    console.log('📦 Préparation du payload...');
    
    // Transformation des questions pour le backend
    const questionsPourBackend = this.questionnaire.questions.map((q, index) => {
      console.log(`  Question ${index} brute:`, q);
      
      // Mapping des types (frontend → backend)
      const typeBackend = this.mapTypeQuestion(q.type);
      console.log(`    Type: ${q.type} → ${typeBackend}`);
      
      // Transformation des réponses
      const reponsesTransformees = q.reponses?.map((r, rIndex) => {
        console.log(`    Réponse ${rIndex}: texte="${r.texte}", correcte=${r.correcte}`);
        return {
          texte: r.texte,
          correcte: r.correcte
        };
      }) || [];
      
      const questionTransformee = {
        enonce: q.enonce,
        type: typeBackend,
        points: q.points || 1,
        reponses: reponsesTransformees
      };
      
      console.log(`  Question ${index} transformée:`, questionTransformee);
      return questionTransformee;
    });
    
    // Création du payload final
    const payload: QuestionnairePayload = {
      titre: this.questionnaire.titre,
      chapitreId: this.questionnaire.chapitreId!,
      duree: this.questionnaire.duree || 0,
      description: this.questionnaire.description,
      questions: questionsPourBackend
    };
    
    console.log('📤 Payload final à envoyer:');
    console.log(JSON.stringify(payload, null, 2));
    console.log('📊 Statistiques:');
    console.log(`  - Titre: ${payload.titre}`);
    console.log(`  - ChapitreId: ${payload.chapitreId}`);
    console.log(`  - Nombre de questions: ${payload.questions.length}`);
    console.log(`  - Total réponses: ${payload.questions.reduce((acc, q) => acc + (q.reponses?.length || 0), 0)}`);

    // 3. ENVOI AU BACKEND
    console.log('🔄 Envoi au backend...');
    this.sauvegardeEnCours = true;

    this.questionnaireService.sauvegarderQuestionnaire(payload).pipe(
      switchMap((questionnaireCree: any) => {
        console.log('=== ✅ ÉTAPE 1/2: QUESTIONNAIRE CRÉÉ ===');
        console.log('Réponse backend:', questionnaireCree);
        
        if (!questionnaireCree) {
          console.error('❌ Réponse backend vide');
          throw new Error("La création du questionnaire a échoué.");
        }
        
        console.log(`✅ Questionnaire créé avec ID: ${questionnaireCree.id}`);
        console.log(`📋 Titre: ${questionnaireCree.titre}`);
        console.log(`📊 Questions créées: ${questionnaireCree.questions?.length || 0}`);
        
        // Vérification des IDs de questions
        if (!questionnaireCree.questions || questionnaireCree.questions.length === 0) {
          console.warn('⚠️ Aucune question retournée dans la réponse');
          // On continue quand même sans créer de test
          return [null];
        }
        
        const questionIds = questionnaireCree.questions.map((q: any) => q.id);
        console.log(`🔑 IDs des questions créées:`, questionIds);
        
        // Création de la requête pour le test
        const testRequest: CreateTestRequest = {
          titre: questionnaireCree.titre,
          chapitreId: questionnaireCree.chapitreId,
          questionIds: questionIds
        };
        
        console.log('=== 🚀 ÉTAPE 2/2: CRÉATION DU TEST ===');
        console.log('Payload test:', testRequest);
        
        return this.testService.createTest(testRequest);
      })
    ).subscribe({
      next: (testCree: any) => {
        console.log('=== 🎉 PROCESSUS TERMINÉ ===');
        
        if (testCree) {
          console.log('✅ Test créé avec succès:', testCree);
          alert("Questionnaire et test créés avec succès !");
        } else {
          console.log('✅ Questionnaire créé (sans test)');
          alert("Questionnaire créé avec succès !");
        }
        
        this.reinitialiserFormulaireManuel();
        this.chargerQuestionnaires();
        this.sauvegardeEnCours = false;
        
        console.log('🔄 Formulaire réinitialisé');
        console.log('🔄 Liste des questionnaires rechargée');
      },
      error: (err) => {
        console.error('=== ❌ ERREUR LORS DE LA SAUVEGARDE ===');
        console.error('Erreur complète:', err);
        console.error('Message:', err.message);
        console.error('Status:', err.status);
        console.error('Erreur backend:', err.error);
        
        let messageErreur = "Une erreur est survenue lors de la sauvegarde.";
        
        if (err.error?.message) {
          messageErreur = err.error.message;
        } else if (err.message) {
          messageErreur = err.message;
        }
        
        alert("Erreur: " + messageErreur);
        this.sauvegardeEnCours = false;
      }
    });
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
    console.log('Questionnaires chargés:', this.listeQuestionnaires.length);
    console.log('=== FIN DEBUG ===');
  }
}