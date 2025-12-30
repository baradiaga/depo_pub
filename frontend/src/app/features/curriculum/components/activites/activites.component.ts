import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../../services/auth.service';
import { ElementConstitutifService } from '../../../../services/element-constitutif.service';
import { ElementConstitutifResponse } from '../../../../models/models';

@Component({
  selector: 'app-activites',
  templateUrl: './activites.component.html',
  styleUrls: ['./activites.component.css']
})
export class ActivitesComponent implements OnInit {
  loading = true;
  utilisateurNomComplet: string = '';
  utilisateurEmail: string = '';
  utilisateurRole: string = '';

  // Stats réelles
  stats = {
    totalMatieres: 0,
    totalChapitres: 0,
    progressionMoyenne: 0
  };

  portails: any[] = [];

  constructor(
    private router: Router, 
    private authService: AuthService,
    private ecService: ElementConstitutifService
  ) {}

  ngOnInit(): void {
    this.chargerUtilisateur();
    this.chargerDonneesReelles();
  }

  chargerUtilisateur(): void {
    this.utilisateurNomComplet = this.authService.getUserFullName() || 'Étudiant';
    this.utilisateurEmail = localStorage.getItem('user_email') || '';
    this.utilisateurRole = localStorage.getItem('user_role') || 'Étudiant';
  }

  chargerDonneesReelles() {
    this.loading = true;
    this.ecService.getElementsConstitutifsAvecDetails().subscribe({
      next: (matieres: ElementConstitutifResponse[]) => {
        this.calculerStats(matieres);
        this.genererPortails(matieres);
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur de synchro', err);
        this.loading = false;
      }
    });
  }

  calculerStats(matieres: ElementConstitutifResponse[]) {
    this.stats.totalMatieres = matieres.length;
    
    let totalChap = 0;
    let totalProgress = 0;

    matieres.forEach(m => {
      const nbChap = m.chapitres?.length || 0;
      totalChap += nbChap;
      
      // Calcul de progression simple pour la moyenne
      if (nbChap > 0) {
        const termines = m.chapitres!.filter(c => (c as any).valide).length;
        totalProgress += (termines / nbChap) * 100;
      }
    });

    this.stats.totalChapitres = totalChap;
    this.stats.progressionMoyenne = matieres.length > 0 ? Math.round(totalProgress / matieres.length) : 0;
  }

  genererPortails(matieres: ElementConstitutifResponse[]) {
    this.portails = [
      { 
        titre: 'Mes Matières', 
        type: 'MATIERES', 
        icone: '📚', 
        couleur: '#1c5980', 
        info: `${this.stats.totalMatieres} Enseignements`,
        progress: this.stats.progressionMoyenne 
      },
      { 
        titre: 'Centre d\'Exercices', 
        type: 'EXERCICES', 
        icone: '✍️', 
        couleur: '#2a9d8f', 
        info: 'Pratique interactive',
        progress: 0 // À lier plus tard à un service d'exercices
      },
      { 
        titre: 'Ressources', 
        type: 'RESSOURCES', 
        icone: '📂', 
        couleur: '#e9c46a', 
        info: 'Documents & PDF',
        progress: 100
      }
    ];
  }

  ouvrirPortail(type: string) {
    const routes: any = {
      'MATIERES': 'app/curriculum/matieresliste',
      'EXERCICES': '/app/curriculum/evaluations',
      'RESSOURCES': '/curriculum/matieresliste' // Temporaire
    };
    this.router.navigate([routes[type]]);
  }
}
