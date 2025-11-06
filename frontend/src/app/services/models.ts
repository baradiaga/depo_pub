// Fichier : src/app/services/models.ts (Nouveau et Central)

// ====================================================================
// === CE FICHIER EST LE NOUVEAU POINT DE VÉRITÉ POUR VOS INTERFACES ===
// ====================================================================

// --- INTERFACES POUR SECTION ---

/**
 * Représente une section d'un chapitre.
 * Utilisée dans toute l'application.
 */
export interface Section {
  id: number;
  titre: string;
  contenu: string;
  ordre: number;
}

/**
 * Le payload envoyé au backend pour mettre à jour une section.
 * Doit correspondre au DTO `SectionUpdateRequest.java`.
 */
export interface SectionUpdatePayload {
  titre: string;
  contenu: string;
}

// --- INTERFACES POUR CHAPITRE ---

/**
 * Représente un chapitre.
 * Utilisée dans toute l'application.
 */
export interface Chapitre {
  id: number;
  nom: string; // Le nom standardisé est 'nom'
  objectif: string;
  niveau: number;
  elementConstitutifNom: string;
  sections: Section[]; // Utilise l'interface Section définie ci-dessus
  score?: number; // Propriété optionnelle pour les parcours étudiants
}

/**
 * Le payload envoyé au backend pour créer un chapitre et sa structure.
 * Doit correspondre au DTO `ChapitrePayload.java`.
 */
export interface ChapitrePayload {
  matiere: string;
  titre: string;
  niveau: number;
  objectif: string;
  sections: { titre: string }[];
}

// --- INTERFACE POUR ÉLÉMENT CONSTITUTIF (MATIÈRE) ---

/**
 * Représente un Élément Constitutif (ou "Matière").
 */
// --- INTERFACES POUR ÉLÉMENT CONSTITUTIF (MATIÈRE) ---
export interface ElementConstitutifResponse {
  id: number;
  nom: string;
  code: string;
  description: string;
  credit: number;
  enseignant: {
    id: number;
    nom: string;
    prenom: string;
  } | null;
  chapitres?: Chapitre[];
}

// ====================================================================
// === L'INTERFACE MANQUANTE EST AJOUTÉE ICI                        ===
// ====================================================================
export interface ElementConstitutifRequest {
  nom: string;
  code: string;
  credit: number;
  description: string;
  enseignantId: number | null;
}