import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-activites',
  templateUrl: './activites.component.html',
  styleUrls: ['./activites.component.css']
})
export class ActivitesComponent implements OnInit {

  // Types d'activités
  typesActivites = [
    { id: 'tous', label: 'Toutes', icon: '🌐', couleur: '#6c757d' },
    { id: 'exercice', label: 'Exercices', icon: '💻', couleur: '#4361ee' },
    { id: 'quiz', label: 'Quiz', icon: '📝', couleur: '#4cc9f0' },
    { id: 'projet', label: 'Projets', icon: '🚀', couleur: '#f72585' },
    { id: 'collaboratif', label: 'Collaboratif', icon: '👥', couleur: '#f8961e' }
  ];

  // Filtres par statut
  filtresStatut = [
    { id: 'tous', label: 'Toutes', icon: '📋' },
    { id: 'a-faire', label: 'À faire', icon: '⏳' },
    { id: 'en-cours', label: 'En cours', icon: '▶️' },
    { id: 'soumis', label: 'Soumis', icon: '📤' },
    { id: 'corrige', label: 'Corrigé', icon: '✅' }
  ];

  // Données des activités
  activites = [
    {
      id: 1,
      titre: 'Créer une calculatrice simple',
      description: 'Implémentez une calculatrice avec les opérations de base',
      type: 'exercice',
      statut: 'en-cours',
      matiere: 'Java',
      chapitre: 'Bases de la programmation',
      sequence: 'Variables et types de données',
      dateLimite: '20/02/2024',
      dateSoumission: null,
      dureeEstimee: '2h',
      difficulte: 'intermediaire',
      points: 100,
      pointsObtenus: null,
      progression: 60,
      ressources: ['Énoncé PDF', 'Code de départ', 'Tests unitaires'],
      estVerrouillee: false,
      tentativeActuelle: 1,
      tentativesMax: 3,
      estNote: true,
      format: 'code'
    },
    {
      id: 2,
      titre: 'Quiz sur les boucles',
      description: 'Questions à choix multiples sur les structures de répétition',
      type: 'quiz',
      statut: 'a-faire',
      matiere: 'Java',
      chapitre: 'Boucles et itérations',
      sequence: 'Boucles For',
      dateLimite: '25/02/2024',
      dateSoumission: null,
      dureeEstimee: '30min',
      difficulte: 'facile',
      points: 50,
      pointsObtenus: null,
      progression: 0,
      ressources: ['Ressources de révision'],
      estVerrouillee: false,
      tentativeActuelle: 0,
      tentativesMax: 2,
      estNote: false,
      format: 'quiz'
    },
    {
      id: 3,
      titre: 'Projet: Gestion de bibliothèque',
      description: 'Système complet de gestion de bibliothèque avec interface',
      type: 'projet',
      statut: 'soumis',
      matiere: 'Java',
      chapitre: 'Programmation avancée',
      sequence: 'Fonctions et méthodes',
      dateLimite: '10/02/2024',
      dateSoumission: '09/02/2024',
      dureeEstimee: '10h',
      difficulte: 'avance',
      points: 200,
      pointsObtenus: 185,
      progression: 100,
      ressources: ['Spécifications', 'Maquettes', 'Base de données'],
      estVerrouillee: false,
      tentativeActuelle: 1,
      tentativesMax: 1,
      estNote: true,
      format: 'projet'
    },
    {
      id: 4,
      titre: 'Discussion: Meilleures pratiques',
      description: 'Forum de discussion sur les design patterns',
      type: 'collaboratif',
      statut: 'corrige',
      matiere: 'Algorithme',
      chapitre: 'Algorithmes fondamentaux',
      sequence: 'Complexité algorithmique',
      dateLimite: '15/02/2024',
      dateSoumission: '14/02/2024',
      dureeEstimee: '1h',
      difficulte: 'intermediaire',
      points: 30,
      pointsObtenus: 28,
      progression: 100,
      ressources: ['Articles de référence'],
      estVerrouillee: false,
      tentativeActuelle: 1,
      tentativesMax: 1,
      estNote: false,
      format: 'discussion'
    },
    {
      id: 5,
      titre: 'Exercice: Tris et recherches',
      description: 'Implémentation des algorithmes de tri classiques',
      type: 'exercice',
      statut: 'verrouillee',
      matiere: 'Algorithme',
      chapitre: 'Algorithmes fondamentaux',
      sequence: 'Introduction aux algorithmes',
      dateLimite: 'À venir',
      dateSoumission: null,
      dureeEstimee: '4h',
      difficulte: 'avance',
      points: 150,
      pointsObtenus: null,
      progression: 0,
      ressources: ['Pseudocode', 'Benchmarks'],
      estVerrouillee: true,
      tentativeActuelle: 0,
      tentativesMax: 2,
      estNote: true,
      format: 'code'
    }
  ];

