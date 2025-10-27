import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GestionQuestionnaireComponent } from './components/gestion-questionnaire/gestion-questionnaire.component';

const routes: Routes = [
{ path: 'gestion-questionnaire', component: GestionQuestionnaireComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class EnseignantRoutingModule { }
