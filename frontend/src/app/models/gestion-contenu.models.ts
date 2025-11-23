// Fichier : src/app/models/gestion-contenu.models.ts (Version Finale Complète)

/**
 * Représente une section de contenu (texte, vidéo, etc.).
 * Correspond au DTO SectionDto du backend.
 */
export interface Section {
  id: number;
  titre: string;
  contenu: string; // Peut être du HTML, une URL de vidéo, etc.
  ordre: number;
  typeSection: 'TEXTE' | 'VIDEO' | 'FICHIER' | 'EXERCICE'| 'QUIZ'; // Types possibles
}

/**
 * Représente un chapitre avec toutes ses sections.
 * Correspond au DTO ChapitreContenuDto du backend.
 */
export interface ChapitreContenu {
  id: number;
  nom: string;
  objectif: string;
  ordre: number;
  sections: Section[];
}

// ====================================================================
// === MODÈLES AJOUTÉS POUR LA CRÉATION ET LA MISE À JOUR           ===
// ====================================================================

/**
 * Données nécessaires pour créer un nouveau chapitre.
 * Correspond au DTO ChapitreCreateDto du backend.
 */
export interface ChapitreCreateDto {
  nom: string;
  objectif?: string; // L'objectif est optionnel à la création
}

/**
 * Données nécessaires pour créer une nouvelle section.
 * Correspond au DTO SectionCreateDto du backend.
 */
export interface SectionCreateDto {
  titre: string;
  typeSection: 'TEXTE' | 'VIDEO' | 'FICHIER' | 'EXERCICE' | 'QUIZ';
}

/**
 * Données nécessaires pour mettre à jour une section existante.
 * Correspond au DTO SectionUpdateDto du backend.
 */
export interface SectionUpdateDto {
  titre: string;
  contenu: string;
}
