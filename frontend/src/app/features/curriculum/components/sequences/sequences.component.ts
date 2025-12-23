import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-sequences',
  templateUrl: './sequences.component.html',
  styleUrls: ['./sequences.component.css']
})
export class SequencesComponent implements OnInit {
  
  // Données hiérarchisées : Matières -> Chapitres -> Séquences
  matieres = [
    {
      id: 1,
      nom: 'Java',
      icon: '☕',
      couleur: '#dc3545',
      progression: 45,
      chapitres: [
        {
          id: 1,
          titre: 'Bases de la programmation Java',
          description: 'Introduction aux concepts fondamentaux',
          progression: 80,
          sequences: [
            {
              id: 1,
              titre: 'Introduction à la programmation',
              description: 'Les bases de la programmation avec exemples',
              duree: '2h',
              progression: 60,
              statut: 'en cours',
              points: 100,
              pointsAcquis: 75,
              icon: '🚀',
              badgeColor: '#1c5980',
              estVerrouillee: false,
              ressources: ['vidéo', 'pdf', 'quiz']
            },
            {
              id: 2,
              titre: 'Variables et types de données',
              description: 'Manipulez variables et types de données',
              duree: '1h30',
              progression: 100,
              statut: 'terminée',
              points: 80,
              pointsAcquis: 80,
              icon: '🔢',
              badgeColor: '#28a745',
              estVerrouillee: false,
              ressources: ['vidéo', 'exercice']
            },
            {
              id: 3,
              titre: 'Structures conditionnelles',
              description: 'Maîtrisez if/else et switch',
              duree: '3h',
              progression: 25,
              statut: 'en cours',
              points: 120,
              pointsAcquis: 30,
              icon: '⚖️',
              badgeColor: '#ffc107',
              estVerrouillee: false,
              ressources: ['vidéo', 'projet']
            }
          ]
        },
        {
          id: 2,
          titre: 'Programmation avancée',
          description: 'Concepts avancés et bonnes pratiques',
          progression: 0,
          sequences: [
            {
              id: 4,
              titre: 'Boucles et itérations',
              description: 'Boucles for, while et do-while',
              duree: '2h30',
              progression: 0,
              statut: 'verrouillée',
              points: 150,
              pointsAcquis: 0,
              icon: '🔄',
              badgeColor: '#6c757d',
              estVerrouillee: true,
              ressources: ['vidéo', 'exercice', 'projet']
            },
            {
              id: 5,
              titre: 'Fonctions et méthodes',
              description: 'Fonctions réutilisables',
              duree: '3h20',
              progression: 0,
              statut: 'verrouillée',
              points: 140,
              pointsAcquis: 0,
              icon: '⚙️',
              badgeColor: '#6c757d',
              estVerrouillee: true,
              ressources: ['vidéo', 'quiz', 'projet']
            }
          ]
        }
      ]
    },
    {
      id: 2,
      nom: 'Algorithme',
      icon: '🧮',
      couleur: '#17a2b8',
      progression: 0,
      chapitres: [
        {
          id: 3,
          titre: 'Algorithmes fondamentaux',
          description: 'Structures de données et algorithmes de base',
          progression: 0,
          sequences: [
            {
              id: 6,
              titre: 'Introduction aux algorithmes',
              description: 'Algorithmes fondamentaux',
              duree: '4h',
              progression: 0,
              statut: 'verrouillée',
              points: 200,
              pointsAcquis: 0,
              icon: '🧠',
              badgeColor: '#6c757d',
              estVerrouillee: true,
              ressources: ['vidéo', 'pdf', 'exercice']
            },
            {
              id: 7,
              titre: 'Complexité algorithmique',
              description: 'Notation O et analyse de performance',
              duree: '3h',
              progression: 0,
              statut: 'verrouillée',
              points: 180,
              pointsAcquis: 0,
              icon: '📈',
              badgeColor: '#6c757d',
              estVerrouillee: true,
              ressources: ['vidéo', 'quiz']
            }
          ]
        }
      ]
    },
    {
      id: 3,
      nom: 'Base de données',
      icon: '🗄️',
      couleur: '#28a745',
      progression: 0,
      chapitres: [
        {
          id: 4,
          titre: 'SQL et modélisation',
          description: 'Concepts de base de données relationnelles',
          progression: 0,
          sequences: [
            {
              id: 8,
              titre: 'Introduction à SQL',
              description: 'Requêtes SELECT, INSERT, UPDATE',
              duree: '3h',
              progression: 0,
              statut: 'à venir',
              points: 150,
              pointsAcquis: 0,
              icon: '📋',
              badgeColor: '#6c757d',
              estVerrouillee: true,
              ressources: ['vidéo', 'exercice']
            }
          ]
        }
      ]
    }
  ];

