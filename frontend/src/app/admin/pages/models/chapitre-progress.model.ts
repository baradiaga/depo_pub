export interface ChapitreProgress {
  // Propriétés existantes
  chapitreId: number;              // ← AJOUTER
  chapitreNom: string;             // ← AJOUTER
  score: number;                   // ← AJOUTER (0-100)
  tempsPasse: number;              // ← AJOUTER (en minutes)
  statut: 'NON_COMMENCE' | 'EN_COURS' | 'TERMINE' | 'ABANDONNE'; // ← AJOUTER
  
  // Propriétés optionnelles
  dateCompletion?: Date;           // Date de fin
  dateDebut?: Date;                // Date de début
  quizReussis?: number;            // Quiz réussis
  quizTotal?: number;              // Total quiz
  matiere?: string;                // Matière (si séparée)
  
  // ... autres propriétés
}