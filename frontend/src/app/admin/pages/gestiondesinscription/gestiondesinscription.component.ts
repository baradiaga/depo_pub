import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { EtudiantService, EtudiantDto } from '../../../services/etudiant.service';

@Component({
  selector: 'app-gestiondesinscription',
  templateUrl: './gestiondesinscription.component.html',
  styleUrls: ['./gestiondesinscription.component.css']
})
export class GestionDesInscriptionComponent implements OnInit {
  isLoading = true;
  etudiantsOriginaux: EtudiantDto[] = [];
  etudiantsFiltres: EtudiantDto[] = [];
  termeDeRecherche: string = '';

  constructor(private etudiantService: EtudiantService, private router: Router, private toastr: ToastrService) {}

  ngOnInit(): void { this.chargerInscriptions(); }

  chargerInscriptions(): void {
    this.isLoading = true;
    this.etudiantService.getEtudiants().subscribe({
      next: (data) => {
        this.etudiantsOriginaux = data;
        this.etudiantsFiltres = data;
        this.isLoading = false;
      },
      error: () => { this.isLoading = false; this.toastr.error('Erreur lors du chargement des étudiants.'); }
    });
  }

  filtrerEtudiants(): void {
    const filtre = this.termeDeRecherche.toLowerCase().trim();
    if (!filtre) {
      this.etudiantsFiltres = this.etudiantsOriginaux;
    } else {
      this.etudiantsFiltres = this.etudiantsOriginaux.filter(e =>
        e.nom.toLowerCase().includes(filtre) || e.prenom.toLowerCase().includes(filtre) || e.email.toLowerCase().includes(filtre)
      );
    }
  }

  inscrireEtudiant(): void {
    this.router.navigate(['/app/admin/inscriptions/nouveau']);
  }

  modifierInscription(id: number): void {
    this.router.navigate(['/app/admin/inscriptions/modifier', id]);
  }

  supprimerInscription(id: number): void {
    if (confirm('Voulez-vous vraiment supprimer cet étudiant ?')) {
      this.etudiantService.deleteEtudiant(id).subscribe({
        next: () => { this.toastr.success('Étudiant supprimé.'); this.chargerInscriptions(); },
        error: (err) => this.toastr.error(err.error?.message || 'Erreur lors de la suppression.')
      });
    }
  }
}
