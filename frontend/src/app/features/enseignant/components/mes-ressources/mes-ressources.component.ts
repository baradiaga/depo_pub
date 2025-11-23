// Fichier : src/app/features/enseignant/components/mes-ressources/mes-ressources.component.ts

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ElementConstitutifResponse } from '../../../../models/models';
import { ElementConstitutifService } from '../../../../services/element-constitutif.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-mes-ressources',
  templateUrl: './mes-ressources.component.html',
  styleUrls: ['./mes-ressources.component.scss']
})
export class MesRessourcesComponent implements OnInit {

  mesMatieres: ElementConstitutifResponse[] = [];
  isLoading = true;
  errorMessage: string | null = null;

  constructor(
    private ecService: ElementConstitutifService,
    private router: Router,
    private toastr: ToastrService
  ) { }

  ngOnInit(): void {
    this.chargerMesMatieres();
  }

  chargerMesMatieres(): void {
    this.isLoading = true;
    this.errorMessage = null;
    this.ecService.getMesMatieres().subscribe({
      next: (data) => {
        this.mesMatieres = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = "Une erreur est survenue lors du chargement de vos matières.";
        this.isLoading = false;
        this.toastr.error(this.errorMessage);
        console.error(err);
      }
    });
  }

  /**
   * Navigue vers la page de gestion de contenu pour la matière sélectionnée.
   * @param matiereId L'ID de la matière à gérer.
   */
  gererContenu(matiereId: number): void {
    this.router.navigate(['app/enseignant/gestion-contenu', matiereId]);
  }
}
