import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router'; // Assurez-vous que ActivatedRoute est importé
import { ToastrService } from 'ngx-toastr'; // <-- Utilisons Toastr pour les notifications, c'est plus propre qu'alert()
import { EtudiantService, EtudiantDto } from '../../../services/etudiant.service';

@Component({
  selector: 'app-gestiondesinscription',
  templateUrl: './gestiondesinscription.component.html',
  styleUrls: ['./gestiondesinscription.component.css']
})
export class GestionDesInscriptionComponent implements OnInit {
  
  // On ne garde que la liste des inscriptions
  inscriptions: EtudiantDto[] = []; 
  isLoading = true; // Pour afficher un indicateur de chargement

  // On injecte Router pour la navigation et Toastr pour les notifications
  constructor(
    private etudiantService: EtudiantService,
    private router: Router, 
    private route: ActivatedRoute, 

    private toastr: ToastrService 
  ) {}

  ngOnInit(): void {
    this.chargerInscriptions();
  }

  chargerInscriptions(): void {
    this.isLoading = true;
    this.etudiantService.getEtudiants().subscribe({
      next: (data) => {
        this.inscriptions = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.toastr.error('Erreur lors du chargement des inscriptions.');
        this.isLoading = false;
        console.error(err);
      }
    });
  }

  // --- MÉTHODES DE NAVIGATION ---

  /**
   * Navigue vers le formulaire de création.
   */
  inscrireEtudiant(): void {
    // Le chemin doit correspondre à ce que nous définirons dans le routing
    this.router.navigate(['nouveau'], { relativeTo: this.route }); 
  }

  /**
   * Navigue vers le formulaire de modification pour un étudiant donné.
   */
  modifierInscription(id: number | undefined): void {
    if (!id) return;
    this.router.navigate(['app/admin/inscriptions/modifier', id]);
  }

  // --- MÉTHODE DE SUPPRESSION ---

  supprimerInscription(id: number | undefined): void {
    if (!id) return;
    
    // On peut utiliser une modale de confirmation plus élégante plus tard,
    // pour l'instant, confirm() fait l'affaire.
    if (confirm('Voulez-vous vraiment supprimer cette inscription ?')) {
      this.etudiantService.deleteEtudiant(id).subscribe({
        next: () => {
          this.toastr.success('Inscription supprimée avec succès.');
          // On rafraîchit la liste en filtrant l'élément supprimé pour éviter un appel réseau
          this.inscriptions = this.inscriptions.filter(insc => insc.id !== id);
        },
        error: (err) => {
          this.toastr.error(err.error?.message || 'Une erreur est survenue lors de la suppression.');
          console.error(err);
        }
      });
    }
  }
}
