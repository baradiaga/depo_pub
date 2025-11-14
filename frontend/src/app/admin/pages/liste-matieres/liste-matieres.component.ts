// Fichier : src/app/admin/pages/liste-matieres/liste-matieres.component.ts (Final avec rafraîchissement automatique)

import { Component, OnInit, OnDestroy } from '@angular/core'; // <-- OnDestroy AJOUTÉ
import { Subscription } from 'rxjs'; // <-- Subscription AJOUTÉ

import { ElementConstitutifService } from '../../../services/element-constitutif.service';
import { Chapitre, ElementConstitutifResponse } from '../../../models/models';

@Component({
  selector: 'app-liste-matieres',
  templateUrl: './liste-matieres.component.html',
  styleUrls: ['./liste-matieres.component.css']
})
export class ListeMatieresComponent implements OnInit, OnDestroy { // <-- OnDestroy AJOUTÉ
  
  isLoading = true;
  public chapitresParMatiere: { [matiereNom: string]: Chapitre[] } = {};
  public matiereSelectionnee: string = "";
  public contenuVisible: { [matiereNom: string]: boolean } = {};

  // On garde une référence à la souscription pour la détruire proprement.
  private refreshSubscription!: Subscription;

  constructor(private ecService: ElementConstitutifService) {}

  ngOnInit(): void {
    // 1. On charge les données une première fois au démarrage du composant.
    this.chargerDonneesDepuisBackend();

    // ====================================================================
    // === ÉCOUTEUR DE NOTIFICATIONS AJOUTÉ ICI                         ===
    // ====================================================================
    // 2. On s'abonne à l'observable de rafraîchissement du service.
    //    Chaque fois qu'une notification est émise (par ex. après la création d'un chapitre),
    //    le code à l'intérieur de ce subscribe() sera exécuté.
    this.refreshSubscription = this.ecService.refreshNeeded$.subscribe(() => {
      console.log("Notification de rafraîchissement reçue dans ListeMatieresComponent ! Rechargement des données...");
      this.chargerDonneesDepuisBackend();
    });
  }

  /**
   * C'est une bonne pratique de se désabonner pour éviter les fuites de mémoire
   * lorsque le composant est détruit (par exemple, en naviguant vers une autre page).
   */
  ngOnDestroy(): void {
    if (this.refreshSubscription) {
      this.refreshSubscription.unsubscribe();
    }
  }

  chargerDonneesDepuisBackend(): void {
    this.isLoading = true;
    this.ecService.getElementsConstitutifsAvecDetails().subscribe({
      next: (data: ElementConstitutifResponse[]) => {
        this.chapitresParMatiere = {};
        for (const matiere of data) {
          if (matiere.nom) {
            // La propriété 'chapitres' est bien définie dans l'interface ElementConstitutifResponse
            this.chapitresParMatiere[matiere.nom] = matiere.chapitres || [];
          }
        }
        this.isLoading = false;
      },
      error: (err: any) => {
        this.isLoading = false;
        console.error('Erreur lors du chargement des données :', err);
        alert('Impossible de charger les données.');
      }
    });
  }

  // --- Les autres méthodes restent inchangées ---

  getMatieres(): string[] {
    return Object.keys(this.chapitresParMatiere);
  }

  toggleContenu(matiereNom: string): void {
    this.contenuVisible[matiereNom] = !this.contenuVisible[matiereNom];
  }

  isContenuVisible(matiereNom: string): boolean {
    return !!this.contenuVisible[matiereNom];
  }

  getSectionsUniques(sections: any[] | undefined): any[] {
    if (!sections) return [];
    return sections;
  }
}
