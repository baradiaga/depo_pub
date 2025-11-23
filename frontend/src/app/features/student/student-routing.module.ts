// Fichier : src/app/features/student/student-routing.module.ts

import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

// --- Vos composants ---
import { TestsListComponent } from './components/tests-list/tests-list.component';
import { RecommendationComponent } from './components/recommendation/recommendation.component';
import { ParcoursComponent } from './components/parcours/parcours.component';
import { ChapitreDetailComponent } from './components/chapitre-detail/chapitre-detail.component';
import { ResultatsComponent } from './components/resultats/resultats.component';
import { PasserTestConnaissanceComponent } from './components/passer-test-connaissance/passer-test-connaissance.component';
// On importe le nouveau composant de sélection
import { SelectionTestConnaissanceComponent } from './components/selection-test-connaissance/selection-test-connaissance.component';
import { DeroulementTestComponent } from './components/deroulement-test/deroulement-test.component';
import { ResultatDiagnosticComponent } from './components/resultat-diagnostic/resultat-diagnostic.component';
import { VueCoursComponent } from './components/vue-cours/vue-cours.component';
const routes: Routes = [
  // --- Vos routes existantes ---
  { path: 'test/passer/:chapitreId', component: TestsListComponent },
  { path: 'recommendation', component: RecommendationComponent },
  { path: 'parcours', component: ParcoursComponent },
  { path: 'chapitre-detail/:id', component: ChapitreDetailComponent },
  { path: 'mes-resultats', component: ResultatsComponent },
  
  // ====================================================================
  // === NOUVELLES ROUTES POUR LE TEST DE CONNAISSANCE                ===
  // ====================================================================
  // 1. La page pour SÉLECTIONNER la matière
  { path: 'test-connaissance', component: SelectionTestConnaissanceComponent }, // <-- On pointe vers le nouveau composant
  
  // 2. La page pour PASSER le test
  { path: 'test-connaissance/passer/:matiereId', component: PasserTestConnaissanceComponent },
  { path: 'dashboard/test-connaissance/:id', component: DeroulementTestComponent },
  {path: 'resultat-diagnostic',component: ResultatDiagnosticComponent },
  {path: 'cours/:id', component: VueCoursComponent },

  // --- La route par défaut ---
  { path: '', redirectTo: 'parcours', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class StudentRoutingModule { }
