// src/app/shared/shared.module.ts
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

// Composants partagés
import { NavbarComponent } from './components/navbar/navbar.component';
import { FooterComponent } from './components/footer/footer.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { ProfilutilisateurComponent } from './components/profilutilisateur/profilutilisateur.component';
import { ModificationmotdepassComponent } from './components/modificationmotdepass/modificationmotdepass.component';
import { ModificationprofilComponent } from './components/modificationprofil/modificationprofil.component';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';

// Directives partagées
import { DashboardDirective } from './directives/dashboard.directive';

@NgModule({
  declarations: [
    NavbarComponent,
    FooterComponent,
    SidebarComponent,
    ProfilutilisateurComponent,
    ModificationmotdepassComponent,
    ModificationprofilComponent,
    DashboardDirective,
    PageNotFoundComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule
  ],
  exports: [
    NavbarComponent,
    FooterComponent,
    SidebarComponent,
    ProfilutilisateurComponent,
    ModificationmotdepassComponent,
    ModificationprofilComponent,
    PageNotFoundComponent,
    DashboardDirective,
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule
  ]
})
export class SharedModule {}
