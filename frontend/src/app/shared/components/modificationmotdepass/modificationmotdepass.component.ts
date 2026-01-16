import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-modificationmotdepass',
  templateUrl: './modificationmotdepass.component.html',
  styleUrls: ['./modificationmotdepass.component.css']
})
export class ModificationmotdepassComponent implements OnInit {
  passwordForm!: FormGroup;
  
  // Variables pour la visibilité du mot de passe
  showOldPassword = false;
  showNewPassword = false;
  showConfirmPassword = false;

  // CORRECTION : Ajouter 'private authService: AuthService' dans le constructeur
  constructor(
    private fb: FormBuilder, 
    private router: Router,
    private authService: AuthService 
  ) {}

  ngOnInit(): void {
    this.passwordForm = this.fb.group({
      ancienMotDePasse: ['', Validators.required],
      nouveauMotDePasse: ['', [Validators.required, Validators.minLength(6)]],
      confirmationMotDePasse: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.passwordForm.invalid) {
      return;
    }

    const { ancienMotDePasse, nouveauMotDePasse, confirmationMotDePasse } = this.passwordForm.value;

    if (nouveauMotDePasse !== confirmationMotDePasse) {
      alert('Les mots de passe ne correspondent pas.');
      return;
    }

    const updateData = {
      nom: localStorage.getItem('user_nom'),
      prenom: localStorage.getItem('user_prenom'),
      email: localStorage.getItem('user_email'),
      motDePasseActuel: ancienMotDePasse,
      nouveauMotDePasse: nouveauMotDePasse
    };

    this.authService.updateProfile(updateData).subscribe({
      next: () => {
        alert('Mot de passe modifié avec succès.');
        this.router.navigate(['/app/profil']);
      },
      error: (err: any) => {
        alert('Erreur : ' + (err.error?.message || 'Une erreur est survenue'));
      }
    });
  }

  togglePasswordVisibility(field: string): void {
    if (field === 'old') {
      this.showOldPassword = !this.showOldPassword;
    } else if (field === 'new') {
      this.showNewPassword = !this.showNewPassword;
    } else if (field === 'confirm') {
      this.showConfirmPassword = !this.showConfirmPassword;
    }
  }
}
