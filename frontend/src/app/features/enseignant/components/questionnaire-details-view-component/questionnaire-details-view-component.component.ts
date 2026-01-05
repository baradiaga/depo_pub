import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { QuestionnaireService, QuestionnaireDetail, Question } from '../../../../services/questionnaire.service';
import { TestService } from '../../../../services/test.service'; 
@Component({
  selector: 'app-questionnaire-details-view-component',
  templateUrl: './questionnaire-details-view-component.component.html',
  styleUrls: ['./questionnaire-details-view-component.component.css']
})
export class QuestionnaireDetailsViewComponentComponent implements OnInit {

  questionnaireId!: number;
  questionnaire: QuestionnaireDetail | null = null;
  
  // États de chargement
  loadingQuestionnaire = true;
  errorLoadingQuestionnaire = false;
  
  // ⚡ AJOUTEZ CETTE PROPRIÉTÉ MANQUANTE :
  isDeleting = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private questionnaireService: QuestionnaireService,
    private testService: TestService
  ) {}

  ngOnInit(): void {
    // Récupérer l'ID du questionnaire depuis l'URL
    this.route.params.subscribe(params => {
      this.questionnaireId = +params['id'];
      this.loadQuestionnaireDetails();
    });
  }

  // -------------------
  // CHARGE LE QUESTIONNAIRE (avec ses questions incluses)
  // -------------------
  loadQuestionnaireDetails(): void {
    this.loadingQuestionnaire = true;
    this.errorLoadingQuestionnaire = false;

    this.questionnaireService.getQuestionnaireById(this.questionnaireId)
      .subscribe({
        next: (data) => {
          console.log('✅ Questionnaire complet reçu:', data);
          console.log(`📋 ${data.questions?.length || 0} questions incluses`);
          
          // DEBUG DÉTAILLÉ
          if (data.questions) {
            console.log('📝 Détail des questions:');
            data.questions.forEach((q, i) => {
              console.log(`  Q${i+1}: "${q.enonce}" (${q.type})`);
              console.log(`     Points: ${q.points}, Réponses: ${q.reponses?.length || 0}`);
              if (q.reponses && q.reponses.length > 0) {
                q.reponses.forEach((r, j) => {
                  console.log(`       R${j+1}: "${r.texte}" ${r.correcte ? '✓' : '✗'}`);
                });
              }
            });
          }
          
          this.questionnaire = data;
          this.loadingQuestionnaire = false;
        },
        error: (err) => {
          console.error('❌ Erreur chargement questionnaire:', err);
          this.errorLoadingQuestionnaire = true;
          this.loadingQuestionnaire = false;
        }
      });
  }

  // -------------------
  // NAVIGATION ET ACTIONS
  // -------------------

  // 🔥 Redirection vers l'édition
  navigateToEdit(): void {
    console.log('📝 Redirection vers édition du questionnaire', this.questionnaireId);
    this.router.navigate(['/enseignant/questionnaires/edit', this.questionnaireId]);
  }

  // 🔥 Retour à la liste
  navigateToList(): void {
    this.router.navigate(['/app/enseignant/gestion-questionnaire']);
  }

  // 🔥 Suppression avec confirmation
  deleteQuestionnaire(): void {
    if (!this.questionnaire) return;
    
    const confirmation = confirm(`Êtes-vous sûr de vouloir supprimer le questionnaire "${this.questionnaire.titre}" ?`);
    
    if (confirmation) {
      this.isDeleting = true;
      
      this.questionnaireService.supprimerQuestionnaire(this.questionnaireId)
        .subscribe({
          next: () => {
            console.log('✅ Questionnaire supprimé avec succès');
            alert('Questionnaire supprimé avec succès');
            this.navigateToList();
          },
          error: (err) => {
            console.error('❌ Erreur lors de la suppression:', err);
            alert('Erreur lors de la suppression');
            this.isDeleting = false;
          }
        });
    }
  }

  // 🔥 Dupliquer le questionnaire
  duplicateQuestionnaire(): void {
    if (!this.questionnaire) return;
    
    const newTitle = prompt('Nouveau titre pour la copie :', `${this.questionnaire.titre} (Copie)`);
    
    if (newTitle && newTitle.trim()) {
      // Logique de duplication à implémenter selon votre API
      console.log('Duplication vers:', newTitle);
      alert(`Duplication en cours vers: "${newTitle}"\n(Fonctionnalité à implémenter)`);
      // this.questionnaireService.duplicateQuestionnaire(this.questionnaireId, newTitle)
      //   .subscribe(...);
    }
  }

  // 🔥 Créer un test à partir du questionnaire
  // 🔥 Assigner ce questionnaire comme test officiel du chapitre