  // Filtres actifs
  filtreTypeActif = 'tous';
  filtreStatutActif = 'tous';
  
  // Activité sélectionnée
  activiteSelectionnee: any = null;
  
  // Mode de vue
  modeVue: 'liste' | 'calendrier' = 'liste';

  // Statistiques
  stats = {
    total: 0,
    aFaire: 0,
    enCours: 0,
    soumis: 0,
    corrige: 0,
    pointsTotal: 0,
    pointsObtenus: 0,
    tauxCompletion: 0
  };

  constructor() { }

  ngOnInit(): void {
    this.calculerStatistiques();
  }

  // === NOUVELLES MÉTHODES POUR CORRIGER LES ERREURS ===
  
  getTypeInfo(typeId: string): any {
    const type = this.typesActivites.find(t => t.id === typeId);
    return type || { couleur: '#6c757d', icon: '📋', label: 'Autre' };
  }

  getActiviteTypeColor(typeId: string): string {
    const type = this.getTypeInfo(typeId);
    return type.couleur + '20'; // 20 pour l'opacité
  }

  getActiviteTypeIcon(typeId: string): string {
    const type = this.getTypeInfo(typeId);
    return type.icon;
  }

  getActiviteTypeLabel(typeId: string): string {
    const type = this.getTypeInfo(typeId);
    return type.label;
  }

