// src/app/shared/shared.module.ts
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

// Composants partagés
import { DashboardComponent } from '../features/admin/components/dashboard/dashboard.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { FooterComponent } from './components/footer/footer.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { ProfilutilisateurComponent } from './components/profilutilisateur/profilutilisateur.component';
import { ModificationmotdepassComponent } from './components/modificationmotdepass/modificationmotdepass.component';
import { ModificationprofilComponent } from './components/modificationprofil/modificationprofil.component';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';


// Directives partagées
import { DashboardDirective } from './directives/dashboard.directive';
import { SafeUrlPipe } from './pipes/safe-url.pipe';
import { VueCoursComponent } from '../features/student/components/vue-cours/vue-cours.component';

@NgModule({
  declarations: [
    DashboardComponent,
    NavbarComponent,
    FooterComponent,
    SidebarComponent,
    ProfilutilisateurComponent,
    ModificationmotdepassComponent,
    ModificationprofilComponent,
    DashboardDirective,
    PageNotFoundComponent,
    SafeUrlPipe,
    VueCoursComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule
  ],
  exports: [
    DashboardComponent,
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
    ReactiveFormsModule,
    SafeUrlPipe,
  ]
})
export class SharedModule {}
