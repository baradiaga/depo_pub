import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GestionQuestionnaireComponent } from './components/gestion-questionnaire/gestion-questionnaire.component';
import { SharedModule } from '../../shared/shared.module'; 
import { DashboardComponent } from '../admin/components/dashboard/dashboard.component'; 
import { SelectionMatiereGestionComponent } from './components/selection-matiere-gestion/selection-matiere-gestion.component';

import { GestionContenuComponent } from './components/gestion-contenu/gestion-contenu.component';
import { MesRessourcesComponent } from './components/mes-ressources/mes-ressources.component';
const routes: Routes = [
{ path: 'gestion-questionnaire', component: GestionQuestionnaireComponent },
{ path: 'dashboard', component: DashboardComponent },
{ path: 'gestion-contenu', component: SelectionMatiereGestionComponent },
{ path: 'gestion-contenu/:id',  component: GestionContenuComponent },
 { path: 'ressources',component: MesRessourcesComponent},
];

@NgModule({
  imports: [RouterModule.forChild(routes),SharedModule],
  exports: [RouterModule]
})
export class EnseignantRoutingModule { }
