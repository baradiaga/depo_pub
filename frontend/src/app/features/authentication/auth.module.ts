// Fichier : src/app/features/authentication/auth.module.ts (Version finale)

import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module'; // <-- 1. IMPORTER SHAREDMODULE
import { AuthRoutingModule } from './auth-routing.module'; // <-- 2. IMPORTER LE ROUTING

import { LoginComponent } from './components/login/login.component';

@NgModule({
  declarations: [
    LoginComponent
  ],
  imports: [
    SharedModule,      // <-- 3. AJOUTER ICI (fournit FormsModule, CommonModule, etc.)
    AuthRoutingModule  // <-- 4. AJOUTER ICI
  ]
})
export class AuthModule { }