  // État actuel
  matiereSelectionnee: any = null;
  chapitreSelectionne: any = null;
  sequenceSelectionnee: any = null;
  vueActive: 'matieres' | 'chapitres' | 'sequence' = 'matieres';

  // Statistiques globales
  stats = {
    matieresTotal: 0,
    chapitresTotal: 0,
    sequencesTotal: 0,
    progressionGlobale: 0,
    sequencesTerminees: 0,
    sequencesEnCours: 0,
    pointsAcquis: 0,
    pointsTotaux: 0
  };

  constructor() { }

  ngOnInit(): void {
    this.calculerStatistiques();
  }

  // === NOUVELLES MÉTHODES POUR CORRIGER LES ERREURS ===
  
  // 1. Exposer Math.PI pour le template
  get PI(): number {
    return Math.PI;
  }

  // 2. Calculer la circonférence
  getCircumference(radius: number): number {
    return 2 * Math.PI * radius;
  }

  // 3. Calculer le dashoffset pour les cercles SVG
  getDashOffset(radius: number, progress: number): number {
    return this.getCircumference(radius) * (1 - progress / 100);
  }

  // 4. Obtenir le nombre de séquences terminées dans un chapitre
  getSequencesTermineesCount(chapitre: any): number {
    if (!chapitre || !chapitre.sequences) return 0;
    return chapitre.sequences.filter((s: any) => s.statut === 'terminée').length;
  }

  // === FIN DES NOUVELLES MÉTHODES ===

  // Calculer les statistiques globales
  calculerStatistiques(): void {
    let sequencesTotal = 0;
    let sequencesTerminees = 0;
    let sequencesEnCours = 0;
    let pointsAcquis = 0;
    let pointsTotaux = 0;

    this.matieres.forEach(matiere => {
      matiere.chapitres.forEach(chapitre => {
        chapitre.sequences.forEach(sequence => {
          sequencesTotal++;
          pointsTotaux += sequence.points;
          pointsAcquis += sequence.pointsAcquis;
          
          if (sequence.statut === 'terminée') {
            sequencesTerminees++;
          } else if (sequence.statut === 'en cours') {
            sequencesEnCours++;
          }
        });
      });
    });

    this.stats = {
      matieresTotal: this.matieres.length,
      chapitresTotal: this.matieres.reduce((acc, m) => acc + m.chapitres.length, 0),
      sequencesTotal: sequencesTotal,
      progressionGlobale: sequencesTotal > 0 ? Math.round((sequencesTerminees / sequencesTotal) * 100) : 0,
      sequencesTerminees: sequencesTerminees,
      sequencesEnCours: sequencesEnCours,
      pointsAcquis: pointsAcquis,
      pointsTotaux: pointsTotaux
    };
  }

  // Sélectionner une matière et afficher ses chapitres
  selectionnerMatiere(matiere: any): void {
    this.matiereSelectionnee = matiere;
    this.vueActive = 'chapitres';
    this.chapitreSelectionne = null;
    this.sequenceSelectionnee = null;
  }

  // Sélectionner un chapitre et afficher ses séquences
  selectionnerChapitre(chapitre: any): void {
    this.chapitreSelectionne = chapitre;
    this.vueActive = 'sequence';
  }

  // Sélectionner une séquence pour voir le détail
  selectionnerSequence(sequence: any): void {
    if (sequence.estVerrouillee) {
      this.afficherMessage(`🔒 Cette séquence est verrouillée. Terminez les prérequis d'abord !`, 'warning');
      return;
    }
    this.sequenceSelectionnee = sequence;
  }

