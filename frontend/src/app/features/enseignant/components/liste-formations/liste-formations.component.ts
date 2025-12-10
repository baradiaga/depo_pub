import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { FormationService } from '../../../../services/formation.service';
import { FormationDetail } from '../../../../models/models';
import { Router } from '@angular/router';

@Component({
  selector: 'app-liste-formations',
  templateUrl: './liste-formations.component.html',
  styleUrls: ['./liste-formations.component.css']
})
export class ListeFormationsComponent implements OnInit {

  formations: FormationDetail[] = [];
  formationsFiltrees: FormationDetail[] = [];
  isLoading = false;

  formRecherche: FormGroup;
  statuts: string[] = ['TOUS', 'ACTIF', 'ARCHIVE', 'EN_PREPARATION'];

  constructor(
    private fb: FormBuilder,
    private formationService: FormationService,
    private router: Router   // ✅ injection du Router pour naviguer vers la page d’édition
  ) {
    this.formRecherche = this.fb.group({
      recherche: [''],
      statut: ['TOUS']
    });
  }

  ngOnInit(): void {
    this.chargerFormations();

    // appliquer le filtre à chaque changement
    this.formRecherche.get('recherche')?.valueChanges.subscribe(() => this.appliquerFiltre());
    this.formRecherche.get('statut')?.valueChanges.subscribe(() => this.appliquerFiltre());
  }

  // Chargement des formations depuis le backend
  chargerFormations(): void {
    this.isLoading = true;
    this.formationService.getAllFormations().subscribe({
      next: (data) => {
        this.formations = data || [];
        this.appliquerFiltre();
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
      }
    });
  }

  // Appliquer le filtrage selon la recherche et le statut
  appliquerFiltre(): void {
    const recherche = (this.formRecherche.get('recherche')?.value || '').toLowerCase();
    const statut = this.formRecherche.get('statut')?.value;

    this.formationsFiltrees = this.formations.filter(f => {
      const matchesRecherche =
        (f.nom?.toLowerCase().includes(recherche) || false) ||
        (f.code?.toLowerCase().includes(recherche) || false);

      const matchesStatut = statut === 'TOUS' || f.statut === statut;

      return matchesRecherche && matchesStatut;
    });
  }

  // ✅ Supprimer une formation
  supprimerFormation(id: number): void {
    if (!confirm('Êtes-vous sûr de vouloir supprimer cette formation ?')) return;

    this.formationService.supprimerFormation(id).subscribe({
      next: () => {
        // on met à jour la liste locale
        this.formations = this.formations.filter(f => f.id !== id);
        this.appliquerFiltre();
      },
      error: (err) => console.error(err)
    });
  }

  // ✅ Éditer une formation
  editerFormation(id: number): void {
    // redirection vers une page d’édition (ex: /app/enseignant/formations/:id/edit)
    this.router.navigate(['/app/enseignant/formations', id, 'edit']);
  }

}
