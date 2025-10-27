import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';

// --- 1. Importez les modules principaux de votre application ---
import { CoreModule } from './core/core.module'; // <-- NOTRE NOUVEAU COREMODULE
import { SharedModule } from './shared/shared.module'; // <-- NOTRE NOUVEAU SHAREDMODULE
import { AppRoutingModule } from './app-routing.module';

// --- 2. Importez les modules externes (bibliothèques tierces ) ---
import { ToastrModule } from 'ngx-toastr';
import { AngularEditorModule } from '@kolkov/angular-editor';
// --- 3. Importez uniquement le composant racine de l'application ---
import { AppComponent } from './app.component';



@NgModule({
  declarations: [
    // AppModule ne déclare plus que le composant racine AppComponent.
    AppComponent
  ],
  imports: [
    // --- 4. Modules à importer UNE SEULE FOIS dans l'application ---
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    CoreModule,

    // --- 5. Modules de fonctionnalités et de routing ---
    AppRoutingModule, // Doit venir après CoreModule si des routes utilisent ses layouts
    SharedModule,     // SharedModule peut être importé ici et dans les autres modules

    // --- 6. Configuration des modules externes ---
    AngularEditorModule,
    ToastrModule.forRoot({
      timeOut: 5000,
      positionClass: 'toast-top-right',
      preventDuplicates: true,
    }),
  ],
  providers: [
    // Le tableau des providers est maintenant VIDE.
    // L'intercepteur est fourni par le CoreModule.
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
