import { NgModule, Optional, SkipSelf } from '@angular/core';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router'; // <-- 1. IMPORTER ICI
import { SharedModule } from '../shared/shared.module';

// Importez les services, layouts et intercepteurs
import { AuthInterceptor } from '../interceptors/auth.interceptor';
import { AuthService } from '../services/auth.service';
import { ThemeService } from './services/theme.service';

import { PublicLayoutComponent } from './layouts/public-layout/public-layout.component';
import { PrivateLayoutComponent } from './layouts/private-layout/private-layout.component';
import { EtudiantLayoutComponent } from './layouts/etudiant-layout/etudiant-layout.component';

@NgModule({
  declarations: [
    PublicLayoutComponent,
    PrivateLayoutComponent,
    EtudiantLayoutComponent
  ],
  imports: [
    // Modules nécessaires pour que les layouts fonctionnent
    CommonModule,
    RouterModule, // <-- 2. AJOUTER ICI
    SharedModule
  ],
  exports: [
    PublicLayoutComponent,
    PrivateLayoutComponent,
    EtudiantLayoutComponent
  ],
  providers: [
    AuthService,
    ThemeService,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ]
} )
export class CoreModule {
  constructor(@Optional() @SkipSelf() parentModule: CoreModule) {
    if (parentModule) {
      throw new Error('CoreModule is already loaded. Import it in the AppModule only.');
    }
  }
}