  // Démarrer/Continuer une séquence
  demarrerSequence(sequence: any): void {
    if (sequence.estVerrouillee) {
      this.afficherMessage('🔒 Cette séquence est verrouillée', 'warning');
      return;
    }

    if (sequence.statut === 'terminée') {
      this.afficherMessage(`🎯 Révision de "${sequence.titre}"`, 'info');
      // Ici: navigation vers le contenu de la séquence
    } else if (sequence.statut === 'en cours') {
      this.afficherMessage(`▶️ Reprise de "${sequence.titre}"`, 'info');
      // Ici: navigation vers le contenu de la séquence
    } else {
      sequence.statut = 'en cours';
      sequence.progression = 5; // Démarrage à 5%
      this.afficherMessage(`🚀 Démarrage de "${sequence.titre}"`, 'success');
      this.calculerStatistiques(); // Recalculer les stats
    }
  }

  // Revenir à la liste des matières
  revenirMatieres(): void {
    this.vueActive = 'matieres';
    this.matiereSelectionnee = null;
    this.chapitreSelectionne = null;
    this.sequenceSelectionnee = null;
  }

  // Revenir aux chapitres de la matière
  revenirChapitres(): void {
    this.vueActive = 'chapitres';
    this.chapitreSelectionne = null;
    this.sequenceSelectionnee = null;
  }

  // Calculer la progression d'un chapitre
  calculerProgressionChapitre(chapitre: any): number {
    if (!chapitre.sequences.length) return 0;
    const total = chapitre.sequences.reduce((acc: number, s: any) => acc + s.progression, 0);
    return Math.round(total / chapitre.sequences.length);
  }

  // Calculer la progression d'une matière
  calculerProgressionMatiere(matiere: any): number {
    if (!matiere.chapitres.length) return 0;
    const total = matiere.chapitres.reduce((acc: number, c: any) => acc + this.calculerProgressionChapitre(c), 0);
    return Math.round(total / matiere.chapitres.length);
  }

  // Obtenir la couleur en fonction de la progression
  getCouleurProgression(progression: number): string {
    if (progression >= 80) return '#28a745'; // Vert
    if (progression >= 50) return '#1c5980'; // Bleu
    if (progression >= 25) return '#ffc107'; // Jaune
    return '#dc3545'; // Rouge
  }

  // Obtenir la classe CSS pour un statut
  getClasseStatut(statut: string): string {
    const classes: { [key: string]: string } = {
      'terminée': 'statut-terminee',
      'en cours': 'statut-encours',
      'verrouillée': 'statut-verrouillee',
      'à venir': 'statut-avenir'
    };
    return classes[statut] || 'statut-defaut';
  }

  // Obtenir le texte du bouton d'action
  getTexteAction(sequence: any): string {
    switch (sequence.statut) {
      case 'terminée': return 'Réviser';
      case 'en cours': return 'Continuer';
      case 'verrouillée': return 'Verrouillé';
      default: return 'Commencer';
    }
  }

  // Obtenir la classe CSS du bouton d'action
  getClasseBoutonAction(sequence: any): string {
    switch (sequence.statut) {
      case 'terminée': return 'btn-outline-success';
      case 'en cours': return 'btn-primary';
      case 'verrouillée': return 'btn-secondary disabled';
      default: return 'btn-primary';
    }
  }

  // Obtenir l'icône du bouton d'action
  getIconeAction(sequence: any): string {
    switch (sequence.statut) {
      case 'terminée': return '🔄';
      case 'en cours': return '▶️';
      case 'verrouillée': return '🔒';
      default: return '🚀';
    }
  }

  // Afficher un message à l'utilisateur
  afficherMessage(message: string, type: 'success' | 'error' | 'warning' | 'info' = 'info'): void {
    console.log(`${type.toUpperCase()}: ${message}`);
    
    // Version simple pour démo
    const alertClass = {
      'success': 'alert-success',
      'error': 'alert-danger',
      'warning': 'alert-warning',
      'info': 'alert-info'
    }[type];
    
    // Créer un toast simple
    const toast = document.createElement('div');
    toast.className = `alert ${alertClass} alert-dismissible fade show position-fixed`;
    toast.style.cssText = 'top: 20px; right: 20px; z-index: 1050;';
    toast.innerHTML = `
      ${message}
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    document.body.appendChild(toast);
    
    // Auto-supprimer après 3 secondes
    setTimeout(() => {
      if (toast.parentNode) {
        toast.parentNode.removeChild(toast);
      }
    }, 3000);
  }

  // Obtenir les séquences d'une matière
  getSequencesDeMatiere(matiereId: number): any[] {
    const matiere = this.matieres.find(m => m.id === matiereId);
    if (!matiere) return [];
    
    return matiere.chapitres.flatMap(chapitre => chapitre.sequences);
  }
}