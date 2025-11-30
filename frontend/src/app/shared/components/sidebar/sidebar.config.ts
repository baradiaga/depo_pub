import { UserRole } from '../../../models/user.model';

export interface MenuItem {
  id: number;
  label: string;
  featureKey: string;
  icon?: string;
  children?: SubMenuItem[];
  defaultRoles?: UserRole[];
}

export interface SubMenuItem {
  label: string;
  route: string;
  featureKey: string;
  queryParams?: any;
}

export const COMPLETE_SIDEBAR_CONFIG: MenuItem[] = [
  // --- ID 1: Paramétrage (Routes corrigées) ---
  {
    id: 1,
    label: 'Paramétrage',
    featureKey: 'parametrage',
    icon: 'settings',
    defaultRoles: ['ADMIN', 'ETUDIANT', 'TUTEUR', 'ENSEIGNANT', 'RESPONSABLE_FORMATION', 'TECHNOPEDAGOGUE'],
    children: [
      // Ces routes sont maintenant préfixées. Assurez-vous d'avoir un module pour les gérer.
      { label: 'Profil utilisateur', route: '/app/profil', featureKey: 'profil_utilisateur' },
      { label: 'Modifier mot de passe', route: '/app/profil/change-password', featureKey: 'modifier_mot_passe' },
      { label: 'Modifier profil', route: '/app/profil/edit', featureKey: 'modifier_profil' }
    ]
  },
  // --- ID 2: Gestion des utilisateurs (Routes corrigées) ---
  {
    id: 2,
    label: 'Gestion des utilisateurs',
    featureKey: 'gestion_utilisateurs',
    icon: 'users',
    defaultRoles: ['ADMIN'],
    children: [
      { label: 'Gestion Des rôles', route: '/app/admin/roles', featureKey: 'gestion_roles' },
      { label: 'Permissions', route: '/app/admin/permissions', featureKey: 'gestion_permissions' },
      { label: 'Gestion utilisateur', route: '/app/admin/users', featureKey: 'gestion_utilisateur' }
    ]
  },
  // --- ID 3: Gestion des fonctionnalités (Routes corrigées) ---
  {
    id: 3,
    label: 'Gestion des fonctionnalités',
    featureKey: 'gestion_fonctionnalites',
    icon: 'settings',
    defaultRoles: ['ADMIN'],
    children: [
      { label: 'Ajouter fonctionnalité', route: '/app/admin/features', featureKey: 'ajouter_fonctionnalite' },
      { label: 'Attribuer une fonctionnalité', route: '/app/admin/feature-assignment', featureKey: 'attribuer_fonctionnalite' },
      { label: 'Liste des fonctionnalités', route: '/app/admin/features', featureKey: 'liste_fonctionnalites' },
      { label: 'Gestion des Permissions', route: '/app/admin/permissions', featureKey: 'gestion_permissions_admin' }
    ]
  },
  // --- ID 4: Gestion des formations (Routes corrigées) ---
  {
    id: 4,
    label: 'Gestion des formations',
    featureKey: 'gestion_formations',
    icon: 'book',
    defaultRoles: ['ADMIN', 'RESPONSABLE_FORMATION'],
    children: [
      { label: 'Créer formation', route: '/app/enseignant/formations', featureKey: 'creer_formation' },
      { label: 'Liste des formations', route: '/app/admin/formations', featureKey: 'liste_formations' },
      { label: 'Gérer inscriptions', route: '/app/admin/inscriptions', featureKey: 'gerer_inscriptions' }
    ]
  },
  // --- ID 5: Gestion des maquettes (Routes corrigées) ---
  {
    id: 5,
    label: 'Gestion des maquettes',
    featureKey: 'gestion_maquettes',
    icon: 'layout',
    defaultRoles: ['ADMIN', 'RESPONSABLE_FORMATION','ENSEIGNANT'],
    children: [
      { label: 'Unité d\'enseignement', route: '/app/admin/unites-enseignement', featureKey: 'gestion_unites' },
      { label: 'Éléments constitutifs', route: '/app/admin/elements-constitutifs', featureKey: 'gestion_elements' },
      { label: 'Paramétrage des chapitres', route: '/app/admin/parametrage-chapitre', featureKey: 'parametrage_chapitres' },
      { label: 'Structure des matières', route: '/app/admin/matieres', featureKey: 'structure_matieres' },
      { label: 'Syllabus', route: '/app/admin/chapitres', featureKey: 'syllabus' }
    ]
  },
  // --- ID 6: Gestion des inscriptions aux classes (Routes corrigées) ---
  {
    id: 6,
    label: 'Gestion des inscriptions aux classes',
    featureKey: 'gestion_inscriptions_classes',
    icon: 'user-plus',
    defaultRoles: ['ADMIN', 'RESPONSABLE_FORMATION'],
    children: [
      { label: 'Inscriptions aux classes', route: '/app/admin/inscriptions', featureKey: 'inscriptions_classes' },
      { label: 'Validation inscriptions', route: '/app/admin/validation-inscriptions', featureKey: 'validation_inscriptions' }
    ]
  },
  // --- ID 7: Gestion des équivalences (Routes corrigées) ---
  {
    id: 7,
    label: 'Gestion des équivalences',
    featureKey: 'gestion_equivalences',
    icon: 'equal',
    defaultRoles: ['ADMIN', 'RESPONSABLE_FORMATION'],
    children: [
      { label: 'Créer équivalence', route: '/app/admin/equivalences/create', featureKey: 'creer_equivalence' },
      { label: 'Liste équivalences', route: '/app/admin/equivalences', featureKey: 'liste_equivalences' }
    ]
  },
  // --- ID 8: Gestion des parcours (Admin) (Routes corrigées) ---
  {
    id: 8,
    label: 'Gestion des parcours (Admin)',
    featureKey: 'gestion_parcours_admin',
    icon: 'route',
    defaultRoles: ['ADMIN'],
    children: [
      { label: 'Parcours recommandés', route: '/app/admin/parcours', featureKey: 'parcours_recommandes_admin', queryParams: { type: 'RECOMMANDE' } },
      { label: 'Parcours choisis', route: '/app/admin/parcours', featureKey: 'parcours_choisis_admin', queryParams: { type: 'CHOISI' } },
      { label: 'Parcours mixtes', route: '/app/admin/parcours', featureKey: 'parcours_mixtes_admin', queryParams: { type: 'MIXTE' } }
    ]
  },
  // --- ID 9: Gestion des ressources pédagogiques (Routes corrigées) ---
  {
    id: 9,
    label: 'Gestion des ressources pédagogiques',
    featureKey: 'gestion_ressources_pedagogiques',
    icon: 'folder',
    defaultRoles: ['ADMIN', 'ENSEIGNANT', 'TECHNOPEDAGOGUE'],
    children: [
      { label: 'Créer ressource', route: '/app/enseignant/matiere/:id/gestion', featureKey: 'creer_ressource' },
      { label: 'Mes ressources', route: '/app/enseignant/ressources', featureKey: 'mes_ressources' },
      { label: 'Banque de ressource', route: '/app/enseignant/banques', featureKey: 'bibliotheque' }
    ]
  },
  // --- ID 10: Gestion des échelles de connaissances (Routes corrigées) ---
  {
    id: 10,
    label: 'Gestion des échelles de connaissances',
    featureKey: 'gestion_echelles_connaissances',
    icon: 'bar-chart',
    defaultRoles: ['ADMIN', 'ENSEIGNANT'],
    children: [
      { label: 'Échelles de connaissances', route: '/app/admin/echelles', featureKey: 'echelles_connaissances' },
      { label: 'Évaluation compétences', route: '/app/admin/evaluation-competences', featureKey: 'evaluation_competences' }
    ]
  },
  // --- ID 11: Gestion des catégories (Routes corrigées) ---
  {
    id: 11,
    label: 'Gestion des catégories',
    featureKey: 'gestion_categories',
    icon: 'tag',
    defaultRoles: ['ADMIN'],
    children: [
      { label: 'Catégories', route: '/app/admin/categories', featureKey: 'categories' },
      { label: 'Sous-catégories', route: '/app/admin/sous-categories', featureKey: 'sous_categories' }
    ]
  },
  // --- ID 13: Gestion des questionnaires (Routes corrigées) ---
  {
    id: 13,
    label: 'Gestion des questionnaires',
    featureKey: 'gestion_questionnaires',
    icon: 'help-circle',
    defaultRoles: ['ADMIN', 'ENSEIGNANT', 'TECHNOPEDAGOGUE'],
    children: [
      { label: 'Créer questionnaires', route: '/app/enseignant/gestion-questionnaire', featureKey: 'liste_questionnaire' },
      { label: 'liste de questionnaire', route: '/app/enseignant/gestion-questionnaire/create', featureKey: 'creer_questionnaire' },
      { label: 'Banque de questions', route: '/app/enseignant/banque-questions', featureKey: 'banque_questions' }
    ]
  },
  // --- ID 14 (premier): ressources externes (Routes corrigées) ---
  {
    id: 12, // ID corrigé pour éviter doublon
    label: 'Ressources externes',
    featureKey: 'gestion_ressources_externes',
    icon: 'external-link',
    defaultRoles: ['ADMIN'],
    children: [
      { label: 'Ressources externes', route: '/app/admin/external-resources', featureKey: 'categories' },
      { label: 'Banque de ressources', route: '/app/admin/external-resources/bank', featureKey: 'sous_categories' }
    ]
  },
  // --- ID 14 (second): Gestion des remédiation (Routes corrigées) ---
  {
    id: 14,
    label: 'Gestion des remédiation',
    featureKey: 'gestion_remediation',
    icon: 'refresh-cw',
    defaultRoles: ['ETUDIANT', 'TUTEUR', 'ENSEIGNANT'],
    children: [
      { label: 'Matières', route: '/app/curriculum/matieres', featureKey: 'matieres' },
      { label: 'Test de connaissance', route: '/app/student/test-connaissance', featureKey: 'test-connaissance' },
      { label: 'Mes Résultats', route: '/app/student/mes-resultats', featureKey: 'mes-resultats' }
    ]
  },
  // --- ID 15: Apprentissage asynchrone (Routes corrigées) ---
  {
    id: 15,
    label: 'Apprentissage asynchrone',
    featureKey: 'apprentissage_asynchrone',
    icon: 'play-circle',
    defaultRoles: ['ETUDIANT', 'ENSEIGNANT', 'TECHNOPEDAGOGUE'],
    children: [
      { label: 'Séquences', route: '/app/curriculum/sequences', featureKey: 'sequences' },
      { label: 'Activités', route: '/app/curriculum/activites', featureKey: 'activites' },
      { label: 'Évaluations', route: '/app/curriculum/evaluations', featureKey: 'evaluations' }
    ]
  },
  // --- ID 16: Tutorat (Routes corrigées) ---
  {
    id: 16,
    label: 'Tutorat',
    featureKey: 'tutorat',
    icon: 'user-check',
    defaultRoles: ['TUTEUR'],
    children: [
      { label: 'Mes étudiants', route: '/app/tuteur/mes-etudiants', featureKey: 'mes_etudiants' },
      { label: 'Suivi progression', route: '/app/tuteur/suivi', featureKey: 'suivi_progression' },
      { label: 'Planifier séances', route: '/app/tuteur/planifier-seances', featureKey: 'planifier_seances' }
    ]
  },
  // --- ID 17: Enseignement (Routes corrigées) ---
  {
    id: 17,
    label: 'Enseignement',
    featureKey: 'enseignement',
    icon: 'graduation-cap',
    defaultRoles: ['ENSEIGNANT'],
    children: [
      { label: 'Mes cours', route: '/app/enseignant/mes-cours', featureKey: 'mes_cours' },
      { label: 'Créer contenu', route: 'app/enseignant/gestion-questionnaire', featureKey: 'creer_contenu' },
      { label: 'Évaluer étudiants', route: '/app/enseignant/evaluer-etudiants', featureKey: 'evaluer_etudiants' }
    ]
  },
  // --- ID 18 (premier): Mes parcours (Étudiant) (Routes corrigées) ---
  {
    id: 18,
    label: 'Mes parcours (Étudiant)',
    featureKey: 'mes_parcours_etudiant',
    icon: 'route',
    defaultRoles: ['ETUDIANT'],
    children: [
  { label: 'Parcours recommandés', route: '/app/student/parcours', featureKey: 'parcours_recommandes_etudiant', queryParams: { type: 'recommandes' } },
  { label: 'Parcours choisis', route: '/app/student/parcours', featureKey: 'parcours_choisis_etudiant', queryParams: { type: 'choisis' } },
  { label: 'Parcours mixtes', route: '/app/student/parcours', featureKey: 'parcours_mixtes_etudiant', queryParams: { type: 'mixtes' } }
]

  },
  // --- ID 18 (second): Innovation pédagogique (Routes corrigées) ---
  {
    id: 19, // ID corrigé pour éviter doublon
    label: 'Innovation pédagogique',
    featureKey: 'innovation_pedagogique',
    icon: 'lightbulb',
    defaultRoles: ['TECHNOPEDAGOGUE'],
    children: [
      { label: 'Outils numériques', route: '/app/technopedagogue/outils-numeriques', featureKey: 'outils_numeriques' },
      { label: 'Méthodes innovantes', route: '/app/technopedagogue/methodes-innovantes', featureKey: 'methodes_innovantes' },
      { label: 'Formation enseignants', route: '/app/technopedagogue/formation-enseignants', featureKey: 'formation_enseignants' }
    ]
  }
];

