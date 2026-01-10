// Fichier : src/app/features/admin/components/echelle-connaissance/echelle-connaissance.component.ts (Mise à jour)

import { Component, OnInit } from '@angular/core';
import { EchelleConnaissance, EchelleConnaissanceService } from '../services/echelle-connaissance.service'; // Assurez-vous que le chemin est correct

@Component({
  selector: 'app-echelle-connaissance',
  templateUrl: './echelle-connaissance.component.html',
  styleUrls: ['./echelle-connaissance.component.css']
})
export class EchelleConnaissanceComponent implements OnInit {
  // Remplacement des données mock par un tableau vide
  echelles: EchelleConnaissance[] = [];
  
  // Le type est maintenant l'interface importée
  newEchelle: EchelleConnaissance = { 
  intervalle: '', 
  seuilMin: 0, 
  seuilMax: 0, 
  couleur: '#6c757d', 
  description: '', 
  recommandation: '' 
};
  editingId: number | null = null;
  loading: boolean = false;
  errorMessage: string | null = null;

  // Injection du service
  constructor(private echelleService: EchelleConnaissanceService) { }

  ngOnInit(): void {
    this.loadEchelles();
  }

  loadEchelles() {
    this.loading = true;
    this.errorMessage = null;
    this.echelleService.getAll().subscribe({
      next: (data) => {
        this.echelles = data;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = "Erreur lors du chargement des échelles. Vérifiez que le backend est démarré et que vous êtes authentifié.";
        this.loading = false;
        console.error(err);
      }
    });
  }

  addOrUpdateEchelle() {
     console.log("Données envoyées au serveur :", this.newEchelle); 
  this.errorMessage = null;
  this.loading = true;
  // SÉCURITÉ : Empêcher le "null" pour la base de données
  if (!this.newEchelle.couleur) {
    this.newEchelle.couleur = '#6c757d'; // Gris par défaut
  }

  // AUTO-GENERATION de l'intervalle pour satisfaire la contrainte NOT NULL de la BDD
  if (!this.newEchelle.intervalle || this.newEchelle.intervalle === '') {
    this.newEchelle.intervalle = `[${this.newEchelle.seuilMin}% - ${this.newEchelle.seuilMax}%]`;
  }

  if (this.editingId) {
    const echelleToUpdate: EchelleConnaissance = { ...this.newEchelle, id: this.editingId };
    this.echelleService.update(echelleToUpdate).subscribe({
      next: () => {
        this.loadEchelles();
        this.resetForm();
      },
      error: (err) => {
        this.errorMessage = "Erreur lors de la mise à jour. Vérifiez les logs serveurs.";
        this.loading = false;
        console.error(err);
      }
    });
  } else {
    // CRÉATION
    this.echelleService.create(this.newEchelle).subscribe({
      next: () => {
        this.loadEchelles();
        this.resetForm();
      },
      error: (err) => {
        // Si l'erreur 500 persiste ici, c'est que seuilMin/Max arrivent NULL au serveur
        this.errorMessage = "Erreur lors de la création. Vérifiez que tous les champs numériques sont remplis.";
        this.loading = false;
        console.error(err);
      }
    });
  }
}


  editEchelle(echelle: EchelleConnaissance) {
    this.editingId = echelle.id!; // L'ID est garanti d'exister ici
    this.newEchelle = { ...echelle };
  }

  deleteEchelle(id: number) {
    this.errorMessage = null;
    this.echelleService.delete(id).subscribe({
      next: () => {
        this.echelles = this.echelles.filter(e => e.id !== id); // Mise à jour optimiste de l'UI
      },
      error: (err) => {
        this.errorMessage = "Erreur lors de la suppression.";
        console.error(err);
      }
    });
  }

  resetForm() {
  this.newEchelle = { 
    intervalle: '', 
    seuilMin: 0, 
    seuilMax: 0, 
    couleur: '#6c757d', 
    description: '', 
    recommandation: '' 
  };
  this.editingId = null;
  this.loading = false;
}
}
