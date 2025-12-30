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
import { MatieresListeComponent } from './components/matieres-liste/matieres-liste.component';
import { ExercicesListeComponent } from './components/exercices-liste/exercices-liste.component';
import { RessourcesListeComponent } from './components/ressources-liste/ressources-liste.component';
import { EvaluationsListeComponent } from './components/evaluations-liste/evaluations-liste.component';
import { ApprentissageCoursComponent } from './components/apprentissage-cours-component/apprentissage-cours-component.component';
@NgModule({
  declarations: [
    // --- 2. Déclarez tous les composants ici ---
    MatieresReprendreComponent,
    SyllabusComponent,
    SequencesComponent,
    ActivitesComponent,
    EvaluationsComponent,
    MatieresListeComponent,
    ExercicesListeComponent,
    RessourcesListeComponent,
    EvaluationsListeComponent,
    ApprentissageCoursComponent,
    
  ],
  imports: [
    SharedModule,
    CurriculumRoutingModule,
    StudentModule,
  ]
})
export class CurriculumModule { }
