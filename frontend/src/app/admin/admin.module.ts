// Fichier : src/app/admin/admin.module.ts (Version refactorisée)

import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module'; // <-- 1. L'import le plus important
import { AdminRoutingModule } from './admin-routing.module'; // <-- 2. Notre nouveau routing

// Modules externes spécifiques à ce module (si nécessaire)
import { QuillModule } from 'ngx-quill'; // <-- On utilise UNIQUEMENT celui-ci

// --- 3. Importez TOUS les composants qui appartiennent à ce module ---

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
import { EnseignantModule } from '../features/enseignant/enseignant.module';
import { FormulaireInscriptionComponent } from './pages/formulaire-inscription/formulaire-inscription.component'; // <-- Importer le module
import { PermissionManagementComponent } from './permission-management/permission-management.component';
@NgModule({
  declarations: [
    // --- 4. Déclarez TOUS ces composants ici ---
    
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
  ],
  imports: [
    // --- 5. La liste d'imports est maintenant simple et propre ---
    SharedModule, // Fournit CommonModule, FormsModule, ReactiveFormsModule, etc.
    AdminRoutingModule,
    QuillModule, // Module externe utilisé par ce feature module
    EnseignantModule,
  ]
})
export class AdminModule { }