createTestFromQuestionnaire(): void {
  if (!this.questionnaire || !this.questionnaire.chapitreId) {
    alert("Impossible d'assigner ce test : ID du chapitre manquant.");
    return;
  }

  const confirmation = confirm(
    `Voulez-vous définir le questionnaire "${this.questionnaire.titre}" comme le test officiel pour ce chapitre ?\n\n` +
    `Cela remplacera le test actuel si un autre était déjà assigné.`
  );

  if (confirmation) {
    this.loadingQuestionnaire = true; // On réutilise le loader pour l'action

    this.testService.assignerQuestionnaire(this.questionnaire.chapitreId, this.questionnaireId)
      .subscribe({
        next: (response) => {
          console.log('✅ Test assigné avec succès:', response);
          alert('Le questionnaire a été assigné avec succès ! Les étudiants peuvent maintenant passer ce test.');
          this.loadingQuestionnaire = false;
          // Optionnel : rediriger vers la vue du chapitre ou du test
          // this.router.navigate(['/app/enseignant/chapitres', this.questionnaire.chapitreId]);
        },
        error: (err) => {
          console.error('❌ Erreur lors de l\'assignation:', err);
          alert('Erreur lors de l\'assignation du test. Vérifiez que le chapitre est valide.');
          this.loadingQuestionnaire = false;
        }
      });
  }
}


  // -------------------
  // MÉTHODES UTILES POUR LE TEMPLATE
  // -------------------
  
  // Getter pour accéder facilement aux questions
  get questions(): Question[] {
    return this.questionnaire?.questions || [];
  }
  
  // Vérifie s'il y a des questions
  get hasQuestions(): boolean {
    return this.questions.length > 0;
  }
  
  // Traduit les types de questions en français
  getQuestionTypeLabel(type: string): string {
    const labels: {[key: string]: string} = {
      'QCM': 'Choix Multiple',
      'QCU': 'Choix Unique',
      'VRAI_FAUX': 'Vrai ou Faux',
      'TEXTE_LIBRE': 'Texte Libre'
    };
    return labels[type] || type;
  }
  
  // Retourne une classe CSS selon le type de question
  getQuestionTypeClass(type: string): string {
    const classes: {[key: string]: string} = {
      'QCM': 'badge bg-primary',
      'QCU': 'badge bg-info',
      'VRAI_FAUX': 'badge bg-warning',
      'TEXTE_LIBRE': 'badge bg-success'
    };
    return classes[type] || 'badge bg-secondary';
  }
  
  // Formate la durée
  formatDuree(minutes: number | undefined): string {
    if (!minutes) return 'Non spécifiée';
    if (minutes < 60) return `${minutes} min`;
    const heures = Math.floor(minutes / 60);
    const minsRestantes = minutes % 60;
    return minsRestantes > 0 ? `${heures}h${minsRestantes}` : `${heures}h`;
  }
  
  // Rafraîchir les données
  refreshData(): void {
    this.loadQuestionnaireDetails();
  }
  
  // -------------------
  // MÉTHODES POUR LA GESTION DES RÉPONSES
  // -------------------
  
  // Compte le nombre de réponses correctes pour une question
  countCorrectAnswers(question: Question): number {
    if (!question.reponses) return 0;
    return question.reponses.filter(r => r.correcte).length;
  }
  
  // Vérifie si une question a des réponses
 hasReponses(question: Question): boolean {
  return question.reponses !== undefined && question.reponses.length > 0;
}

  
  // Retourne la réponse correcte pour les questions à réponse unique
  getCorrectAnswer(question: Question): string | null {
    if (!this.hasReponses(question)) return null;
    const correct = question.reponses?.find(r => r.correcte);
    return correct ? correct.texte : null;
  }

  // -------------------
  // MÉTHODES STATISTIQUES (pour la section stats)
  // -------------------

  // ⚡ AJOUTEZ CES 3 MÉTHODES MANQUANTES :

  // Compte le nombre total de réponses
  countTotalReponses(): number {
    if (!this.questionnaire?.questions) return 0;
    return this.questionnaire.questions.reduce((total, q) => {
      return total + (q.reponses?.length || 0);
    }, 0);
  }

  // Compte les questions de type QCM/QCU
  countQCMQuestions(): number {
    if (!this.questionnaire?.questions) return 0;
    return this.questionnaire.questions.filter(q => 
      q.type === 'QCM' || q.type === 'QCU'
    ).length;
  }

  // Calcule le total des points
  calculateTotalPoints(): number {
    if (!this.questionnaire?.questions) return 0;
    return this.questionnaire.questions.reduce((total, q) => {
      return total + (q.points || 0);
    }, 0);
  }

  // -------------------
  // MÉTHODES SUPPLÉMENTAIRES (optionnelles)
  // -------------------

  // Exporte le questionnaire en JSON
  exportQuestionnaire(): void {
    if (!this.questionnaire) return;
    
    const dataStr = JSON.stringify(this.questionnaire, null, 2);
    const dataUri = 'data:application/json;charset=utf-8,'+ encodeURIComponent(dataStr);
    
    const exportFileDefaultName = `questionnaire-${this.questionnaireId}-${new Date().toISOString().split('T')[0]}.json`;
    
    const linkElement = document.createElement('a');
    linkElement.setAttribute('href', dataUri);
    linkElement.setAttribute('download', exportFileDefaultName);
    linkElement.click();
    
    console.log('✅ Questionnaire exporté');
    alert('Questionnaire exporté en JSON');
  }

  // Imprime la page
  printQuestionnaire(): void {
    window.print();
  }
}