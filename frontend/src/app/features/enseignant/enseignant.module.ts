// Fichier : src/app/features/enseignant/enseignant.module.ts (Version Finale avec ngx-quill)

import { NgModule } from '@angular/core';
import { EnseignantRoutingModule } from './enseignant-routing.module';

// --- 1. Importez les modules partagés et l'éditeur de texte UNIQUE ---
import { SharedModule } from '../../shared/shared.module'; 
import { NgbModalModule } from '@ng-bootstrap/ng-bootstrap';
import { QuillModule } from 'ngx-quill'; // <-- On utilise UNIQUEMENT celui-ci

// --- 2. Importez TOUS les composants qui appartiennent à ce module ---
import { GestionQuestionnaireComponent } from './components/gestion-questionnaire/gestion-questionnaire.component';
import { SelectionMatiereGestionComponent } from './components/selection-matiere-gestion/selection-matiere-gestion.component';
import { GestionContenuComponent } from './components/gestion-contenu/gestion-contenu.component';
import { FormChapitreComponent } from './components/form-chapitre/form-chapitre.component';
import { FormSectionComponent } from './components/form-section/form-section.component';
import { MesRessourcesComponent } from './components/mes-ressources/mes-ressources.component';

@NgModule({
  // --- 3. Déclarez TOUS les composants de ce module ---
  declarations: [
    GestionQuestionnaireComponent,
    SelectionMatiereGestionComponent,
    GestionContenuComponent,
    FormChapitreComponent,
    FormSectionComponent,
    MesRessourcesComponent,
     
  ],
  // --- 4. Importez les modules nécessaires ---
  imports: [
    SharedModule,          
    EnseignantRoutingModule,
    NgbModalModule,
    QuillModule.forRoot(),
   
  ],
})
export class EnseignantModule { }
