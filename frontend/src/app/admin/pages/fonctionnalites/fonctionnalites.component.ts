import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { FonctionnaliteAdminService } from '../services/fonctionnalite-admin.service';
import { Fonctionnalite } from '../../../models/fonctionnalite.model'; // Adaptez si votre dossier 'models' est ailleurs

@Component({
  selector: 'app-fonctionnalites',
  templateUrl: './fonctionnalites.component.html',
  styleUrls: ['./fonctionnalites.component.css']
})
export class FonctionnalitesComponent implements OnInit, OnDestroy {

  // --- État général ---
  currentView: 'gestion' | 'permissions' = 'gestion'; // <-- CONSERVÉ
  private subs = new Subscription();

  // --- Données pour la vue CRUD ---
  allFonctionnalites: Fonctionnalite[] = [];

  // --- État pour le formulaire de GESTION (CRUD) ---
  isFormVisible = false;
  editingFonctionnalite: Fonctionnalite | null = null;

  constructor(private adminService: FonctionnaliteAdminService) {}

  ngOnInit(): void {
    this.adminService.loadAllAdminData();

    // S'abonner uniquement aux fonctionnalités pour la vue CRUD
    this.subs.add(this.adminService.fonctionnalites$.subscribe(data => this.allFonctionnalites = data));
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  // --- Méthodes pour le mode GESTION (CRUD) ---

  startNewFonctionnalite(): void {
    this.editingFonctionnalite = { nom: '', featureKey: '', icon: '', sousFonctionnalites: [] };
    this.isFormVisible = true;
  }

  startEditFonctionnalite(fonctionnalite: Fonctionnalite): void {
    // Crée une copie profonde pour éviter de modifier l'original avant la sauvegarde
    this.editingFonctionnalite = JSON.parse(JSON.stringify(fonctionnalite));
    this.isFormVisible = true;
  }

  saveFonctionnalite(): void {
    if (!this.editingFonctionnalite) return;

    if (this.editingFonctionnalite.id) { // Mise à jour
      this.subs.add(this.adminService.updateFonctionnalite(this.editingFonctionnalite.id, this.editingFonctionnalite).subscribe(() => this.closeForm()));
    } else { // Création
      const { id, ...newFonctionnalite } = this.editingFonctionnalite;
      this.subs.add(this.adminService.createFonctionnalite(newFonctionnalite).subscribe(() => this.closeForm()));
    }
  }

  deleteFonctionnalite(id: number | undefined): void {
    if (id === undefined) return;
    if (confirm('Êtes-vous sûr de vouloir supprimer cette fonctionnalité et tous ses sous-menus ?')) {
      this.subs.add(this.adminService.deleteFonctionnalite(id).subscribe());
    }
  }

  addSubFeature(): void {
    if (!this.editingFonctionnalite) return;
    this.editingFonctionnalite.sousFonctionnalites.push({ label: '', featureKey: '', route: '' });
  }

  removeSubFeature(index: number): void {
    if (!this.editingFonctionnalite) return;
    this.editingFonctionnalite.sousFonctionnalites.splice(index, 1);
  }

  closeForm(): void {
    this.isFormVisible = false;
    this.editingFonctionnalite = null;
  }
}
