export interface Chapitre {
  id: number;
  nom: string;
  matiere: string;
  score?: number;  // facultatif (utile surtout pour recommandé)
  raison?: string; // facultatif (utile surtout pour recommandé)
  statut: 'non commencé' | 'en cours' | 'terminé';
  type: 'recommande' | 'choisi'; // pour différencier
}
