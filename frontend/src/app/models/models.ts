// Fichier : src/app/models/models.ts (Version mise à jour pour le compte à rebours)

// ====================================================================
// ===                MODÈLES POUR LES ENTITÉS DE BASE              ===
// ====================================================================

/**
 * Représente une Section de chapitre.
 */
export interface Section {
  id: number;
  titre: string;
  contenu: string;
  ordre: number;
}

/**
 * Représente un Chapitre avec tous ses détails.
 */
export interface Chapitre {
  id: number;
  nom: string;
  objectif: string;
  niveau: number;
  elementConstitutifNom: string;
  sections: Section[];
  score?: number; // Pour les parcours étudiants
}

/**
 * Représente un Élément Constitutif (Matière).
 */
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
// ===                  MODÈLES POUR LES PAYLOADS (DTOs)              ===
// ====================================================================

/**
 * Payload pour la création d'un Élément Constitutif.
 */
export interface ElementConstitutifRequest {
  nom: string;
  code: string;
  credit: number;
  description: string;
  enseignantId: number | null;
}

/**
 * Payload pour la création d'un Chapitre.
 */
export interface ChapitrePayload {
  matiere: string;
  titre: string;
  niveau: number;
  objectif: string;
  sections: { titre: string }[];
}

/**
 * Payload pour la mise à jour d'une Section.
 */
export interface SectionUpdatePayload {
  titre: string;
  contenu: string;
}


// ====================================================================
// ===                  MODÈLES SPÉCIFIQUES AUX TESTS               ===
// ====================================================================

/**
 * Représente les détails d'un chapitre nécessaires pour la page de test.
 */
export interface ChapitreDetail {
  id: number;
  nom: string;
  elementConstitutifId: number;
}

/**
 * Représente une option de réponse pour une question de test (version publique).
 */
export interface Reponse {
  id: number;
  texte: string;
}

/**
 * Représente une question de test (version envoyée au frontend).
 */
export interface Question {
  id: number;
  enonce: string;
  type: 'QCU' | 'QCM' | 'VRAI_FAUX' | 'TEXTE_LIBRE';
  reponses: Reponse[];
  // ====================================================================
  // === CHAMP AJOUTÉ POUR LE COMPTE À REBOURS                        ===
  // ====================================================================
  dureeTest?: number; // Durée totale du test en minutes. Optionnel pour la compatibilité.
}

/**
 * Représente le résultat d'un test après soumission.
 */
export interface ResultatTest {
  scoreObtenu: number;
  totalPointsPossible: number;
  message: string;
}


// ====================================================================
// ===     MODÈLES POUR LA CRÉATION MANUELLE DE QUESTIONNAIRES      ===
// ====================================================================

/**
 * Représente une question lors de sa création par un enseignant.
 */
export interface QuestionPourCreation {
  type: 'qcm' | 'qcu' | 'vrai_faux' | 'texte_libre';
  enonce: string;
  points: number;
  difficulte: 'facile' | 'moyen' | 'difficile';
  reponses?: { texte: string; correcte: boolean; }[];
  reponseVraiFaux?: boolean;
}

/**
 * Représente le payload complet pour la création manuelle d'un questionnaire.
 */
export interface QuestionnaireManuel {
  titre: string;
  matiereId: number | null;
  chapitreId: number | null;
  duree: number;
  description: string;
  questions: QuestionPourCreation[];
}
