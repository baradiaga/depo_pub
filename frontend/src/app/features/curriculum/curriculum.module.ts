import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { CurriculumRoutingModule } from './curriculum-routing.module';

// --- 1. Importez les composants à déclarer ---
import { MatieresReprendreComponent } from './components/matieres-reprendre/matieres-reprendre.component';
import { SyllabusComponent } from './components/syllabus/syllabus.component';
import { SequencesComponent } from './components/sequences/sequences.component';
import { ActivitesComponent } from './components/activites/activites.component';
import { EvaluationsComponent } from './components/evaluations/evaluations.component';
import { StudentModule } from '../../features/student/student.module';
@NgModule({
  declarations: [
    // --- 2. Déclarez tous les composants ici ---
    MatieresReprendreComponent,
    SyllabusComponent,
    SequencesComponent,
    ActivitesComponent,
    EvaluationsComponent
  ],
  imports: [
    SharedModule,
    CurriculumRoutingModule,
    StudentModule,
  ]
})
export class CurriculumModule { }
