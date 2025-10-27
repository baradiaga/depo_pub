// Fichier : src/app/features/authentication/auth-routing.module.ts

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';

const routes: Routes = [
  {
    path: 'login', // deviendra /auth/login
    component: LoginComponent
  },
  {
    path: '', // Redirection par défaut pour /auth
    redirectTo: 'login',
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthRoutingModule { }
