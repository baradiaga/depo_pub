import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
@Component({
  selector: 'app-modificationmotdepass',
  templateUrl: './modificationmotdepass.component.html',
   styleUrls: ['./modificationmotdepass.component.css']
})
export class ModificationmotdepassComponent implements OnInit {
   passwordForm!: FormGroup;

  constructor(private fb: FormBuilder, private router: Router) {}

  ngOnInit(): void {
    this.passwordForm = this.fb.group({
      ancienMotDePasse: ['', Validators.required],
      nouveauMotDePasse: ['', [Validators.required, Validators.minLength(6)]],
      confirmationMotDePasse: ['', Validators.required]
    });
  }
   onSubmit(): void {
    if (this.passwordForm.invalid) {
      alert('Veuillez remplir tous les champs correctement.');
      return;
    }

    const { nouveauMotDePasse, confirmationMotDePasse } = this.passwordForm.value;

    if (nouveauMotDePasse !== confirmationMotDePasse) {
      alert('Les mots de passe ne correspondent pas.');
      return;
    }

    // Ici tu peux appeler un service pour changer le mot de passe
    console.log('Mot de passe modifié avec succès.');
    alert('Mot de passe modifié avec succès.');
    this.router.navigate(['/profilutilisateur']);
  }
  showOldPassword = false;
showNewPassword = false;
showConfirmPassword = false;

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
