import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ChapitreService, ChapitreAvecSections, SectionDetail } from '../../../../services/chapitre.service';

@Component({
  selector: 'app-apprentissage-cours',
  templateUrl: './apprentissage-cours-component.component.html', // Vérifiez que le nom correspond à votre fichier
  styleUrls: ['./apprentissage-cours-component.component.css']
})
export class ApprentissageCoursComponent implements OnInit {
  // On ajoute 'undefined' pour que le HTML accepte le safe navigation operator (?.)
  chapitre: ChapitreAvecSections | undefined; 
  sectionActive: SectionDetail | undefined;
  loading = true;

  constructor(
    public router: Router, // MODIFICATION : private -> public pour corriger l'erreur 2341
    private route: ActivatedRoute,
    private chapitreService: ChapitreService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.chargerContenu(+id);
    }
  }

  chargerContenu(id: number) {
    this.loading = true;
    this.chapitreService.getChapitreComplet(id).subscribe({
      next: (data) => {
        this.chapitre = data;
        if (this.chapitre.sections) {
          this.chapitre.sections.sort((a, b) => a.ordre - b.ordre);
          if (this.chapitre.sections.length > 0) {
            this.sectionActive = this.chapitre.sections[0];
          }
        }
        this.loading = false;
      },
      error: () => {
        this.router.navigate(['/curriculum/matieresliste']);
        this.loading = false;
      }
    });
  }

  selectionnerSection(section: SectionDetail) {
    this.sectionActive = section;
  }

  allerASuivant() {
    if (this.chapitre && this.sectionActive) {
      const index = this.chapitre.sections.indexOf(this.sectionActive);
      if (index < this.chapitre.sections.length - 1) {
        this.sectionActive = this.chapitre.sections[index + 1];
      }
    }
  }
}
