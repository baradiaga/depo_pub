// src/app/services/activite-etudiant.service.ts
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ActiviteEtudiant {
  id: number;
  titre: string;
  description: string;
  route: string;  // Route backend (ex: /api/elements-constitutifs/...)
  navigationRoute: string; // Route frontend (ex: /app/curriculum/...)
  icon: string;
  categorie: 'Matières' | 'Parcours' | 'Progression' | 'Ressources' | 'Évaluation' | 'Diagnostic' | 'Tableau de bord';
  methode: 'GET' | 'POST';
  params?: any;
  couleur: string;
  badge?: string;
  estDisponible: boolean;
  besoinSelection?: 'matiere' | 'ec' | 'chapitre' | 'fichier';
  serviceAssocie?: 'ElementConstitutif' | 'Chapitre' | 'Test' | 'Ressource' | 'Progression';
}

@Injectable({
  providedIn: 'root'
})
export class ActiviteEtudiantService {
  
  // Utilisation de environment.apiUrl (ex: http://localhost:8080/api)
  private readonly baseUrl = environment.apiUrl;

  private activites: ActiviteEtudiant[] = [
    // =================== 📚 MATIÈRES ===================
    {
      id: 1,
      titre: 'Mes matières',
      description: 'Consultez la liste de toutes vos matières inscrites',
      route: '/api/elements-constitutifs/mes-matieres',
      navigationRoute: '/app/curriculum/matieres',
      icon: 'book',
      categorie: 'Matières',
      methode: 'GET',
      couleur: '#4361ee',
      estDisponible: true,
      serviceAssocie: 'ElementConstitutif'
    },
    {
      id: 2,
      titre: 'Détails des matières',
      description: 'Explorez le détail de tous les éléments constitutifs',
      route: '/api/elements-constitutifs/details',
      navigationRoute: '/elements-constitutifs',
      icon: 'info',
      categorie: 'Matières',
      methode: 'GET',
      couleur: '#3a0ca3',
      estDisponible: true,
      serviceAssocie: 'ElementConstitutif'
    },
    {
      id: 3,
      titre: 'Chapitres d\'un EC',
      description: 'Accédez aux chapitres d\'un élément constitutif',
      route: '/api/elements-constitutifs/{ecId}/chapitres',
      navigationRoute: '/chapitres/liste',
      icon: 'list',
      categorie: 'Matières',
      methode: 'GET',
      couleur: '#7209b7',
      badge: 'Sélection requise',
      besoinSelection: 'ec',
      estDisponible: true,
      serviceAssocie: 'Chapitre'
    },
    {
      id: 4,
      titre: 'Contenu d\'une matière',
      description: 'Parcourez l\'arborescence complète chapitres/sections',
      route: '/api/matieres/{matiereId}/contenu',
      navigationRoute: '/matieres/contenu',
      icon: 'folder_tree',
      categorie: 'Matières',
      methode: 'GET',
      couleur: '#f72585',
      badge: 'Sélection requise',
      besoinSelection: 'matiere',
      estDisponible: true,
      serviceAssocie: 'Chapitre'
    },

    // =================== 🗺️ PARCOURS ===================
    {
      id: 5,
      titre: 'Mon parcours',
      description: 'Visualisez votre parcours recommandé et vos choix',
      route: '/api/parcours/etudiant/me',
      navigationRoute: '/parcours',
      icon: 'map',
      categorie: 'Parcours',
      methode: 'GET',
      couleur: '#4cc9f0',
      estDisponible: true
    },
    {
      id: 6,
      titre: 'Choisir mon parcours',
      description: 'Sélectionnez les chapitres pour votre apprentissage',
      route: '/api/parcours/etudiant',
      navigationRoute: '/parcours/choisir',
      icon: 'checklist',
      categorie: 'Parcours',
      methode: 'POST',
      couleur: '#4895ef',
      estDisponible: true
    },

    // =================== 📊 PROGRESSION ===================
    {
      id: 7,
      titre: 'Mes matières avec statut',
      description: 'Suivez votre progression globale dans les matières',
      route: '/api/progression/mes-matieres',
      navigationRoute: '/progression',
      icon: 'trending_up',
      categorie: 'Progression',
      methode: 'GET',
      couleur: '#38b000',
      estDisponible: true,
      serviceAssocie: 'Progression'
    },
    {
      id: 8,
      titre: 'Progression détaillée',
      description: 'Analysez votre avancement chapitre par chapitre',
      route: '/api/progression/mes-chapitres',
      navigationRoute: '/progression/chapitres',
      icon: 'bar_chart',
      categorie: 'Progression',
      methode: 'GET',
      couleur: '#2d6a4f',
      estDisponible: true,
      serviceAssocie: 'Progression'
    },

    // =================== 📁 RESSOURCES ===================
    {
      id: 9,
      titre: 'Ressources pédagogiques',
      description: 'Accédez à toutes les ressources disponibles',
      route: '/api/ressources',
      navigationRoute: '/ressources',
      icon: 'description',
      categorie: 'Ressources',
      methode: 'GET',
      couleur: '#ff9e00',
      estDisponible: true,
      serviceAssocie: 'Ressource'
    },
    {
      id: 10,
      titre: 'Télécharger une ressource',
      description: 'Téléchargez documents et fichiers de cours',
      route: '/api/ressources/telecharger/{nomFichierStocke}',
      navigationRoute: '/ressources/telecharger',
      icon: 'download',
      categorie: 'Ressources',
      methode: 'GET',
      couleur: '#ff9100',
      badge: 'Fichier requis',
      besoinSelection: 'fichier',
      estDisponible: true,
      serviceAssocie: 'Ressource'
    },

    // =================== 🧪 ÉVALUATION ===================
    {
      id: 11,
      titre: 'Questions d\'un test',
      description: 'Obtenez les questions d\'un test par chapitre',
      route: '/api/tests/chapitre/{chapitreId}/questions',
      navigationRoute: '/tests/questions',
      icon: 'quiz',
      categorie: 'Évaluation',
      methode: 'GET',
      couleur: '#ef233c',
      badge: 'Sélection requise',
      besoinSelection: 'chapitre',
      estDisponible: true,
      serviceAssocie: 'Test'
    },
    {
      id: 12,
      titre: 'Soumettre un test',
      description: 'Envoyez vos réponses et obtenez votre score',
      route: '/api/tests/chapitre/{chapitreId}/soumettre',
      navigationRoute: '/tests/soumettre',
      icon: 'send',
      categorie: 'Évaluation',
      methode: 'POST',
      couleur: '#d90429',
      badge: 'Sélection requise',
      besoinSelection: 'chapitre',
      estDisponible: true,
      serviceAssocie: 'Test'
    },
    {
      id: 13,
      titre: 'Historique des tests',
      description: 'Consultez tous vos résultats passés',
      route: '/api/tests/mon-historique',
      navigationRoute: '/tests/historique',
      icon: 'history',
      categorie: 'Évaluation',
      methode: 'GET',
      couleur: '#9d0208',
      estDisponible: true,
      serviceAssocie: 'Test'
    },

    // =================== 🔍 DIAGNOSTIC ===================
    {
      id: 14,
      titre: 'Test diagnostic',
      description: 'Évaluez votre niveau dans une matière',
      route: '/api/diagnostic/generer-test/{matiereId}',
      navigationRoute: '/diagnostic',
      icon: 'psychology',
      categorie: 'Diagnostic',
      methode: 'GET',
      couleur: '#7b2cbf',
      badge: 'À implémenter',
      besoinSelection: 'matiere',
      estDisponible: false
    },

    // =================== 📊 TABLEAU DE BORD ===================
    {
      id: 15,
      titre: 'Mon tableau de bord',
      description: 'Vue d\'ensemble complète de votre apprentissage',
      route: '/api/progression/mes-matieres',
      navigationRoute: '/dashboard',
      icon: 'dashboard',
      categorie: 'Tableau de bord',
      methode: 'GET',
      couleur: '#00b4d8',
      estDisponible: true
    }
  ];

  constructor() { }

  /**
   * Retourne la liste de toutes les activités.
   */
  getActivites(): Observable<ActiviteEtudiant[]> {
    return of(this.activites);
  }

  /**
   * Retourne l'URL API complète d'une activité.
   * Remplace /api par l'URL de l'environnement pour la production 2026.
   */
  getFullApiUrl(activite: ActiviteEtudiant): string {
    // Si la route commence par /api, on remplace par le baseUrl de l'environnement
    const internalPath = activite.route.startsWith('/api') 
                         ? activite.route.substring(4) 
                         : activite.route;
    return `${this.baseUrl}${internalPath}`;
  }

  /**
   * Retourne les activités filtrées par catégorie.
   */
  getActivitesByCategorie(categorie: string): Observable<ActiviteEtudiant[]> {
    return of(this.activites.filter(a => a.categorie === categorie));
  }
}
