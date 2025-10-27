// models/utilisateur-permission.model.ts
export interface UtilisateurPermission {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: string;
  permissions: number[]; // liste des ID de permissions associées
}
