// models/recommandation.model.ts
export interface Ressource {
  type: 'pdf' | 'video' | 'quiz';
  titre: string;
  lien: string;
}

export interface RecommandationChapitre {
  id: number;
  nomChapitre: string;
  matiere: string;
  score: number;
  raison: string;
  niveau: 'Facile' | 'Moyen' | 'Difficile';
  progression: number; // % progression étudiant
  dureeEstimee: string; // ex: "2h30"
  statut: 'Non commencé' | 'En cours' | 'Terminé';
  dateRecommandation: Date;
  ressources?: Ressource[];
}
