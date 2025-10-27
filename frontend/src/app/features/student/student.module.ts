import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module'; // Pour ngFor, ngIf, FormsModule, etc.
import { StudentRoutingModule } from './student-routing.module';

// --- 1. Import de tous les composants à déclarer ---
import { ChapitreDetailComponent } from './components/chapitre-detail/chapitre-detail.component';
import { ParcoursComponent } from './components/parcours/parcours.component';
import { RecommendationComponent } from './components/recommendation/recommendation.component';
import { ResultatsComponent } from './components/resultats/resultats.component';
import { TestdeconnaissanceComponent } from './components/testdeconnaissance/testdeconnaissance.component';
import { TestsListComponent } from './components/tests-list/tests-list.component';

@NgModule({
  declarations: [
    // --- 2. Déclaration de tous les composants de ce module ---
    ChapitreDetailComponent,
    ParcoursComponent,
    RecommendationComponent,
    ResultatsComponent,
    TestdeconnaissanceComponent,
    TestsListComponent
  ],
  imports: [
    SharedModule,
    StudentRoutingModule
  ]
})
export class StudentModule { }
