// Fichier : src/app/pages/profilutilisateur/profilutilisateur.component.ts

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService, UserProfile } from '../../../services/user.service'; // <-- Importer le service et l'interface

@Component({
  selector: 'app-profilutilisateur',
  templateUrl: './profilutilisateur.component.html',
  styleUrls: ['./profilutilisateur.component.css']  
})
export class ProfilutilisateurComponent implements OnInit {  
  
  // On initialise user à null. Il sera rempli par l'appel API.
  public user: UserProfile | null = null;
  public isLoading = true; // Pour afficher un message de chargement
  public errorMessage: string | null = null;

  // On injecte le Router ET notre nouveau UserService
  constructor(
    private router: Router,
    private userService: UserService 
  ) {}

  /**
   * À l'initialisation du composant, on lance la récupération des données.
   */
  ngOnInit(): void {
    this.loadUserProfile();
  }

  loadUserProfile(): void {
    this.isLoading = true;
    this.errorMessage = null;

    // On s'abonne à la méthode du service
    this.userService.getCurrentUserProfile().subscribe({
      // En cas de succès :
      next: (profileData) => {
        this.user = profileData; // On assigne les données reçues à notre variable 'user'
        this.isLoading = false; // On arrête le chargement
      },
      // En cas d'erreur (ex: token expiré) :
      error: (err) => {
        console.error("Erreur lors de la récupération du profil :", err);
        this.errorMessage = "Impossible de charger les informations du profil. Veuillez vous reconnecter.";
        this.isLoading = false; // On arrête le chargement
      }
    });
  }

  onEdit(): void {
    this.router.navigate(['/app/profil/edit']);
  }
}
