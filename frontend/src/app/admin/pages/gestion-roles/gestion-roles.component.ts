import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-gestion-roles',
  templateUrl: './gestion-roles.component.html',
  styleUrls: ['./gestion-roles.component.css']
})
export class GestionRolesComponent implements OnInit {
  roles: string[] = ['ADMIN', 'ETUDIANT', 'ENSEIGNANT'];
  nouveauRole: string = '';
  modeEdition: boolean = false;
  indexEdition: number | null = null;

  utilisateurs: any[] = [
    { nom: 'Diop', prenom: 'Awa', email: 'awa@example.com', role: 'ETUDIANT', motDePasse: 'azerty123' }
  ];

  nouvelUtilisateur = {
    nom: '',
    prenom: '',
    email: '',
    role: '',
    motDePasse: 'azerty123'
  };

  afficherFormulaireUtilisateur: boolean = false;
  afficherListeUtilisateurs: boolean = true;

  get rolesDisponibles(): string[] {
    return this.roles;
  }

  ngOnInit(): void {}

  ajouterRole(): void {
    const nom = this.nouveauRole.trim().toUpperCase();
    if (!nom) return;
    if (this.roles.includes(nom)) {
      alert('Ce rôle existe déjà.');
      return;
    }
    this.roles.push(nom);
    this.nouveauRole = '';
  }

  modifierRole(role: string, index: string): void {
    this.nouveauRole = role;
    this.modeEdition = true;
    this.indexEdition = parseInt(index, 10);
  }

  enregistrerModification(): void {
    const nom = this.nouveauRole.trim().toUpperCase();
    if (!nom || this.indexEdition === null) return;
    if (this.roles.includes(nom) && this.roles[this.indexEdition] !== nom) {
      alert('Ce rôle existe déjà.');
      return;
    }
    this.roles[this.indexEdition] = nom;
    this.nouveauRole = '';
    this.modeEdition = false;
    this.indexEdition = null;
  }

  annulerEdition(): void {
    this.modeEdition = false;
    this.nouveauRole = '';
    this.indexEdition = null;
  }

  confirmerSuppression(index: number): void {
    if (confirm('Voulez-vous vraiment supprimer ce rôle ?')) {
      const roleSupprimé = this.roles[index];
      this.roles.splice(index, 1);
      this.utilisateurs.forEach(user => {
        if (user.role === roleSupprimé) {
          user.role = '';
        }
      });
    }
  }

  ajouterUtilisateur(): void {
    const { nom, prenom, email, role, motDePasse } = this.nouvelUtilisateur;
    if (!nom || !prenom || !email || !role || !motDePasse) {
      alert('Veuillez remplir tous les champs.');
      return;
    }

    const existe = this.utilisateurs.some(u => u.email === email);
    if (existe) {
      alert('Un utilisateur avec cet email existe déjà.');
      return;
    }

    this.utilisateurs.push({ nom, prenom, email, role, motDePasse });

    this.nouvelUtilisateur = {
      nom: '',
      prenom: '',
      email: '',
      role: '',
      motDePasse: 'azerty123'
    };

    this.afficherFormulaireUtilisateur = false;
    this.afficherListeUtilisateurs = false;
  }

  modifierRoleUtilisateur(utilisateur: any): void {
    console.log(`Rôle modifié : ${utilisateur.nom} → ${utilisateur.role}`);
  }
}
