// frontend/src/app/features/profile/profile.module.ts

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ProfileRoutingModule } from './profile-routing.module';
import { SharedModule } from '../../shared/shared.module'; // Importé pour les autres dépendances

@NgModule({
  declarations: [
    
  ],
  imports: [
    SharedModule, // Ajout de SharedModule pour les directives/pipes/modules qu'il exporte
    CommonModule,
    ProfileRoutingModule
  ]
})
export class ProfileModule { }
