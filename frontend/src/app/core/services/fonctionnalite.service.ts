import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';

export interface FeatureGroup {
  module: string;
  subFeatures: string[];
}

@Injectable({
  providedIn: 'root'
})
export class FonctionnaliteService {

  // Données locales (mock)
  private allFeatures: FeatureGroup[] = [
    {
      module: 'Parametrage',
      subFeatures: ['profil utilisateur', 'Modifier mot de passe', 'modifier profil']
    },
    {
      module: 'Gestion des fonctionnalités',
      subFeatures: ['Créer fonctionnalité', 'Modifier fonctionnalité', 'Supprimer fonctionnalité', 'Lister fonctionnalités']
    },
    {
      module: 'Gestion des utilisateurs',
      subFeatures: ['Créer utilisateur', 'Modifier utilisateur', 'Supprimer utilisateur', 'Lister utilisateurs']
    },
    {
      module: 'Gestion des inscriptions aux classes',
      subFeatures: ['Inscrire étudiant', 'Modifier inscription', 'Supprimer inscription', 'Lister inscriptions par classe']
    },
    {
      module: 'Gestion des inscriptions aux cours',
      subFeatures: ['Inscrire étudiant', 'Modifier inscription', 'Supprimer inscription', 'Lister inscriptions par matière']
    },
    {
      module: 'Gestion des formations',
      subFeatures: ['Créer formation', 'Modifier formation', 'Supprimer formation', 'Lister formations']
    },
    {
      module: 'Gestion des maquettes',
      subFeatures: ['Créer maquette', 'Modifier maquette', 'Supprimer maquette', 'Lister maquettes']
    },
    {
      module: 'Gestion des éléments constitutifs (matières)',
      subFeatures: ['Créer matière', 'Modifier matière', 'Supprimer matière', 'Lister matières']
    },
    {
      module: 'Gestion des équivalences',
      subFeatures: ['Créer équivalence', 'Modifier équivalence', 'Supprimer équivalence', 'Lister équivalences']
    },
    {
      module: 'Gestion des échelles de connaissances',
      subFeatures: [
        'Créer échelle de connaissance',
        'Modifier échelle de connaissance',
        'Supprimer échelle de connaissance',
        'Lister échelles de connaissances'
      ]
    },
    {
      module: 'Gestion des catégories',
      subFeatures: ['Créer catégorie', 'Modifier catégorie', 'Supprimer catégorie', 'Lister catégories']
    },
    {
      module: 'Gestion des parcours questionnaires',
      subFeatures: ['Créer questionnaire', 'Modifier questionnaire', 'Supprimer questionnaire', 'Lister questionnaires']
    },
    {
      module: 'Gestion des questions',
      subFeatures: ['Créer question', 'Modifier question', 'Supprimer question', 'Lister questions']
    },
    {
      module: 'Gestion des parcours',
      subFeatures: ['Générer parcours recommandé', 'Générer parcours choisi', 'Générer parcours mixte']
    },
    {
      module: 'Gestion des ressources pédagogiques',
      subFeatures: [
        'Créer ressource pédagogique',
        'Modifier ressource pédagogique',
        'Supprimer ressource pédagogique',
        'Lister ressources pédagogiques'
      ]
    },
    {
      module: 'Gestion des activités pédagogiques',
      subFeatures: [
        'Créer activité pédagogique',
        'Modifier activité pédagogique',
        'Supprimer activité pédagogique',
        'Lister activités pédagogiques'
      ]
    },
    {
      module: 'Gestion des séances de cours',
      subFeatures: ['Créer séance de cours', 'Modifier séance de cours', 'Supprimer séance de cours', 'Lister séances']
    },
    {
      module: 'Gestion de la foire aux questions (FAQ)',
      subFeatures: ['Créer FAQ', 'Modifier FAQ', 'Supprimer FAQ', 'Lister FAQ']
    }
  ];

 private fonctionnalitesSubject = new BehaviorSubject<FeatureGroup[]>([...this.allFeatures]);
  fonctionnalites$ = this.fonctionnalitesSubject.asObservable();

  constructor() {}

  // Récupérer la liste complète (simule un call API)
  getAllFeatures(): Observable<FeatureGroup[]> {
    return this.fonctionnalites$.pipe(delay(500));
  }

  // Récupérer la valeur synchronisée sans Observable (optionnel)
  getAllFeaturesValue(): FeatureGroup[] {
    return this.fonctionnalitesSubject.getValue();
  }

  // Ajouter un module
  ajouterModule(nouveauModule: FeatureGroup): Observable<FeatureGroup[]> {
    const current = this.getAllFeaturesValue();
    const updated = [...current, nouveauModule];
    this.fonctionnalitesSubject.next(updated);
    return of(updated).pipe(delay(300));
  }

  // Modifier un module
  modifierModule(index: number, moduleModifie: FeatureGroup): Observable<FeatureGroup[]> {
    const current = this.getAllFeaturesValue();
    const updated = current.map((m, i) => i === index ? moduleModifie : m);
    this.fonctionnalitesSubject.next(updated);
    return of(updated).pipe(delay(300));
  }

  // Supprimer un module
  supprimerModule(index: number): Observable<FeatureGroup[]> {
    const current = this.getAllFeaturesValue();
    const updated = current.filter((_, i) => i !== index);
    this.fonctionnalitesSubject.next(updated);
    return of(updated).pipe(delay(300));
  }

  // Ajouter un sous-module à un module donné
  ajouterSousModule(indexModule: number, sousModule: string): Observable<FeatureGroup[]> {
    const current = this.getAllFeaturesValue();
    const updated = current.map((mod, i) => {
      if (i === indexModule) {
        return { ...mod, subFeatures: [...mod.subFeatures, sousModule] };
      }
      return mod;
    });
    this.fonctionnalitesSubject.next(updated);
    return of(updated).pipe(delay(300));
  }

  // Modifier un sous-module
  modifierSousModule(indexModule: number, indexSousModule: number, nouveauNom: string): Observable<FeatureGroup[]> {
    const current = this.getAllFeaturesValue();
    const updated = current.map((mod, i) => {
      if (i === indexModule) {
        const newSubFeatures = mod.subFeatures.map((sf, idx) => idx === indexSousModule ? nouveauNom : sf);
        return { ...mod, subFeatures: newSubFeatures };
      }
      return mod;
    });
    this.fonctionnalitesSubject.next(updated);
    return of(updated).pipe(delay(300));
  }

  // Supprimer un sous-module
  supprimerSousModule(indexModule: number, indexSousModule: number): Observable<FeatureGroup[]> {
    const current = this.getAllFeaturesValue();
    const updated = current.map((mod, i) => {
      if (i === indexModule) {
        const newSubFeatures = mod.subFeatures.filter((_, idx) => idx !== indexSousModule);
        return { ...mod, subFeatures: newSubFeatures };
      }
      return mod;
    });
    this.fonctionnalitesSubject.next(updated);
    return of(updated).pipe(delay(300));
  }
}
