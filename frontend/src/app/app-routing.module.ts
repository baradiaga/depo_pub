import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

// --- 1. Importez uniquement les Layouts et le Guard ---
import { PublicLayoutComponent } from './core/layouts/public-layout/public-layout.component';
import { PrivateLayoutComponent } from './core/layouts/private-layout/private-layout.component';
import { AuthGuard } from './auth.guard'; // <-- Assurez-vous que c'est bien 'AuthGuard' avec une majuscule
import { PageNotFoundComponent } from './shared/components/page-not-found/page-not-found.component';

const routes: Routes = [
  // --- Routes publiques (connexion) ---
  {
    path: 'auth', // On préfixe les routes d'authentification
    component: PublicLayoutComponent,
    loadChildren: () => import('./features/authentication/auth.module').then(m => m.AuthModule)
  },

  // --- Routes privées (le cœur de l'application) ---
  {
    path: 'app',
    component: PrivateLayoutComponent,
    canActivate: [AuthGuard], // Le guard protège TOUTES les routes enfants de /app
    children: [
      // --- CHARGEMENT DE TOUS LES FEATURE MODULES ---

      {
        path: 'admin',
        loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule),
        canActivate: [AuthGuard], // Double sécurité pour la section admin
        data: { roles: ['ADMIN', 'RESPONSABLE_FORMATION','ENSEIGNANT'] }
      },
      // Dans app-routing.module.ts, dans les children de la route '/app'
{
  path: 'student',
  loadChildren: () => import('./features/student/student.module').then(m => m.StudentModule),
  canActivate: [AuthGuard],
  data: { roles: ['ETUDIANT', 'ADMIN'] } // L'admin peut aussi voir les pages étudiant
},

      {
        path: 'enseignant',
        loadChildren: () => import('./features/enseignant/enseignant.module').then(m => m.EnseignantModule),
        canActivate: [AuthGuard],
        data: { roles: ['ENSEIGNANT', 'ADMIN'] }
      },
      {
        path: 'tuteur',
        loadChildren: () => import('./features/tuteur/tuteur.module').then(m => m.TuteurModule),
        canActivate: [AuthGuard],
        data: { roles: ['TUTEUR'] }
      },
      {
        path: 'technopedagogue',
        loadChildren: () => import('./features/technopedagogue/technopedagogue.module').then(m => m.TechnopedagogueModule),
        canActivate: [AuthGuard],
        data: { roles: ['TECHNOPEDAGOGUE'] }
      },
      {
        path: 'curriculum', // <-- LA ROUTE QUI NOUS INTÉRESSE
        loadChildren: () => import('./features/curriculum/curriculum.module').then(m => m.CurriculumModule),
        canActivate: [AuthGuard],
        // Ce module est accessible par plusieurs rôles
        data: { roles: ['ETUDIANT', 'ENSEIGNANT', 'TUTEUR', 'TECHNOPEDAGOGUE', 'ADMIN'] }
      },
      {
        path: 'profil',
        // Ici, vous pouvez charger un module de profil ou pointer vers des composants
        // Pour l'instant, on suppose qu'il n'y a pas de rôle requis, juste être connecté
        loadChildren: () => import('./features/profile/profile.module').then(m => m.ProfileModule),
        canActivate: [AuthGuard]
      },

      // Redirection par défaut pour la section 'app'.
      // Si un utilisateur arrive sur /app sans plus de précision, il est redirigé vers sa page d'accueil.
      // La logique est dans le AuthGuard et le AuthService, donc une redirection simple suffit.
      { path: '', redirectTo: 'profil', pathMatch: 'full' },
    ]
  },

  // --- Redirections globales ---
  { path: '', redirectTo: '/auth/login', pathMatch: 'full' }, // Si on arrive à la racine, on va vers le login
  { path: '**', component: PageNotFoundComponent }
 // Toute autre route inconnue redirige vers cette
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
