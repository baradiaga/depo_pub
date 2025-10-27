// Correspond à SousFonctionnaliteDTO.java
export interface SousFonctionnalite {
  id?: number;
  label: string;
  featureKey: string;
  route: string;
}

// Correspond à FonctionnaliteDTO.java
export interface Fonctionnalite {
  id?: number;
  nom: string;
  featureKey: string;
  icon: string;
  sousFonctionnalites: SousFonctionnalite[];
}

// Correspond à la Map<String, Set<String>> du PermissionController
export type PermissionsMap = Record<string, string[]>;
