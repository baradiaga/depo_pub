// Ce type énumère tous les rôles possibles dans l'application.
// Il doit correspondre exactement à l'enum `Role.java` de votre backend.
export type UserRole = 
  | 'ADMIN' 
  | 'ETUDIANT' 
  | 'ENSEIGNANT' 
  | 'TUTEUR' 
  | 'TECHNOPEDAGOGUE' 
  | 'RESPONSABLE_FORMATION'
  | 'USER'  // Ajoutez tous les rôles que vous avez
  | 'MANAGER';

// Cette interface définit la structure d'un objet Utilisateur,
// correspondant à l'entité `Utilisateur.java` du backend.
export interface User {
  id?: number;
  nom: string;
  prenom: string;
  email: string;
  role: UserRole;
  actif: boolean;
  // Le mot de passe n'est généralement pas inclus dans les données envoyées au frontend.
}
