import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TuteurRoutingModule } from './tuteur-routing.module';
import { SharedModule } from '../shared/shared.module';


@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    TuteurRoutingModule,
    SharedModule
  ]
})
export class TuteurModule { }
