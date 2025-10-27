export interface Etudiant {
  id?: number;
  nom: string;
  prenom: string;
  email: string;
  matieresAReprendre: string[];
  dateInscription?: Date;
  enseignantId?: number;
}

export interface Matiere {
  id: number;
  nom: string;
  description?: string;
  chapitres?: Chapitre[];
}

export interface Chapitre {
  id: number;
  nom: string;
  matiereId: number;
  tests?: Test[];
}

export interface Test {
  id: number;
  titre: string;
  chapitreId: number;
  questions: Question[];
}

export interface Question {
  id: number;
  questionText: string;
  options: string[];
  correctAnswerIndex: number;
}

