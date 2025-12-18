// Fichier : src/app/features/enseignant/enseignant.module.ts

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgxPaginationModule } from 'ngx-pagination';
import { EnseignantRoutingModule } from './enseignant-routing.module';

// Modules partagés
import { SharedModule } from '../../shared/shared.module';
import { NgbModalModule } from '@ng-bootstrap/ng-bootstrap';
import { QuillModule } from 'ngx-quill';

// Composants du module Enseignant
import { GestionQuestionnaireComponent } from './components/gestion-questionnaire/gestion-questionnaire.component';
import { SelectionMatiereGestionComponent } from './components/selection-matiere-gestion/selection-matiere-gestion.component';
import { GestionContenuComponent } from './components/gestion-contenu/gestion-contenu.component';
import { FormChapitreComponent } from './components/form-chapitre/form-chapitre.component';
import { FormSectionComponent } from './components/form-section/form-section.component';
import { MesRessourcesComponent } from './components/mes-ressources/mes-ressources.component';
import { BanqueQuestionGestionComponent } from './components/banque-question-gestion/banque-question-gestion.component';
import { GestionRessourcesPedagogiquesComponent } from './components/gestion-ressources-pedagogiques/gestion-ressources-pedagogiques.component';
import { GestionFormationsComponent } from './components/gestion-formations/gestion-formations.component';
import { QuestionnaireListComponent } from './components/questionnaire-list-component/questionnaire-list-component.component';
import { ListeFormationsComponent } from './components/liste-formations/liste-formations.component';
import { MesCoursComponentComponent } from './components/mes-cours-component/mes-cours-component.component';
import { EvaluerEtudiantComponentComponent } from './components/evaluer-etudiant-component/evaluer-etudiant-component.component';
import { FormationFilterPipe } from '../../pipes/formation-filter.pipe';
import { QuestionnaireDetailsViewComponentComponent } from './components/questionnaire-details-view-component/questionnaire-details-view-component.component';
import { EtablissementComponent } from './components/etablissement/etablissement.component';
import { UefrComponent } from './components/uefr/uefr.component';
import { DepartementComponent } from './components/departement/departement.component';
@NgModule({
  
  declarations: [
    GestionQuestionnaireComponent,
    SelectionMatiereGestionComponent,
    GestionContenuComponent,
    FormChapitreComponent,
    FormSectionComponent,
    MesRessourcesComponent,
    BanqueQuestionGestionComponent,
    GestionRessourcesPedagogiquesComponent,
    GestionFormationsComponent,
    QuestionnaireListComponent,
    ListeFormationsComponent,
    MesCoursComponentComponent,
    EvaluerEtudiantComponentComponent,
    FormationFilterPipe,
    QuestionnaireDetailsViewComponentComponent,
    EtablissementComponent,
    UefrComponent,
    DepartementComponent,
  ],

  imports: [
    CommonModule,        // Obligatoire
    FormsModule,         // Obligatoire pour ngModel
    ReactiveFormsModule, // Obligatoire pour formGroup et formControlName
    RouterModule,
    
    SharedModule,
    EnseignantRoutingModule,
    NgbModalModule,
    QuillModule.forRoot(),
    [NgxPaginationModule],
  ],
})
export class EnseignantModule {} 