  formatStatut(statut: string): string {
    const statuts: { [key: string]: string } = {
      'a-faire': 'À faire',
      'en-cours': 'En cours',
      'soumis': 'Soumis',
      'corrige': 'Corrigé',
      'verrouillee': 'Verrouillé'
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

  // === FIN DES NOUVELLES MÉTHODES ===

  // Calculer les statistiques
  calculerStatistiques(): void {
    const total = this.activites.length;
    const aFaire = this.activites.filter(a => a.statut === 'a-faire').length;
    const enCours = this.activites.filter(a => a.statut === 'en-cours').length;
    const soumis = this.activites.filter(a => a.statut === 'soumis').length;
    const corrige = this.activites.filter(a => a.statut === 'corrige').length;
    
    const pointsTotal = this.activites.reduce((acc, a) => acc + a.points, 0);
    const pointsObtenus = this.activites
      .filter(a => a.pointsObtenus)
      .reduce((acc, a) => acc + (a.pointsObtenus || 0), 0);
    
    this.stats = {
      total,
      aFaire,
      enCours,
      soumis,
      corrige,
      pointsTotal,
      pointsObtenus,
      tauxCompletion: total > 0 ? Math.round((corrige / total) * 100) : 0
    };
  }

  // Obtenir les activités filtrées
  get activitesFiltrees() {
    return this.activites.filter(activite => {
      const typeMatch = this.filtreTypeActif === 'tous' || activite.type === this.filtreTypeActif;
      const statutMatch = this.filtreStatutActif === 'tous' || activite.statut === this.filtreStatutActif;
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

  // Sélectionner une activité
  selectionnerActivite(activite: any): void {
    this.activiteSelectionnee = activite;
  }

  // Démarrer une activité
  demarrerActivite(activite: any): void {
    if (activite.estVerrouillee) {
      this.afficherMessage(`🔒 Cette activité est verrouillée. Terminez les prérequis d'abord !`, 'warning');
      return;
    }

    if (activite.statut === 'corrige') {
      this.afficherMessage(`📊 Voir la correction de "${activite.titre}"`, 'info');
      return;
    }

    if (activite.statut === 'soumis') {
      this.afficherMessage(`⏳ "${activite.titre}" est en attente de correction`, 'info');
      return;
    }

    if (activite.statut === 'en-cours') {
      this.afficherMessage(`▶️ Reprise de "${activite.titre}"`, 'info');
      return;
    }

    // Sinon, démarrer l'activité
    activite.statut = 'en-cours';
    activite.progression = 10; // Démarrage à 10%
    this.afficherMessage(`🚀 Démarrage de "${activite.titre}"`, 'success');
    this.calculerStatistiques();
  }

  // Soumettre une activité
  soumettreActivite(activite: any): void {
    if (activite.statut !== 'en-cours') {
      this.afficherMessage('Cette activité ne peut pas être soumise', 'warning');
      return;
    }

    activite.statut = 'soumis';
    activite.progression = 100;
    activite.dateSoumission = new Date().toLocaleDateString('fr-FR');
    activite.tentativeActuelle += 1;
    
    this.afficherMessage(`📤 "${activite.titre}" soumis avec succès !`, 'success');
    this.calculerStatistiques();
  }

  // Obtenir la classe CSS pour un statut
  getClasseStatut(statut: string): string {
    const classes: { [key: string]: string } = {
      'a-faire': 'statut-a-faire',
      'en-cours': 'statut-en-cours',
      'soumis': 'statut-soumis',
      'corrige': 'statut-corrige',
      'verrouillee': 'statut-verrouillee'
    };
    return classes[statut] || 'statut-defaut';
  }

  // Obtenir l'icône pour un statut
  getIconeStatut(statut: string): string {
    const icones: { [key: string]: string } = {
      'a-faire': '⏳',
      'en-cours': '▶️',
      'soumis': '📤',
      'corrige': '✅',
      'verrouillee': '🔒'
    };
    return icones[statut] || '📋';
  }

  // Obtenir le texte du bouton principal
  getTexteBoutonPrincipal(activite: any): string {
    switch (activite.statut) {
      case 'corrige': return 'Voir correction';
      case 'soumis': return 'Voir soumission';
      case 'en-cours': return 'Continuer';
      case 'verrouillee': return 'Verrouillé';
      default: return 'Commencer';
    }
  }

  // Obtenir la classe CSS du bouton principal
  getClasseBoutonPrincipal(activite: any): string {
    switch (activite.statut) {
      case 'corrige': return 'btn-outline-success';
      case 'soumis': return 'btn-outline-info';
      case 'en-cours': return 'btn-primary';
      case 'verrouillee': return 'btn-secondary disabled';
      default: return 'btn-primary';
    }
  }

  // Obtenir l'icône du bouton principal
  getIconeBoutonPrincipal(activite: any): string {
    switch (activite.statut) {
      case 'corrige': return '📊';
      case 'soumis': return '👁️';
      case 'en-cours': return '▶️';
      case 'verrouillee': return '🔒';
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
    if (!date || date === 'À venir') return date;
    return date;
  }

  // Afficher un message
  afficherMessage(message: string, type: 'success' | 'error' | 'warning' | 'info' = 'info'): void {
    console.log(`${type.toUpperCase()}: ${message}`);
    alert(message);
  }

  // Toggle mode de vue
  toggleModeVue(): void {
    this.modeVue = this.modeVue === 'liste' ? 'calendrier' : 'liste';
  }
}