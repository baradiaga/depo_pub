import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-evaluations',
  templateUrl: './evaluations.component.html',
  styleUrls: ['./evaluations.component.css']
})
export class EvaluationsComponent implements OnInit {

  // Types d'évaluations
  typesEvaluations = [
    { id: 'tous', label: 'Toutes', icon: '📋', couleur: '#6c757d' },
    { id: 'quiz', label: 'Quiz', icon: '❓', couleur: '#4361ee' },
    { id: 'examen', label: 'Examens', icon: '📝', couleur: '#f72585' },
    { id: 'projet', label: 'Projets', icon: '🚀', couleur: '#f8961e' },
    { id: 'oral', label: 'Orals', icon: '🎤', couleur: '#4cc9f0' }
  ];

  // Filtres par statut
  filtresStatut = [
    { id: 'tous', label: 'Toutes', icon: '📊' },
    { id: 'a-venir', label: 'À venir', icon: '📅' },
    { id: 'en-cours', label: 'En cours', icon: '⏳' },
    { id: 'terminee', label: 'Terminées', icon: '✅' },
    { id: 'corrigee', label: 'Corrigées', icon: '📈' }
  ];

  // Données des évaluations
  evaluations = [
    {
      id: 1,
      titre: 'Examen final Java',
      description: 'Évaluation complète des concepts Java',
      type: 'examen',
      statut: 'corrigee',
      matiere: 'Java',
      chapitre: 'Tous les chapitres',
      date: '15/02/2024',
      duree: '3h',
      difficulte: 'avance',
      pointsMax: 200,
      pointsObtenus: 175,
      note: 17.5,
      seuilReussite: 10,
      tauxReussite: 85,
      tentatives: 1,
      tentativesMax: 2,
      estNote: true,
      estObligatoire: true,
      competences: ['Programmation', 'Algorithmique', 'Debugging'],
      ressources: ['Instructions', 'Feuille de notes']
    },
    {
      id: 2,
      titre: 'Quiz sur les algorithmes',
      description: 'Questions rapides sur les bases algorithmiques',
      type: 'quiz',
      statut: 'terminee',
      matiere: 'Algorithme',
      chapitre: 'Algorithmes fondamentaux',
      date: '20/02/2024',
      duree: '45min',
      difficulte: 'intermediaire',
      pointsMax: 100,
      pointsObtenus: 92,
      note: 18.4,
      seuilReussite: 10,
      tauxReussite: 92,
      tentatives: 1,
      tentativesMax: 3,
      estNote: false,
      estObligatoire: false,
      competences: ['Logique', 'Complexité'],
      ressources: ['Formulaire']
    },
    {
      id: 3,
      titre: 'Projet: Application web',
      description: 'Développement d\'une application web complète',
      type: 'projet',
      statut: 'en-cours',
      matiere: 'Java',
      chapitre: 'Programmation avancée',
      date: '25/02/2024',
      duree: '10h',
      difficulte: 'avance',
      pointsMax: 300,
      pointsObtenus: null,
      note: null,
      seuilReussite: 10,
      tauxReussite: null,
      tentatives: 1,
      tentativesMax: 1,
      estNote: true,
      estObligatoire: true,
      competences: ['Backend', 'Frontend', 'Base de données'],
      ressources: ['Cahier des charges', 'Maquettes']
    },
    {
      id: 4,
      titre: 'Examen intermédiaire SQL',
      description: 'Évaluation des compétences en base de données',
      type: 'examen',
      statut: 'a-venir',
      matiere: 'Base de données',
      chapitre: 'SQL et modélisation',
      date: '01/03/2024',
      duree: '2h',
      difficulte: 'intermediaire',
      pointsMax: 150,
      pointsObtenus: null,
      note: null,
      seuilReussite: 10,
      tauxReussite: null,
      tentatives: 0,
      tentativesMax: 1,
      estNote: true,
      estObligatoire: true,
      competences: ['SQL', 'Modélisation'],
      ressources: ['Documentation SQL']
    },
    {
      id: 5,
      titre: 'Présentation orale',
      description: 'Présentation des projets réalisés',
      type: 'oral',
      statut: 'a-venir',
      matiere: 'Toutes',
      chapitre: 'Synthèse',
      date: '10/03/2024',
      duree: '30min',
      difficulte: 'intermediaire',
      pointsMax: 50,
      pointsObtenus: null,
      note: null,
      seuilReussite: 10,
      tauxReussite: null,
      tentatives: 0,
      tentativesMax: 1,
      estNote: true,
      estObligatoire: true,
      competences: ['Communication', 'Présentation'],
      ressources: ['Guide de présentation']
    },
    {
      id: 6,
      titre: 'Quiz rapide: Variables',
      description: 'Vérification des connaissances sur les variables',
      type: 'quiz',
      statut: 'terminee',
      matiere: 'Java',
      chapitre: 'Bases de la programmation',
      date: '05/02/2024',
      duree: '15min',
      difficulte: 'facile',
      pointsMax: 50,
      pointsObtenus: 48,
      note: 19.2,
      seuilReussite: 10,
      tauxReussite: 96,
      tentatives: 2,
      tentativesMax: 3,
      estNote: false,
      estObligatoire: false,
      competences: ['Variables', 'Types'],
      ressources: []
    }
  ];

