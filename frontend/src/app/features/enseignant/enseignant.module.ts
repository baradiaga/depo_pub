import { NgModule } from '@angular/core';
import { EnseignantRoutingModule } from './enseignant-routing.module';

// --- 1. IMPORTEZ LES MODULES DONT CE MODULE A BESOIN ---
import { SharedModule } from '../../shared/shared.module'; // Fournit CommonModule, FormsModule, ReactiveFormsModule...
import { AngularEditorModule } from '@kolkov/angular-editor'; // Pour l'éditeur de texte

import { GestionQuestionnaireComponent } from './components/gestion-questionnaire/gestion-questionnaire.component';

@NgModule({
  declarations: [
    GestionQuestionnaireComponent,
  ],
  imports: [
    SharedModule,          
    EnseignantRoutingModule,
    AngularEditorModule    
  ],
  exports: [
    GestionQuestionnaireComponent ,// <-- Export pour qu'il soit utilisable ailleurs
  ]
})
export class EnseignantModule { }
