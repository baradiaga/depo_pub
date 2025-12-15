import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

// --- 1. Importez tous les composants qui servent de "page" dans la section admin ---
import { DashboardComponent } from '../features/admin/components/dashboard/dashboard.component';
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
import { FormulaireInscriptionComponent } from './pages/formulaire-inscription/formulaire-inscription.component';
import { PermissionManagementComponent } from './permission-management/permission-management.component';
import { ValiderInscriptionComponent } from './pages/validerinscription/validerinscription.component';

// --- 2. Définissez les routes internes à la section admin ---
const routes: Routes = [
  // Note : Les URL sont relatives au chemin de chargement du module (ex: /admin)
  
  // Route principale du tableau de bord
  { path: 'dashboard', component: DashboardComponent }, // Sera accessible via /app/admin/dashboard

  // Routes pour la gestion des utilisateurs et des droits
  { path: 'users', component: GestionutilisateurComponent }, // ou UserListComponent si vous préférez
  { path: 'roles', component: GestionRolesComponent },
  { path: 'permissions', component: PermissionsComponent },
  { path: 'features', component: FonctionnalitesComponent },
  { path: 'feature-assignment', component: FeatureAssignmentComponent },
  { path: 'inscriptions', component: GestionDesInscriptionComponent },
  { path: 'inscriptions/nouveau',component: FormulaireInscriptionComponent },
  { path: 'inscriptions/modifier/:id', component: FormulaireInscriptionComponent},
  // Routes pour la gestion du curriculum et des contenus
  { path: 'parcours', component: GestionParcoursComponent },
  { path: 'unites-enseignement', component: GestionUnitesComponent },
  { path: 'elements-constitutifs', component: GestionElementConstitutifComponent },
  { path: 'chapitres', component: GestionChapitreComponent },
  { path: 'matieres', component: ListeMatieresComponent },
  { path: 'categories', component: CategorieComponent },
  { path: 'echelles', component: EchelleConnaissanceComponent },
  { path: 'parametrage-chapitre', component: ParametrageChapitreComponent },
  { path: 'permssionsManegement', component: PermissionManagementComponent},
  { path: 'validation-inscriptions', component: ValiderInscriptionComponent},

  // Route par défaut pour la section admin : redirige vers le tableau de bord
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
];

@NgModule({
  // Utilisez forChild() dans les modules de fonctionnalités (feature modules)
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