  // Filtres actifs
  filtreTypeActif = 'tous';
  filtreStatutActif = 'tous';
  
  // Évaluation sélectionnée
  evaluationSelectionnee: any = null;
  
  // Mode de vue
  modeVue: 'liste' | 'grille' = 'liste';

  // Statistiques
  stats = {
    total: 0,
    aVenir: 0,
    enCours: 0,
    terminees: 0,
    corrigees: 0,
    moyenneGenerale: 0,
    meilleureNote: 0,
    tauxReussiteGlobal: 0
  };

  constructor() { }

  ngOnInit(): void {
    this.calculerStatistiques();
  }

  // Méthodes utilitaires
  getTypeInfo(typeId: string): any {
    const type = this.typesEvaluations.find(t => t.id === typeId);
    return type || { couleur: '#6c757d', icon: '📋', label: 'Autre' };
  }

  getEvaluationTypeColor(typeId: string): string {
    const type = this.getTypeInfo(typeId);
    return type.couleur + '20';
  }

  getEvaluationTypeIcon(typeId: string): string {
    const type = this.getTypeInfo(typeId);
    return type.icon;
  }

  getEvaluationTypeLabel(typeId: string): string {
    const type = this.getTypeInfo(typeId);
    return type.label;
  }

  formatStatut(statut: string): string {
    const statuts: { [key: string]: string } = {
      'a-venir': 'À venir',
      'en-cours': 'En cours',
      'terminee': 'Terminée',
      'corrigee': 'Corrigée'
    };
    return statuts[statut] || statut;
  }

  formatDifficulte(difficulte: string): string {
    const difficulteMap: { [key: string]: string } = {
      'facile': 'Facile',
      'intermediaire': 'Intermédiaire',
      'avance': 'Avancé'
    };
    return difficulteMap[difficulte] || difficulte;
  }

  getCouleurNote(note: number | null): string {
    if (note === null) return '#6c757d';
    if (note >= 16) return '#28a745';
    if (note >= 12) return '#ffc107';
    return '#dc3545';
  }

  getIconeStatut(statut: string): string {
    const icones: { [key: string]: string } = {
      'a-venir': '📅',
      'en-cours': '⏳',
      'terminee': '✅',
      'corrigee': '📈'
    };
    return icones[statut] || '📋';
  }

  // Calculer les statistiques
  calculerStatistiques(): void {
    const total = this.evaluations.length;
    const aVenir = this.evaluations.filter(e => e.statut === 'a-venir').length;
    const enCours = this.evaluations.filter(e => e.statut === 'en-cours').length;
    const terminees = this.evaluations.filter(e => e.statut === 'terminee').length;
    const corrigees = this.evaluations.filter(e => e.statut === 'corrigee').length;
    
    const notesCorrigees = this.evaluations
      .filter(e => e.note !== null && e.statut === 'corrigee')
      .map(e => e.note || 0);
    
    const moyenneGenerale = notesCorrigees.length > 0 
      ? notesCorrigees.reduce((a, b) => a + b, 0) / notesCorrigees.length 
      : 0;
    
    const meilleureNote = notesCorrigees.length > 0 
      ? Math.max(...notesCorrigees) 
      : 0;
    
    const evaluationsReussies = this.evaluations
      .filter(e => e.note !== null && e.note >= (e.seuilReussite || 10))
      .length;
    
    const tauxReussiteGlobal = total > 0 
      ? Math.round((evaluationsReussies / total) * 100) 
      : 0;
    
    this.stats = {
      total,
      aVenir,
      enCours,
      terminees,
      corrigees,
      moyenneGenerale: Math.round(moyenneGenerale * 10) / 10,
      meilleureNote: Math.round(meilleureNote * 10) / 10,
      tauxReussiteGlobal
    };
  }

