import { Component,OnInit } from '@angular/core';
import { Router } from '@angular/router'; 
import { FormBuilder, FormGroup } from '@angular/forms';


@Component({
  selector: 'app-modificationprofil',
  templateUrl: './modificationprofil.component.html',
  styleUrls: ['./modificationprofil.component.css']
})
export class ModificationprofilComponent implements OnInit{
  
profilForm!: FormGroup;

  constructor(private fb: FormBuilder, private router: Router) {}

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
      console.log('Profil mis à jour :', this.profilForm.value);
      alert('Profil mis à jour avec succès !');
      this.router.navigate(['/profilutilisateur']);
    }
  }
}
