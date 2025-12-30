import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { ElementConstitutifService } from '../../../../services/element-constitutif.service';
import { ElementConstitutifResponse } from '../../../../models/models';

@Component({
  selector: 'app-matieres-liste',
  templateUrl: './matieres-liste.component.html',
  styleUrls: ['./matieres-liste.component.css']
})
export class MatieresListeComponent implements OnInit, OnDestroy {
  listeMatieres: ElementConstitutifResponse[] = [];
  loading = true;
  searchTerm: string = '';
  private refreshSub!: Subscription;

  constructor(
    private router: Router,
    private ecService: ElementConstitutifService
  ) {}

  ngOnInit(): void {
    this.chargerDonnees();

    // Système de rafraîchissement automatique si le service notifie un changement
    this.refreshSub = this.ecService.refreshNeeded$.subscribe(() => {
      this.chargerDonnees();
    });
  }

  ngOnDestroy(): void {
    if (this.refreshSub) this.refreshSub.unsubscribe();
  }

  chargerDonnees() {
    this.loading = true;
    // On récupère les détails (incluant les chapitres)
    this.ecService.getElementsConstitutifsAvecDetails().subscribe({
      next: (data) => {
        this.listeMatieres = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des matières', err);
        this.loading = false;
      }
    });
  }

  // Calcul de la progression réelle basée sur les chapitres
  calculerProgression(matiere: ElementConstitutifResponse): number {
    if (!matiere.chapitres || matiere.chapitres.length === 0) return 0;
    
    // On considère un chapitre terminé s'il a un statut spécifique (ex: 'TERMINE' ou 'VALIDE')
    // À adapter selon votre modèle de données Chapitre
    const termines = matiere.chapitres.filter(c => (c as any).statut === 'TERMINE' || (c as any).valide).length;
    return Math.round((termines / matiere.chapitres.length) * 100);
  }

  get matieresFiltrees() {
    return this.listeMatieres.filter(m => 
      m.nom.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      m.code.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  ouvrirCours(m: ElementConstitutifResponse) { // Assurez-vous que l'argument s'appelle 'm' ou 'matiere'
  if (m && m.id) {
    this.router.navigate(['app/curriculum/matieres', m.id]);
  }
}
  retour() {
    this.router.navigate(['/app/curriculum/matieres']);
  }
}