  // Obtenir les évaluations filtrées
  get evaluationsFiltrees() {
    return this.evaluations.filter(evaluation => {
      const typeMatch = this.filtreTypeActif === 'tous' || evaluation.type === this.filtreTypeActif;
      const statutMatch = this.filtreStatutActif === 'tous' || evaluation.statut === this.filtreStatutActif;
      return typeMatch && statutMatch;
    });
  }

  // Appliquer un filtre de type
  appliquerFiltreType(typeId: string): void {
    this.filtreTypeActif = typeId;
  }

  // Appliquer un filtre de statut
  appliquerFiltreStatut(statutId: string): void {
    this.filtreStatutActif = statutId;
  }

  // Réinitialiser les filtres
  reinitialiserFiltres(): void {
    this.filtreTypeActif = 'tous';
    this.filtreStatutActif = 'tous';
  }

  // Sélectionner une évaluation
  selectionnerEvaluation(evaluation: any): void {
    this.evaluationSelectionnee = evaluation;
  }

  // Démarrer une évaluation
  demarrerEvaluation(evaluation: any): void {
    if (evaluation.statut === 'a-venir') {
      this.afficherMessage(`⏳ "${evaluation.titre}" commence le ${evaluation.date}`, 'info');
      return;
    }

    if (evaluation.statut === 'corrigee') {
      this.afficherMessage(`📊 Voir les résultats de "${evaluation.titre}"`, 'info');
      return;
    }

    if (evaluation.statut === 'terminee') {
      this.afficherMessage(`⏳ "${evaluation.titre}" est en cours de correction`, 'info');
      return;
    }

    // Démarrer l'évaluation
    evaluation.statut = 'en-cours';
    this.afficherMessage(`🚀 Démarrage de "${evaluation.titre}"`, 'success');
    this.calculerStatistiques();
  }

  // Soumettre une évaluation
  soumettreEvaluation(evaluation: any): void {
    if (evaluation.statut !== 'en-cours') {
      this.afficherMessage('Cette évaluation ne peut pas être soumise', 'warning');
      return;
    }

    evaluation.statut = 'terminee';
    evaluation.tentatives += 1;
    
    this.afficherMessage(`📤 "${evaluation.titre}" soumis avec succès !`, 'success');
    this.calculerStatistiques();
  }

  // Obtenir la classe CSS pour un statut
  getClasseStatut(statut: string): string {
    const classes: { [key: string]: string } = {
      'a-venir': 'statut-a-venir',
      'en-cours': 'statut-en-cours',
      'terminee': 'statut-terminee',
      'corrigee': 'statut-corrigee'
    };
    return classes[statut] || 'statut-defaut';
  }

  // Obtenir le texte du bouton principal
  getTexteBoutonPrincipal(evaluation: any): string {
    switch (evaluation.statut) {
      case 'corrigee': return 'Voir résultats';
      case 'terminee': return 'En attente';
      case 'en-cours': return 'Continuer';
      case 'a-venir': return 'Planifiée';
      default: return 'Commencer';
    }
  }

  // Obtenir la classe CSS du bouton principal
  getClasseBoutonPrincipal(evaluation: any): string {
    switch (evaluation.statut) {
      case 'corrigee': return 'btn-outline-success';
      case 'terminee': return 'btn-outline-info';
      case 'en-cours': return 'btn-primary';
      case 'a-venir': return 'btn-secondary disabled';
      default: return 'btn-primary';
    }
  }

  // Obtenir l'icône du bouton principal
  getIconeBoutonPrincipal(evaluation: any): string {
    switch (evaluation.statut) {
      case 'corrigee': return '📊';
      case 'terminee': return '⏳';
      case 'en-cours': return '▶️';
      case 'a-venir': return '📅';
      default: return '🚀';
    }
  }

  // Obtenir la couleur pour la difficulté
  getCouleurDifficulte(difficulte: string): string {
    const couleurs: { [key: string]: string } = {
      'facile': '#28a745',
      'intermediaire': '#ffc107',
      'avance': '#dc3545'
    };
    return couleurs[difficulte] || '#6c757d';
  }

  // Formater la date
  formaterDate(date: string): string {
    if (!date) return 'Non planifiée';
    return date;
  }

  // Afficher un message
  afficherMessage(message: string, type: 'success' | 'error' | 'warning' | 'info' = 'info'): void {
    console.log(`${type.toUpperCase()}: ${message}`);
    alert(message);
  }

  // Toggle mode de vue
  toggleModeVue(): void {
    this.modeVue = this.modeVue === 'liste' ? 'grille' : 'liste';
  }

  // Obtenir le pourcentage de réussite
  getPourcentageReussite(evaluation: any): number {
    if (!evaluation.note || !evaluation.pointsMax) return 0;
    return Math.round((evaluation.note / 20) * 100);
  }
}