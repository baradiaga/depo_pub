import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
// 1. Importez votre composant dashboard ici
import { DashboardComponent } from '../admin/components/dashboard/dashboard.component';

const routes: Routes = [
  {
    path: '',
    children: [
      { path: 'dashboard', component: DashboardComponent },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TechnopedagogueRoutingModule { }
