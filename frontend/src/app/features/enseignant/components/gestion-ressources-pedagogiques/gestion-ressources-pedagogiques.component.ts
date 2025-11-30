// Fichier : src/app/features/enseignant/components/gestion-ressources-pedagogiques/gestion-ressources-pedagogiques.component.ts

import { Component, OnInit } from '@angular/core';
import { RessourcePedagogiqueService } from '../../../../services/ressource-pedagogique.service';
import { ElementConstitutifService } from '../../../../services/element-constitutif.service';
import { ChapitreService } from '../../../../services/chapitre.service';
import { RessourcePedagogique, ElementConstitutifResponse, Chapitre } from '../../../../models/models';

@Component({
  selector: 'app-gestion-ressources-pedagogiques',
  templateUrl: './gestion-ressources-pedagogiques.component.html',
  styleUrls: ['./gestion-ressources-pedagogiques.component.css']
})
export class GestionRessourcesPedagogiquesComponent implements OnInit {

  // --- Liste des Ressources ---
  ressources: RessourcePedagogique[] = [];
  isLoadingList = false;

  // --- Formulaire de Téléversement ---
  fichierSelectionne: File | null = null;
  metadata = {
    titre: '',
    description: '',
    matiereId: 0,   // ✅ ajouté pour suivre la matière sélectionnée
    chapitreId: 0,
    tags: ''        // Tags séparés par des virgules
  };
  isUploading = false;

  // --- Données de Sélection ---
  mesMatieres: ElementConstitutifResponse[] = [];
  chapitresDisponibles: Chapitre[] = [];

  constructor(
    private ressourceService: RessourcePedagogiqueService,
    private ecService: ElementConstitutifService,
    private chapitreService: ChapitreService
  ) { }

  ngOnInit(): void {
    this.chargerMesMatieres();
    this.chargerRessources();
  }

  // Charger les matières de l'enseignant
  chargerMesMatieres(): void {
    this.ecService.getMesMatieres().subscribe(data => this.mesMatieres = data);
  }

  // Lorsqu'une matière est sélectionnée
  onMatiereChange(matiereId: number): void {
    this.metadata.matiereId = matiereId;
    this.chapitresDisponibles = [];
    this.metadata.chapitreId = 0;
    if (matiereId) {
      this.chapitreService.getChapitresParMatiere(matiereId)
        .subscribe(data => this.chapitresDisponibles = data);
    }
  }

  // Charger toutes les ressources
  chargerRessources(): void {
    this.isLoadingList = true;
    this.ressourceService.getAllRessources().subscribe({
      next: (data) => {
        this.ressources = data;
        this.isLoadingList = false;
      },
      error: (err) => {
        console.error("Erreur chargement des ressources", err);
        this.isLoadingList = false;
      }
    });
  }

  // Sélection d'un fichier
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input?.files?.length) {
      this.fichierSelectionne = input.files[0];
    }
  }

  // Téléverser une ressource
  televerser(): void {
    if (!this.fichierSelectionne || !this.metadata.titre || !this.metadata.chapitreId) {
      alert("Veuillez sélectionner un fichier, donner un titre et choisir un chapitre.");
      return;
    }

    this.isUploading = true;

    const metadataPayload = {
      titre: this.metadata.titre,
      description: this.metadata.description,
      matiereId: this.metadata.matiereId,
      chapitreId: this.metadata.chapitreId,
      tags: this.metadata.tags.split(',')
        .map(t => t.trim())
        .filter(t => t.length > 0)
    };

    this.ressourceService.televerserRessource(this.fichierSelectionne, metadataPayload).subscribe({
      next: () => {
        alert("Ressource téléversée avec succès !");
        this.reinitialiserFormulaire();
        this.chargerRessources();
      },
      error: (err) => {
        console.error("Erreur de téléversement", err);
        alert("Erreur lors du téléversement.");
      }
    }).add(() => this.isUploading = false);
  }

  // Supprimer une ressource
  supprimerRessource(id: number, titre: string): void {
    if (confirm(`Êtes-vous sûr de vouloir supprimer la ressource : "${titre}" ?`)) {
      this.ressourceService.supprimerRessource(id).subscribe({
        next: () => {
          alert("Ressource supprimée.");
          this.chargerRessources();
        },
        error: (err) => {
          console.error("Erreur de suppression", err);
          alert("Erreur lors de la suppression.");
        }
      });
    }
  }

  // Réinitialiser le formulaire
  reinitialiserFormulaire(): void {
    this.fichierSelectionne = null;
    this.metadata = { titre: '', description: '', matiereId: 0, chapitreId: 0, tags: '' };
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    if (fileInput) fileInput.value = '';
  }

  // URL de téléchargement
  getTelechargementUrl(nomFichierStocke: string): string {
    return this.ressourceService.getTelechargementUrl(nomFichierStocke);
  }

  // ✅ Bonus : afficher le nom du chapitre dans la table
  getChapitreNom(id: number): string {
    const chapitre = this.chapitresDisponibles.find(c => c.id === id);
    return chapitre ? chapitre.nom : '—';
  }
}
