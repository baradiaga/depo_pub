import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GestionQuestionnaireComponent } from './components/gestion-questionnaire/gestion-questionnaire.component';
import { SharedModule } from '../../shared/shared.module'; 
import { DashboardComponent } from '../admin/components/dashboard/dashboard.component'; 
import { SelectionMatiereGestionComponent } from './components/selection-matiere-gestion/selection-matiere-gestion.component';

import { GestionContenuComponent } from './components/gestion-contenu/gestion-contenu.component';
import { MesRessourcesComponent } from './components/mes-ressources/mes-ressources.component';
import { BanqueQuestionGestionComponent } from './components/banque-question-gestion/banque-question-gestion.component';
import { QuestionnaireDetailsComponent } from './components/questionnaire-details/questionnaire-details.component';
import { GestionRessourcesPedagogiquesComponent } from './components/gestion-ressources-pedagogiques/gestion-ressources-pedagogiques.component';
import { GestionFormationsComponent } from './components/gestion-formations/gestion-formations.component';
import { QuestionnaireListComponent } from './components/questionnaire-list-component/questionnaire-list-component.component';
import { ListeFormationsComponent } from './liste-formations/liste-formations.component';
const routes: Routes = [
{ path: 'gestion-questionnaire', component: GestionQuestionnaireComponent },
{ path: 'dashboard', component: DashboardComponent },
{ path: 'gestion-contenu', component: SelectionMatiereGestionComponent },
{ path: 'gestion-contenu/:id',  component: GestionContenuComponent },
{ path: 'ressources',component: MesRessourcesComponent},
{ path: 'banque-questions',component: BanqueQuestionGestionComponent},
{ path: 'questionnaires/:id', component: QuestionnaireDetailsComponent },
{ path: 'banques', component: GestionRessourcesPedagogiquesComponent },
{ path: 'formations', component: GestionFormationsComponent },
{ path: 'formations/:id/edit', component: GestionFormationsComponent },



{ path: 'listequestionnaire', component: QuestionnaireListComponent },
{ path: 'listeformation', component: ListeFormationsComponent }

];

@NgModule({
  imports: [RouterModule.forChild(routes),SharedModule],
  exports: [RouterModule]
})
export class EnseignantRoutingModule { }
