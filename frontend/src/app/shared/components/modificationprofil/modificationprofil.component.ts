import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router'; 
import { FormBuilder, FormGroup } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-modificationprofil',
  templateUrl: './modificationprofil.component.html',
  styleUrls: ['./modificationprofil.component.css']
})
export class ModificationprofilComponent implements OnInit {
  
  profilForm!: FormGroup;

  // CORRECTION 1 : Injection de AuthService dans le constructeur
  constructor(
    private fb: FormBuilder, 
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Simule les données actuelles de l'utilisateur
    this.profilForm = this.fb.group({
      nom: ['Diaga'],
      prenom: ['Bara'],
      email: ['bara@example.com']
    });
  }

  onSubmit(): void {
    if (this.profilForm.valid) {
      this.authService.updateProfile(this.profilForm.value).subscribe({
        next: () => {
          alert('Profil mis à jour avec succès !');
          this.router.navigate(['/app/profil']);
        },
        // CORRECTION 2 : Ajout du type ': any' pour le paramètre err
        error: (err: any) => alert('Erreur : ' + (err.error?.message || 'Une erreur est survenue'))
      });
    }
  }
}
