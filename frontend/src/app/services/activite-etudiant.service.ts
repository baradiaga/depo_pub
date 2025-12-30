// src/app/services/activite-etudiant.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

export interface ActiviteEtudiant {
  id: number;
  titre: string;
  description: string;
  route: string;  // Route backend (API)
  navigationRoute: string; // Route frontend (Angular)
  icon: string;
  categorie: 'Matières' | 'Parcours' | 'Progression' | 'Ressources' | 'Évaluation' | 'Diagnostic' | 'Tableau de bord';
  methode: 'GET' | 'POST';
  params?: any;
  couleur: string;
  badge?: string;
  estDisponible: boolean;
  // NOUVEAU : Indique si l'activité nécessite une sélection préalable
  besoinSelection?: 'matiere' | 'ec' | 'chapitre' | 'fichier';
  // NOUVEAU : Service associé pour récupérer les données
  serviceAssocie?: 'ElementConstitutif' | 'Chapitre' | 'Test' | 'Ressource' | 'Progression';
}

@Injectable({
  providedIn: 'root'
})
export class ActiviteEtudiantService {
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
      navigationRoute: '/chapitres/liste', // Nouvelle route pour choisir un EC
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
      estDisponible: false // Non analysé, donc marqué comme non disponible
    },
    {
      id: 15,
      titre: 'Corriger un diagnostic',
      description: 'Soumettez et corrigez votre test diagnostic',
      route: '/api/diagnostic/corriger-test',
      navigationRoute: '/diagnostic/correction',
      icon: 'grading',
      categorie: 'Diagnostic',
      methode: 'POST',
      couleur: '#5a189a',
      badge: 'À implémenter',
      estDisponible: false // Non analysé, donc marqué comme non disponible
    },

    // =================== 📊 TABLEAU DE BORD ===================
    {
      id: 16,
      titre: 'Mon tableau de bord',
      description: 'Vue d\'ensemble complète de votre apprentissage',
      route: '/api/progression/mes-matieres', // Alternative : utiliser la progression
      navigationRoute: '/dashboard',
      icon: 'dashboard',
      categorie: 'Tableau de bord',
      methode: 'GET',
      couleur: '#023e8a',
      estDisponible: true,
      serviceAssocie: 'Progression'
    },

    // =================== 📖 NOUVELLES ACTIVITÉS ===================
    {
      id: 17,
      titre: 'Contenu complet d\'un chapitre',
      description: 'Consultez le contenu détaillé avec sections',
      route: '/api/chapitres/{chapitreId}/details-complets',
      navigationRoute: '/chapitres/contenu',
      icon: 'article',
      categorie: 'Matières',
      methode: 'GET',
      couleur: '#9d4edd',
      badge: 'Sélection requise',
      besoinSelection: 'chapitre',
      estDisponible: true,
      serviceAssocie: 'Chapitre'
    }
  ];

  constructor(private http: HttpClient) {}

  // =================== MÉTHODES EXISTANTES ===================
  getActivites(): Observable<ActiviteEtudiant[]> {
    return of(this.activites);
  }

  getCategories(): Observable<string[]> {
    const categories = [...new Set(this.activites.map(a => a.categorie))];
    return of(categories);
  }

  getActivitesParCategorie(): Observable<Map<string, ActiviteEtudiant[]>> {
    const map = new Map<string, ActiviteEtudiant[]>();
    this.activites.forEach(activite => {
      if (!map.has(activite.categorie)) {
        map.set(activite.categorie, []);
      }
      map.get(activite.categorie)!.push(activite);
    });
    return of(map);
  }

  getActiviteById(id: number): Observable<ActiviteEtudiant | undefined> {
    return of(this.activites.find(a => a.id === id));
  }

  getStatistiques(): Observable<any> {
    const total = this.activites.length;
    const disponibles = this.activites.filter(a => a.estDisponible).length;
    const getCount = this.activites.filter(a => a.methode === 'GET').length;
    const postCount = this.activites.filter(a => a.methode === 'POST').length;
    const categoriesCount = new Set(this.activites.map(a => a.categorie)).size;

    return of({
      total,
      disponibles,
      getCount,
      postCount,
      categoriesCount,
      pourcentageDisponible: Math.round((disponibles / total) * 100),
      pourcentageGet: Math.round((getCount / total) * 100),
      pourcentagePost: Math.round((postCount / total) * 100)
    });
  }

  rechercherActivites(terme: string): Observable<ActiviteEtudiant[]> {
    terme = terme.toLowerCase();
    const resultats = this.activites.filter(activite =>
      activite.titre.toLowerCase().includes(terme) ||
      activite.description.toLowerCase().includes(terme) ||
      activite.categorie.toLowerCase().includes(terme)
    );
    return of(resultats);
  }

  // =================== NOUVELLES MÉTHODES ===================
  
  /**
   * Filtre les activités par disponibilité
   */
  getActivitesDisponibles(): Observable<ActiviteEtudiant[]> {
    return of(this.activites.filter(a => a.estDisponible));
  }

  /**
   * Récupère les activités nécessitant une sélection
   */
  getActivitesAvecSelection(): Observable<ActiviteEtudiant[]> {
    return of(this.activites.filter(a => a.besoinSelection));
  }

  /**
   * Récupère les activités par service associé
   */
  getActivitesParService(service: string): Observable<ActiviteEtudiant[]> {
    return of(this.activites.filter(a => a.serviceAssocie === service));
  }

  /**
   * Exécute une activité (appel API)
   */
  executerActivite(activite: ActiviteEtudiant, params?: any): Observable<any> {
    let url = activite.route;
    
    // Remplace les paramètres dans l'URL
    if (params) {
      Object.keys(params).forEach(key => {
        url = url.replace(`{${key}}`, params[key]);
      });
    }

    // Vérifie que tous les paramètres sont remplacés
    const missingParams = url.match(/{([^}]+)}/g);
    if (missingParams) {
      throw new Error(`Paramètres manquants: ${missingParams.join(', ')}`);
    }

    // Exécute la requête
    if (activite.methode === 'GET') {
      return this.http.get(url);
    } else if (activite.methode === 'POST') {
      return this.http.post(url, params || {});
    } else {
      throw new Error(`Méthode non supportée: ${activite.methode}`);
    }
  }
}