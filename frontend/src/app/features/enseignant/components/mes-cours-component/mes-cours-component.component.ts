import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { EnseignantService, Formation } from '../../../../services/enseignant.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GestionFormationsComponent } from '../gestion-formations/gestion-formations.component';

@Component({
  selector: 'app-mes-cours-component',
  templateUrl: './mes-cours-component.component.html',
  styleUrls: ['./mes-cours-component.component.css']
})
export class MesCoursComponentComponent implements OnInit {
  public formations$!: Observable<Formation[]>;

  // Filtres
  searchText: string = '';
  statusFilter: string = '';

  constructor(
    private enseignantService: EnseignantService,
    private router: Router,
    private modalService: NgbModal
  ) { }

  ngOnInit(): void {
    this.formations$ = this.enseignantService.getMesFormations();
  }

  voirDetails(formation: Formation) {
    // Option 1 : Navigation vers une page de détail
    // this.router.navigate(['/formations', formation.id]);

    // Option 2 : Ouvrir un modal avec le composant de détail
    const modalRef = this.modalService.open(GestionFormationsComponent, { size: 'lg' });
    modalRef.componentInstance.formationId = formation.id;
  }

  editerFormation(formation: Formation) {
  this.router.navigate(['/enseignant/formations', formation.id, 'edit']);
}

}
