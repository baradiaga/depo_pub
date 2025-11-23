// Fichier : src/app/pages/syllabus/syllabus.component.ts (Version avec voirDetails implémenté)

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SyllabusService, MatiereSyllabus, ChapitreSyllabus } from '../../../../services/syllabus.service';

// --- IMPORTS NÉCESSAIRES POUR LA MODALE ---
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { VueCoursComponent } from '../../../student/components/vue-cours/vue-cours.component'; // J'ai corrigé le chemin ici

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
    private syllabusService: SyllabusService,
    private modalService: NgbModal // <-- MODIFICATION 1/2 : Injection du service de modale
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
    this.router.navigate(['/app/student/test/passer', chapitre.id]);
  }

  // ====================================================================
  // === MÉTHODE "voirDetails" IMPLÉMENTÉE POUR OUVRIR LA MODALE      ===
  // ====================================================================
  /**
   * Ouvre une fenêtre modale pour afficher le contenu détaillé du cours.
   * @param chapitre Le chapitre sur lequel l'utilisateur a cliqué.
   */
  voirDetails(chapitre: ChapitreSyllabus): void { // <-- MODIFICATION 2/2 : Le contenu de la méthode est remplacé
    // Sécurité : on s'assure que les données de la matière sont chargées.
    if (!this.matiere) {
      console.error("[SyllabusComponent] Impossible d'ouvrir les détails : les données de la matière ne sont pas chargées.");
      return;
    }

    console.log(`[SyllabusComponent] Ouverture du contenu pour la matière ID : ${this.matiere.id}`);

    // On ouvre le composant 'VueCoursComponent' dans une modale.
    const modalRef = this.modalService.open(VueCoursComponent, {
      size: 'xl',           // Modale extra-large
      centered: true,       // Centrée verticalement
      scrollable: true,     // Permet au contenu de la modale de défiler
      fullscreen: 'lg-down' // Plein écran sur les petits appareils
    });

    // On passe l'ID de la matière au composant 'VueCoursComponent'.
    modalRef.componentInstance.matiereId = this.matiere.id;
  }
}
