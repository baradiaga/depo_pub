// Fichier : src/app/models/models.ts (Version mise à jour pour le compte à rebours)
export type TypeQuestion = 'QCM' | 'QCU' | 'VRAI_FAUX' | 'TEXTE_LIBRE';
export type Difficulte = 'FACILE' | 'MOYEN' | 'DIFFICILE';
export type StatutQuestion = 'BROUILLON' | 'VALIDEE' | 'ARCHIVEE';
// ====================================================================
// ===                MODÈLES POUR LES ENTITÉS DE BASE              ===
// ====================================================================
export interface BanqueReponseCreation {
  texte: string;
  correcte: boolean;
}


export interface BanqueQuestionCreation {
  enonce: string;
  typeQuestion: TypeQuestion;
  points: number;
  difficulte: Difficulte;
  chapitreId: number;
  reponses: BanqueReponseCreation[];
  tags: string[]; // Noms des tags
}
export interface BanqueReponseDetail {
  id: number;
  texte: string;
  correcte: boolean;
}
export interface BanqueQuestionDetail {
  id: number;
  enonce: string;
  typeQuestion: TypeQuestion;
  points: number;
  difficulte: Difficulte;
  chapitreId: number;
  chapitreNom: string;
  auteurNom: string;
  dateCreation: string; // LocalDateTime en Java -> string en TS
  noteQualite: number;
  nombreUtilisations: number;
  reponses: BanqueReponseDetail[];
  tags: string[];
  chapitres: Chapitre[];
  statut: StatutQuestion;
}
export interface EvaluationQuestion {
  note: number;
  commentaire?: string;
}
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
export interface CreateTestRequest {
  titre: string;
  chapitreId: number;
  questionIds: number[];
}
export interface QuestionnaireManuel {
  titre: string;
  matiereId: number | null;
  chapitreId: number | null;
  duree: number;
  description: string;
  questions: QuestionPourCreation[];
}
// Fichier : src/app/models/models.ts (Ajout)

export interface RessourcePedagogique {
  id: number;
  titre: string;
  description: string;
  nomFichier: string;
  cheminStockage: string;
  typeMime: string;
  tailleOctets: number;
  dateTeleversement: string;
  chapitreId: number;
  // Ajoutez d'autres champs si nécessaire (auteur, tags, etc.)
}
// --- FormationCreation (payload envoyé au backend) ---
export interface FormationCreation {
  // informations générales
  nom: string;
  code: string;
  description?: string;
  annee: number;


  // métadonnées
  statut: 'ACTIF' | 'ARCHIVE' | 'EN_PREPARATION';
  duree: number; // en années (ou fraction)
  niveauEtude: 'LICENCE' | 'MASTER' | 'CERTIFICAT' | 'DOCTORAT';
  responsableId: number | null;

  // pédagogique
  objectifs?: string;
  competences?: string[];      // tableau de compétences
  prerequis?: string;
  debouches?: string;
  evaluationModalites?: string;
  volumeHoraireTotal?: number; // en heures

  // organisation / admin
  modaliteEnseignement?: 'PRESENTIEL' | 'DISTANCIEL' | 'HYBRIDE';
  lieu?: string;
  dateDebut?: string; // ISO date string
  dateFin?: string;   // ISO date string
  capacite?: number;
  tarif?: number;

  // structure
  elementsConstitutifsIds: number[]; // <-- tableau

  // ressources humaines / documents
  intervenantsIds?: number[];
  documentsIds?: number[];
}

// --- FormationDetail (retour du backend, avec plus de détails) ---
export interface FormationDetail {
  id: number;
  nom: string;
  code?: string;
  description?: string;

  statut?: 'ACTIF' | 'ARCHIVE' | 'EN_PREPARATION';
  duree?: number;
  niveauEtude?: 'LICENCE' | 'MASTER' | 'CERTIFICAT' | 'DOCTORAT';

  responsableNom?: string; // ou responsableId?: number
  createurNom?: string;
  dateCreation?: string;

  objectifs?: string;
  competences?: string[];
  prerequis?: string;
  debouches?: string;
  evaluationModalites?: string;
  volumeHoraireTotal?: number;

  modaliteEnseignement?: string;
  lieu?: string;
  dateDebut?: string;
  dateFin?: string;
  capacite?: number;
  tarif?: number;

  elementsConstitutifs?: ElementConstitutifResponse[]; // liaison
  intervenants?: { id: number; nom: string; prenom?: string }[];
  documents?: { id: number; nomFichier: string; url?: string }[];
}
