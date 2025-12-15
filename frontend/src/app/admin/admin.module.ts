import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { AdminRoutingModule } from './admin-routing.module';
import { CommonModule, DatePipe } from '@angular/common';
import { QuillModule } from 'ngx-quill';

// --- Composants importés ---
import { UserListComponent } from '../features/admin/components/user-list/user-list.component';
import { GestionRolesComponent } from './pages/gestion-roles/gestion-roles.component';
import { PermissionsComponent } from './pages/permissions/permissions.component';
import { GestionutilisateurComponent } from './pages/gestionutilisateur/gestionutilisateur.component';
import { FonctionnalitesComponent } from './pages/fonctionnalites/fonctionnalites.component';
import { FeatureAssignmentComponent } from './pages/feature-assignment/feature-assignment.component';
import { GestionParcoursComponent } from './pages/gestion-parcours/gestion-parcours.component';
import { EchelleConnaissanceComponent } from './pages/echelle-connaissance/echelle-connaissance.component';
import { CategorieComponent } from './pages/categorie/categorie.component';
import { GestionElementConstitutifComponent } from './pages/gestion-element-constitutif/gestion-element-constitutif.component';
import { GestionChapitreComponent } from './pages/gestion-chapitre/gestion-chapitre.component';
import { GestionUnitesComponent } from './pages/gestion-unites/gestion-unites.component';
import { ParametrageChapitreComponent } from './pages/parametrage-chapitre/parametrage-chapitre.component';
import { ListeMatieresComponent } from './pages/liste-matieres/liste-matieres.component';
import { GestionDesInscriptionComponent } from './pages/gestiondesinscription/gestiondesinscription.component';
//import { EnseignantModule } from '../features/enseignant/enseignant.module';
import { FormulaireInscriptionComponent } from './pages/formulaire-inscription/formulaire-inscription.component';
import { PermissionManagementComponent } from './permission-management/permission-management.component';
import { ValiderInscriptionComponent } from './pages/validerinscription/validerinscription.component';
import { StudentDetailComponent } from './pages/student-detail/student-detail.component';

@NgModule({
  declarations: [
    UserListComponent,
    GestionRolesComponent,
    PermissionsComponent,
    GestionutilisateurComponent,
    FonctionnalitesComponent,
    FeatureAssignmentComponent,
    GestionParcoursComponent,
    EchelleConnaissanceComponent,
    CategorieComponent,
    GestionElementConstitutifComponent,
    GestionChapitreComponent,
    GestionUnitesComponent,
    ParametrageChapitreComponent,
    ListeMatieresComponent,
    GestionDesInscriptionComponent,
    FormulaireInscriptionComponent,
    PermissionManagementComponent,
    ValiderInscriptionComponent,
    StudentDetailComponent,
  ],
  imports: [
    SharedModule,
    AdminRoutingModule,
    QuillModule,
   // EnseignantModule,
    CommonModule,
  ],
  providers: [
    DatePipe   // <-- Ajoute ceci
  ]
})
export class AdminModule { }
