import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GestionQuestionnaireComponent } from './components/gestion-questionnaire/gestion-questionnaire.component';
import { SharedModule } from '../../shared/shared.module'; 
import { DashboardComponent } from '../admin/components/dashboard/dashboard.component'; 
const routes: Routes = [
{ path: 'gestion-questionnaire', component: GestionQuestionnaireComponent },
 { path: 'dashboard', component: DashboardComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes),SharedModule],
  exports: [RouterModule]
})
export class EnseignantRoutingModule { }
