import { Component, OnInit } from '@angular/core';
import { UserService, Utilisateur } from '../../../core/services/user.service'; // adapte le chemin

@Component({
  selector: 'app-gestionutilisateur',
  templateUrl: './gestionutilisateur.component.html',
  styleUrls: ['./gestionutilisateur.component.css']
})
export class GestionutilisateurComponent implements OnInit {
  afficherFormulaireUtilisateur = false;
  afficherListeUtilisateurs = true;

  rolesDisponibles = ['ADMIN', 'ETUDIANT', 'ENSEIGNANT', 'TUTEUR', 'TECHNOPEDAGOGUE', 'RESPONSABLE_FORMATION'];

  nouvelUtilisateur: Utilisateur = {
    nom: '',
    prenom: '',
    email: '',
    motDePasse: '',
    role: ''
  };

  utilisateurs: Utilisateur[] = [];

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.chargerUtilisateurs();
  }

  // Charger les utilisateurs depuis le backend
  chargerUtilisateurs(): void {
    this.userService.getUsers().subscribe({
      next: (data) => this.utilisateurs = data,
      error: (err) => console.error('Erreur lors du chargement des utilisateurs', err)
    });
  }

  // Ajouter un utilisateur
  ajouterUtilisateur(): void {
    this.userService.addUser(this.nouvelUtilisateur).subscribe({
      next: (user) => {
        this.utilisateurs.push(user);
        this.nouvelUtilisateur = { nom: '', prenom: '', email: '', motDePasse: '', role: '' };
        this.afficherFormulaireUtilisateur = false;
      },
      error: (err) => console.error('Erreur lors de l’ajout', err)
    });
  }

  // Modifier un utilisateur
  modifierUtilisateur(user: Utilisateur): void {
    if (!user.id) return; // id requis pour update
    this.userService.updateUser(user.id, user).subscribe({
      next: () => console.log('Utilisateur modifié', user.email),
      error: (err) => console.error('Erreur lors de la modification', err)
    });
  }

  // Supprimer un utilisateur
  supprimerUtilisateur(user: Utilisateur): void {
    if (!user.id) return;
    this.userService.deleteUser(user.id).subscribe({
      next: () => this.utilisateurs = this.utilisateurs.filter(u => u.id !== user.id),
      error: (err) => console.error('Erreur lors de la suppression', err)
    });
  }

  // Modifier uniquement le rôle
  modifierRoleUtilisateur(user: Utilisateur): void {
    this.modifierUtilisateur(user);
  }
}
