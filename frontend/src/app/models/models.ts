// src/app/models/models.ts

// ====================================================================
// === TYPES GÉNÉRAUX (Fournis par l'utilisateur)
// ====================================================================
export type TypeQuestion = 'QCM' | 'QCU' | 'VRAI_FAUX' | 'TEXTE_LIBRE';
export type Difficulte = 'FACILE' | 'MOYEN' | 'DIFFICILE';
export type StatutQuestion = 'BROUILLON' | 'VALIDEE' | 'ARCHIVEE';

// ====================================================================
// === TYPES SPÉCIFIQUES À LA FORMATION ACADÉMIQUE (Perfectionnement)
// ====================================================================
export type StatutFormation = 'ACTIF' | 'ARCHIVE' | 'EN_PREPARATION' | 'EN_VALIDATION';
export type NiveauEtude = 'LICENCE' | 'MASTER' | 'CERTIFICAT' | 'DOCTORAT' | 'BACHELOR' | 'MS';
export type ModaliteEnseignement = 'PRESENTIEL' | 'DISTANCIEL' | 'HYBRIDE';
export type NiveauAcquisition = 'INITIATION' | 'MAITRISE' | 'EXPERTISE';

// ====================================================================
// === NOUVELLES INTERFACES POUR LA STRUCTURE UNIVERSITAIRE
// ====================================================================

export interface Etablissement {
  id: number;
  nom: string;
  // Ajoutez d'autres propriétés si nécessaire dans votre API
}

export interface Uefr {
  id: number;
  nom: string;
  etablissementId?: number;
  // Ajoutez d'autres propriétés si nécessaire
}

export interface Departement {
  id: number;
  nom: string;
  uefrId?: number;
  // Ajoutez d'autres propriétés si nécessaire
}

/**
 * Représente une compétence détaillée avec son niveau visé et ses indicateurs d'évaluation.
 */
export interface CompetenceDetail {
  libelle: string;
  niveauAcquisition: NiveauAcquisition;
  indicateursEvaluation: string; // Simplifié en string pour le formulaire
}

/**
 * Représente une Unité d'Enseignement (UE) ou un Module.
 * C'est l'unité de base de la structure académique.
 */
export interface UniteEnseignement {
    id?: number;
    nom: string;
    code: string;
    description: string;
    objectifs: string;
    ects: number;
    semestre: number;
    formationId: number | null;
    anneeCycle: number;
    elementConstitutifIds: number[];
    volumeHoraireCours: number;
    volumeHoraireTD: number;
    volumeHoraireTP: number;
    // Ajoutez ceci pour le calcul local
    niveauEtudeComplet?: string;
}

// ====================================================================
// === MODÈLES DE CONTENU PÉDAGOGIQUE (Fournis par l'utilisateur)
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
 * Représente un Élément Constitutif (Matière/Cours).
 * Le champ 'credit' est conservé tel que fourni par l'utilisateur.
 */
export interface ElementConstitutifResponse {
  id: number;
  nom: string;
  code: string;
  volumeHoraireCours: number;
  volumeHoraireTD: number;
  volumeHoraireTP: number;
  uniteEnseignementId: number;
  description: string;
  credit: number;
  enseignant: {
    id: number;
    nom: string;
    prenom: string;
  } | null;
  chapitres?: Chapitre[];
}

/**
 * Payload pour la création d'un Élément Constitutif.
 */
export interface ElementConstitutifRequest {
  nom: string;
  code: string;
  credit: number;
  description: string;
  enseignantId: number | null;
  volumeHoraireCours: number;
  volumeHoraireTD: number;
  volumeHoraireTP: number;
  uniteEnseignementId: number;
}

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
}

// ====================================================================
// === MODÈLES DE FORMATION (Fusion du perfectionnement et des champs utilisateur)
// ====================================================================

/**
 * Interface pour la création/modification d'une formation (Payload).
 * Intègre la nouvelle structure modulaire (UEs) et les compétences détaillées.
 */
export interface FormationCreation {
  // NOUVEAUX CHAMPS - Références administratives
  etablissementId: number;
  uefrId: number;
  departementId: number;
  
  // Informations Générales
  nom: string;
  code: string;
  description: string;
  statut: StatutFormation;
  duree: number;
  niveauEtude: NiveauEtude;
  anneeCycle: number; // Remplace 'annee'
  responsableId: number | null;

  // Pédagogique
  objectifs: string;
  competences: CompetenceDetail[]; // Nouvelle structure
  prerequis: string;
  debouches: string;
  evaluationModalites: string;

  // Organisation / Admin
  modaliteEnseignement: ModaliteEnseignement;
  lieu: string;
  dateDebut: string;
  dateFin: string;
  capacite: number | null;
  tarif: number | null;
  certificationProfessionnelle: string; // Nouveau champ

  // Structure Académique
  unitesEnseignement: UniteEnseignement[]; // Nouvelle structure

  // Ressources Humaines / Documents
  intervenantsIds: number[];
  documentsIds: number[];
}

/**
 * Interface pour la lecture (détail) d'une formation.
 * Inclut les champs calculés (volume horaire, ECTS) et les détails des relations.
 */
export interface FormationDetail extends FormationCreation {
  id: number;
  volumeHoraireTotal: number; // Calculé
  ectsTotal: number; // Calculé
  responsableNom?: string;
  createurNom?: string;
  dateCreation?: string;
  
  // NOUVEAUX CHAMPS - Références complètes pour l'affichage
  etablissement?: Etablissement;
  uefr?: Uefr;
  departement?: Departement;
}

// ====================================================================
// === MODÈLES BANQUE DE QUESTIONS / TESTS (Fournis par l'utilisateur)
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
  tags: string[];
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
  dateCreation: string;
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

export interface ChapitrePayload {
  matiere: string;
  titre: string;
  niveau: number;
  objectif: string;
  sections: { titre: string }[];
}

export interface SectionUpdatePayload {
  titre: string;
  contenu: string;
}

export interface ChapitreDetail {
  id: number;
  nom: string;
  elementConstitutifId: number;
}

export interface Reponse {
  id: number;
  texte: string;
}

export interface Question {
  id: number;
  enonce: string;
  type: 'QCU' | 'QCM' | 'VRAI_FAUX' | 'TEXTE_LIBRE';
  reponses: Reponse[];
  dureeTest?: number;
}

export interface ResultatTest {
  scoreObtenu: number;
  totalPointsPossible: number;
  message: string;
}

export interface QuestionPourCreation {
  type: 'qcm' | 'qcu' | 'vrai_faux' | 'texte_libre';
  enonce: string;
  points: number;
  difficulte: 'facile' | 'moyen' | 'difficile';
  reponses?: { texte: string; correcte: boolean; }[];
  reponseVraiFaux?: boolean;
}

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