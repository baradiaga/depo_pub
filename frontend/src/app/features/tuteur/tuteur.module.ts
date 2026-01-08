import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TuteurRoutingModule } from './tuteur-routing.module';
import { StudentListComponent } from './components/student-list/student-list.component';
import { StudentDetailComponent } from './components/student-detail/student-detail.component';
import { TestManagerComponent } from './components/test-manager/test-manager.component';


@NgModule({
  declarations: [
    StudentListComponent,
    StudentDetailComponent,
    TestManagerComponent
  ],
  imports: [
    CommonModule,
    TuteurRoutingModule,
    FormsModule 
  ]
})
export class TuteurModule { }
