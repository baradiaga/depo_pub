import { Component } from '@angular/core';
import { ThemeService } from '../../../core/services/theme.service';

interface FeatureGroup {
  module: string;
  subFeatures: string[];
}

@Component({
  selector: 'app-feature-assignment',
  templateUrl: './feature-assignment.component.html',
  styleUrls: ['./feature-assignment.component.css']
})
export class FeatureAssignmentComponent {


 
  roles: string[] = ['ADMIN', 'ETUDIANT', 'ENSEIGNANT', 'TUTEUR', 'TECHNOPEDAGOGUE', 'RESPONSABLE_FORMATION'];
  selectedRole: string = '';
  afficherListe: boolean = false;

  visibleModules: Set<string> = new Set<string>();

  allFeatures: FeatureGroup[] = [
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

  roleFeaturesMap: Record<string, string[]> = {
    ADMIN: ['Créer utilisateur', 'Lister utilisateurs'],
    ETUDIANT: [],
    ENSEIGNANT: []
  };

  isFeatureAssigned(feature: string): boolean {
    return this.roleFeaturesMap[this.selectedRole]?.includes(feature);
  }

  toggleFeatureAssignment(feature: string): void {
    const features = this.roleFeaturesMap[this.selectedRole] || [];

    if (features.includes(feature)) {
      this.roleFeaturesMap[this.selectedRole] = features.filter(f => f !== feature);
    } else {
      this.roleFeaturesMap[this.selectedRole] = [...features, feature];
    }
  }

  toggleModuleAssignment(module: string, assign: boolean): void {
    const group = this.allFeatures.find(f => f.module === module);
    if (!group) return;

    const current = this.roleFeaturesMap[this.selectedRole] || [];

    if (assign) {
      this.visibleModules.add(module);
    } else {
      this.visibleModules.delete(module);
      this.roleFeaturesMap[this.selectedRole] = current.filter(f => !group.subFeatures.includes(f));
    }
  }

  onMainFeatureToggle(event: Event, module: string): void {
    const input = event.target as HTMLInputElement;
    if (input && module) {
      this.toggleModuleAssignment(module, input.checked);
    }
  }

  enregistrer(): void {
    console.log('Permissions pour', this.selectedRole, ':', this.roleFeaturesMap[this.selectedRole]);
    alert('Permissions enregistrées pour le rôle : ' + this.selectedRole);
  }

    
   
    }

