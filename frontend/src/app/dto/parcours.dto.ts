// Fichier : src/app/dto/parcours.dto.ts

// Correspond au ParcoursItemDto du backend
export interface ParcoursItemDto {
  chapitreId: number;
  chapitreNom: string;
  matiereNom: string;
  dernierScore: number;
}

// Correspond au ParcoursDto du backend
export interface ParcoursDto {
  recommandes: ParcoursItemDto[];
  choisis: ParcoursItemDto[];
  mixtes: ParcoursItemDto[];
}
