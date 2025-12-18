export interface Departement {
  id: number;
  nom: string;
  sigle: string;
  adresse: string;
  contact: string;
  logo?: string;
  lien?: string;
  uefrId: number;
  createdAt?: Date;
  updatedAt?: Date;
  formationId: number;
}