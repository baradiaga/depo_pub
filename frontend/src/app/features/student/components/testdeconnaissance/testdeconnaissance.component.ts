import { Component, OnInit } from '@angular/core';
import { Observable, BehaviorSubject, switchMap, of, tap, catchError } from 'rxjs';
import { RecommandationTestService, ElementConstitutif, ChapitreTest, MatiereSelection } from '../../../../services/recommandation-test.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';

@Component({
  selector: 'app-testdeconnaissance',
  templateUrl: './testdeconnaissance.component.html',
  styleUrls: ['./testdeconnaissance.component.css']
})
export class TestdeconnaissanceComponent implements OnInit {

  matieres$!: Observable<MatiereSelection[]>;
  ecDetails$!: Observable<ElementConstitutif | undefined>;
  private selectedMatiereIdSubject = new BehaviorSubject<number | null>(null);
  
  isLoading = true;
  isDetailsLoading = false;

  constructor(
    private recoTestService: RecommandationTestService,
    private toastr: ToastrService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.matieres$ = this.recoTestService.getListeMatieres().pipe(
      catchError(err => {
        console.error("Erreur critique lors du chargement des matières:", err);
        this.toastr.error("Impossible de charger la liste des matières. Vérifiez la console.", "Erreur Réseau");
        this.isLoading = false;
        return of([]);
      })
    );

    this.matieres$.subscribe(() => {
      this.isLoading = false;
    });

    this.ecDetails$ = this.selectedMatiereIdSubject.pipe(
      switchMap(id => {
        if (id) {
          this.isDetailsLoading = true;
          return this.recoTestService.getDetailsEC(id).pipe(
            tap(() => this.isDetailsLoading = false)
          );
        }
        return of(undefined);
      })
    );
  }

  onSelectMatiere(event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    this.selectedMatiereIdSubject.next(selectElement.value ? Number(selectElement.value) : null);
  }

  onStartTest(chapitre: ChapitreTest): void {
    this.toastr.info(`Préparation du test pour : ${chapitre.nom}`, 'Test de connaissance');
    this.router.navigate(['/admin/passer-test', chapitre.id]);
  }
}
