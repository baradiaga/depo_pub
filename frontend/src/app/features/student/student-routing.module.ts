import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TestsListComponent } from './components/tests-list/tests-list.component';
import { RecommendationComponent } from './components/recommendation/recommendation.component';
import { ParcoursComponent } from './components/parcours/parcours.component';
import { ChapitreDetailComponent } from './components/chapitre-detail/chapitre-detail.component';

const routes: Routes = [
  { path: 'test/passer/:chapitreId', component: TestsListComponent },
  { path: 'recommendation', component: RecommendationComponent },
  { path: 'parcours', component: ParcoursComponent },
  { path: 'chapitre/:id', component: ChapitreDetailComponent },
  { path: '', redirectTo: 'parcours', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class StudentRoutingModule { }
