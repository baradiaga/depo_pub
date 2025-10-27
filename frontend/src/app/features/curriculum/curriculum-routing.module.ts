import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

// --- 1. Importez les composants de contenu ---
import { MatieresReprendreComponent } from './components/matieres-reprendre/matieres-reprendre.component';
import { SyllabusComponent } from './components/syllabus/syllabus.component';
import { SequencesComponent } from './components/sequences/sequences.component';
import { ActivitesComponent } from './components/activites/activites.component';
import { EvaluationsComponent } from './components/evaluations/evaluations.component';

const routes: Routes = [
  // Le chemin est relatif à /app/curriculum
  { path: 'matieres', component: MatieresReprendreComponent},
  { path: 'matieres/:slug', component: SyllabusComponent },
  { path: 'sequences', component: SequencesComponent },
  { path: 'activites', component: ActivitesComponent },
  { path: 'evaluations', component: EvaluationsComponent },


  // Route par défaut
  { path: '', redirectTo: 'matieres', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CurriculumRoutingModule { }
