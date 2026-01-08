import { Component, OnInit } from '@angular/core';
import { UserService, Utilisateur, UserRole } from '../../../core/services/user.service';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-gestionutilisateur',
  templateUrl: './gestionutilisateur.component.html',
  styleUrls: ['./gestionutilisateur.component.css']
})
export class GestionutilisateurComponent implements OnInit {
  // UI States
  afficherFormulaireUtilisateur = false;
  isLoading = false;
  searchQuery = new Subject<string>();
  
  // Data
  utilisateurs: Utilisateur[] = [];
  stats: any = null;
  rolesDisponibles = Object.values(UserRole);

  nouvelUtilisateur: Utilisateur = this.resetUser();

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.chargerUtilisateurs();
    this.chargerStats();
    this.initSearch();
  }

  // --- LOGIQUE DE DONNÉES ---

  chargerUtilisateurs(): void {
    this.isLoading = true;
    this.userService.getUsers().subscribe({
      next: (data) => {
        this.utilisateurs = data;
        this.isLoading = false;
      },
      error: (err) => this.handleError('Erreur chargement', err)
    });
  }

  chargerStats(): void {
    this.userService.getStats().subscribe(s => this.stats = s);
  }

  private initSearch(): void {
    this.searchQuery.pipe(
      debounceTime(400), // Attendre 400ms après la frappe
      distinctUntilChanged(),
      switchMap(query => this.userService.searchUsers(query))
    ).subscribe(results => this.utilisateurs = results);
  }

  // --- ACTIONS ---

  ajouterUtilisateur(): void {
    this.userService.addUser(this.nouvelUtilisateur).subscribe({
      next: (res) => {
        // Le backend renvoie { message: "...", user: ... }
        this.utilisateurs.unshift(res.user); 
        this.nouvelUtilisateur = this.resetUser();
        this.afficherFormulaireUtilisateur = false;
        this.chargerStats(); // Mettre à jour les compteurs
      },
      error: (err) => this.handleError('Erreur création', err)
    });
  }

  toggleStatus(user: Utilisateur): void {
    if (!user.id) return;
    const action = user.enabled ? 
      this.userService.deactivateUser(user.id) : 
      this.userService.activateUser(user.id);

    action.subscribe({
      next: () => {
        user.enabled = !user.enabled;
        this.chargerStats();
      },
      error: (err) => this.handleError('Erreur statut', err)
    });
  }

  supprimerUtilisateur(id: number): void {
    if (confirm('Supprimer définitivement cet utilisateur ?')) {
      this.userService.deleteUser(id).subscribe({
        next: () => {
          this.utilisateurs = this.utilisateurs.filter(u => u.id !== id);
          this.chargerStats();
        }
      });
    }
  }

  // --- HELPERS ---

  private resetUser(): Utilisateur {
    return { nom: '', prenom: '', email: '', motDePasse: '', role: UserRole.ETUDIANT };
  }

  private handleError(message: string, err: any) {
    this.isLoading = false;
    console.error(message, err);
    alert(err.error?.message || "Une erreur est survenue");
  }

  onSearch(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.searchQuery.next(value);
  }
}
