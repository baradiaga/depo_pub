import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from '../admin/components/dashboard/dashboard.component'; 
import { StudentListComponent } from './components/student-list/student-list.component';
import { StudentDetailComponent } from './components/student-detail/student-detail.component';
import { TestManagerComponent } from './components/test-manager/test-manager.component';
const routes: Routes =  [
  {
    path: '', // Chemin de base du module (ex: /app/tuteur)
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { 
        path: 'listeetudiant', 
        component: StudentListComponent,
        title: 'Suivi des Étudiants' 
      },
      { 
        path: 'student/:id', 
        component: StudentDetailComponent,
        title: 'Analyse du Parcours' 
      },
      { 
        path: 'tests', 
        component: TestManagerComponent,
        title: 'Gestion des Évaluations' 
      },
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
export class TuteurRoutingModule { }
