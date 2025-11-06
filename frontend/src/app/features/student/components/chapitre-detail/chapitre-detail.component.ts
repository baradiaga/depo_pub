import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ChapitreService } from '../../../../services/chapitre.service';
import { Chapitre } from '../../../../services/models'; // <-- CORRECTION
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-chapitre-detail',
  templateUrl: './chapitre-detail.component.html',
  styleUrls: ['./chapitre-detail.component.css']
})
export class ChapitreDetailComponent implements OnInit {
  chapitre: Chapitre | null = null;
  isLoading = true;

  constructor(
    private route: ActivatedRoute,
    private chapitreService: ChapitreService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    const chapitreId = this.route.snapshot.paramMap.get('id');
    if (chapitreId) {
      this.loadChapitreDetails(+chapitreId);
    } else {
      this.isLoading = false;
      this.toastr.error("ID de chapitre manquant dans l'URL.");
    }
  }

  loadChapitreDetails(id: number): void {
    this.isLoading = true;
    // On utilise la méthode corrigée 'findById'
    this.chapitreService.findById(id).subscribe({
      next: (data) => {
        this.chapitre = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.toastr.error('Impossible de charger les détails du chapitre.');
        console.error(err);
      }
    });
  }

  getCouleurScore(score: number | undefined): string {
    if (score === undefined) return 'bg-secondary';
    if (score >= 75) return 'bg-success';
    if (score >= 50) return 'bg-warning text-dark';
    return 'bg-danger';
  }
}
