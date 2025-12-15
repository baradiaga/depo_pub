export interface InscriptionValidationRequest {
  inscriptionId: number;
  statut: 'VALIDE' | 'REJETE';
}

// src/app/admin/models/inscription.dto.ts
export interface Inscription {
  id: number;
  statut: 'EN_ATTENTE' | 'VALIDE' | 'REJETE';
  actif: boolean;
  dateInscription: string;

  // Étudiant
  etudiantId: number;
  etudiantNomComplet: string;
  etudiantEmail: string;

  // Matière
  ecId: number;
  ecNom: string;
  ecCode: string;
}


// Getter pour le template
export function getNomEtudiantDTO(inscription: any): string {
  return inscription.etudiantNomComplet; // déjà formaté par le backend
}

export function getNomMatiereDTO(inscription: any): string {
  return inscription.ecNom; // déjà présent dans le DTO
}
