export interface Uefr {
   id: number;
  nom: string;
  sigle: string;
  adresse: string;
  contact: string;
  logo?: string;
  lien?: string;
  etablissementId: number;
  createdAt?: Date;
  updatedAt?: Date;
}