// frontend/src/app/features/profile/profile-routing.module.ts

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
// Import des composants depuis le shared module
import { ProfilutilisateurComponent } from '../../shared/components/profilutilisateur/profilutilisateur.component';
import { ModificationprofilComponent } from '../../shared/components/modificationprofil/modificationprofil.component';
import { ModificationmotdepassComponent } from '../../shared/components/modificationmotdepass/modificationmotdepass.component';

const routes: Routes = [
  // La route de base /app/profil
  { path: '', component: ProfilutilisateurComponent },
  // La route /app/profil/edit
  { path: 'edit', component: ModificationprofilComponent },
  // La route /app/profil/change-password
  { path: 'change-password', component: ModificationmotdepassComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProfileRoutingModule { }
