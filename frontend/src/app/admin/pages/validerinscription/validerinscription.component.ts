// Fichier: src/app/features/admin/components/valider-inscription/valider-inscription.component.ts

import { Component, OnInit } from '@angular/core';
import { InscriptionService } from '../../../services/inscription.service';
import { Inscription, InscriptionValidationRequest } from '../../../models/inscription-validation.model';

@Component({
  selector: 'app-valider-inscription',
  templateUrl: './validerinscription.component.html',
  styleUrls: ['./validerinscription.component.css']
})
export class ValiderInscriptionComponent implements OnInit {

  inscriptions: Inscription[] = [];
  loading: boolean = false;
  error: string | null = null;
  success: string | null = null;

  constructor(private inscriptionService: InscriptionService) { }

  ngOnInit(): void {
    this.chargerInscriptionsEnAttente();
  }

  /**
   * Charge la liste des inscriptions en attente de validation.
   */
  chargerInscriptionsEnAttente(): void {
    this.loading = true;
    this.error = null;
    this.success = null;

    this.inscriptionService.getInscriptionsEnAttente().subscribe(
      (data: Inscription[]) => {
        this.inscriptions = data;
        this.loading = false;
      },
      (error: any) => {
        console.error('Erreur lors du chargement des inscriptions:', error);
        this.error = 'Impossible de charger les inscriptions en attente.';
        this.loading = false;
      }
    );
  }

  /**
   * Valide une inscription en changeant son statut à VALIDE.
   * @param inscription L'inscription à valider.
   */
  validerInscription(inscription: Inscription): void {
    const request: InscriptionValidationRequest = {
      inscriptionId: inscription.id,
      statut: 'VALIDE'
    };

    this.inscriptionService.validerInscription(request).subscribe(
      (response: Inscription) => {
        this.success = `L'inscription de l'étudiant a été validée avec succès.`;
        // Mettre à jour la liste locale
        this.inscriptions = this.inscriptions.filter(i => i.id !== inscription.id);
        // Optionnel: Recharger la liste
        setTimeout(() => this.chargerInscriptionsEnAttente(), 2000);
      },
      (error: any) => {
        console.error('Erreur lors de la validation:', error);
        this.error = 'Erreur lors de la validation de l\'inscription.';
      }
    );
  }

  /**
   * Rejette une inscription en changeant son statut à REJETE.
   * @param inscription L'inscription à rejeter.
   */
  rejeterInscription(inscription: Inscription): void {
    const request: InscriptionValidationRequest = {
      inscriptionId: inscription.id,
      statut: 'REJETE'
    };

    this.inscriptionService.validerInscription(request).subscribe(
      (response: Inscription) => {
        this.success = `L'inscription de l'étudiant a été rejetée.`;
        // Mettre à jour la liste locale
        this.inscriptions = this.inscriptions.filter(i => i.id !== inscription.id);
        // Optionnel: Recharger la liste
        setTimeout(() => this.chargerInscriptionsEnAttente(), 2000);
      },
      (error: any) => {
        console.error('Erreur lors du rejet:', error);
        this.error = 'Erreur lors du rejet de l\'inscription.';
      }
    );
  }

  /**
   * Ferme le message d'erreur.
   */
  fermerErreur(): void {
    this.error = null;
  }

  /**
   * Ferme le message de succès.
   */
  fermerSucces(): void {
    this.success = null;
  }

  /**
   * Active une inscription en mettant le champ 'actif' à true.
   * @param inscription L'inscription à activer.
   */
  activerInscription(inscription: Inscription): void {
    this.inscriptionService.changerStatutActif(inscription.id, true).subscribe(
      (response: Inscription) => {
        this.success = `L'inscription a été activée avec succès.`;
        // Mettre à jour l'inscription dans la liste locale
        const index = this.inscriptions.findIndex(i => i.id === inscription.id);
        if (index !== -1) {
          this.inscriptions[index].actif = true;
        }
      },
      (error: any) => {
        console.error('Erreur lors de l\'activation:', error);
        this.error = 'Erreur lors de l\'activation de l\'inscription.';
      }
    );
  }

  /**
   * Désactive une inscription en mettant le champ 'actif' à false.
   * @param inscription L'inscription à désactiver.
   */
  desactiverInscription(inscription: Inscription): void {
    this.inscriptionService.changerStatutActif(inscription.id, false).subscribe(
      (response: Inscription) => {
        this.success = `L'inscription a été désactivée avec succès.`;
        // Mettre à jour l'inscription dans la liste locale
        const index = this.inscriptions.findIndex(i => i.id === inscription.id);
        if (index !== -1) {
          this.inscriptions[index].actif = false;
        }
      },
      (error: any) => {
        console.error('Erreur lors de la désactivation:', error);
        this.error = 'Erreur lors de la désactivation de l\'inscription.';
      }
    );
  }
}