// La configuration des permissions reste inchangée
export const DEFAULT_ROLE_PERMISSIONS: Record<UserRole, string[]> = {
  // L'ADMIN a accès à TOUT. On liste toutes les clés de fonctionnalités principales.
  ADMIN: [
    'parametrage',
    'gestion_utilisateurs',
    'gestion_fonctionnalites',
    'gestion_formations',
    'gestion_maquettes',
    'gestion_inscriptions_classes',
    'gestion_equivalences',
    'gestion_parcours_admin',
    'gestion_ressources_pedagogiques',
    'gestion_echelles_connaissances',
    'gestion_categories',
    'gestion_questionnaires',
    'gestion_ressources_externes', // Clé corrigée
    'gestion_remediation',
    'apprentissage_asynchrone',
    'tutorat',
    'enseignement',
    'mes_parcours_etudiant',
    'innovation_pedagogique'
  ],

  // L'ETUDIANT a accès à ses fonctionnalités d'apprentissage.
  ETUDIANT: [
    'parametrage',
    'mes_parcours_etudiant',
    'gestion_remediation',
    'apprentissage_asynchrone',
    'test-connaissance',
    'mes-resultats'
  ],

  // L'ENSEIGNANT a accès à ses outils de création, de suivi et de contenu.
  ENSEIGNANT: [
    'parametrage',
    'enseignement',
    'gestion_ressources_pedagogiques',
    'gestion_echelles_connaissances',
    'gestion_questionnaires',
    'gestion_remediation',
    'apprentissage_asynchrone',
    'gestion_maquettes',       // Pour voir le menu principal
    'parametrage_chapitres'
  ],

  // Le TUTEUR a accès aux outils de suivi et de remédiation.
  TUTEUR: [
    'parametrage',
    'tutorat',
    'gestion_remediation'
  ],

  // Le TECHNOPEDAGOGUE gère les contenus, les outils et l'innovation.
  TECHNOPEDAGOGUE: [
    'parametrage',
    'innovation_pedagogique',
    'gestion_ressources_pedagogiques',
    'gestion_questionnaires',
    'apprentissage_asynchrone'
  ],

  // Le RESPONSABLE_FORMATION a une vue administrative sur le curriculum et les formations.
  RESPONSABLE_FORMATION: [
    'parametrage',
    'gestion_formations',
    'gestion_maquettes',
    'gestion_inscriptions_classes',
    'gestion_equivalences',
    'gestion_parcours_admin'
  ],

  // Rôle de base avec accès minimal (juste le profil).
  USER: [
    'parametrage'
  ],

  // Rôle de base pour un manager, si vous en avez besoin.
  MANAGER: [
    'parametrage'
  ],
};
