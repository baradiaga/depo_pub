export interface StudentJourney {
  id: number;
  nom: string;
  prenom: string;
  email?: string;
  parcoursType: 'RECOMMANDE' | 'CHOISI' | 'MIXTE';
  
  // Ajoutez ces propriétés
  nomComplet?: string;          // Pour "nomComplet"
  formationActuelle?: string;   // Pour "formationActuelle"
  niveauEtude?: string;         // Pour "niveauEtude"
  moyenneGeneraleTests?: number; // Pour "moyenneGeneraleTests"
  testsPasses?: number;         // Pour "testsPasses"
  
  // Propriétés déjà existantes (optionnelles)
  scoreGlobal?: number;
  pourcentageCompletion?: number;
  tempsTotalPasse?: number;
  dateDerniereActivite?: Date;
}