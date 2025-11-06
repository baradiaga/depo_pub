// Fichier : src/app/pages/syllabus/syllabus.component.ts (Version Complète et Mise à Jour)

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SyllabusService, MatiereSyllabus, ChapitreSyllabus } from '../../../../services/syllabus.service';

@Component({
  selector: 'app-syllabus',
  templateUrl: './syllabus.component.html',
  styleUrls: ['./syllabus.component.css']
})
export class SyllabusComponent implements OnInit {

  matiere: MatiereSyllabus | null = null;
  isLoading: boolean = true;
  errorMessage: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private syllabusService: SyllabusService
  ) { }

  ngOnInit(): void {
    this.isLoading = true;
    const matiereId = this.route.snapshot.paramMap.get('slug');
    
    if (matiereId) {
      this.syllabusService.getSyllabusPourEtudiant(+matiereId).subscribe({
        next: (data) => {
          console.log('[SyllabusComponent] Syllabus reçu :', data);
          this.matiere = data;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('[SyllabusComponent] Erreur lors du chargement :', err);
          this.errorMessage = "Impossible de charger le syllabus. Le backend a rencontré une erreur.";
          this.isLoading = false;
        }
      });
    } else {
      this.errorMessage = "URL incorrecte. L'ID de la matière est manquant.";
      this.isLoading = false;
    }
  }

  lancerTest(chapitre: ChapitreSyllabus): void {
    console.log(`[SyllabusComponent] Lancement du test pour le chapitre ID : ${chapitre.id}`);
    this.router.navigate(['/app/test/passer', chapitre.id]);
  }

  // ====================================================================
  // === NOUVELLE MÉTHODE POUR LE BOUTON "DÉTAILS"                     ===
  // ====================================================================
  /**
   * Affiche les détails d'un chapitre (logique à implémenter plus tard).
   * @param chapitre Le chapitre dont on veut voir les détails.
   */
  voirDetails(chapitre: ChapitreSyllabus): void {
    console.log(`[SyllabusComponent] Affichage des détails pour le chapitre :`, chapitre);
    // TODO: Implémenter l'ouverture d'une fenêtre modale avec les détails.
    alert(`Détails pour le chapitre "${chapitre.nom}" (à implémenter).`);
  }
}
