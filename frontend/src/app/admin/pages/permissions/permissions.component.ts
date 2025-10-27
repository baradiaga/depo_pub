import { Component, OnInit } from '@angular/core';

type Action = 'créer' | 'lire' | 'modifier' | 'supprimer' | 'lister';


@Component({
  selector: 'app-permissions',
  templateUrl: './permissions.component.html',
  styleUrls: ['./permissions.component.css']
})
export class PermissionsComponent implements OnInit {
  roles: string[] = [
    'ADMIN',
    'ETUDIANT',
    'ENSEIGNANT',
    'TUTEUR',
    'TECHNOPEDAGOGUE',
    'RESPONSABLE_FORMATION'
  ];

  tables: string[] = [
    'utilisateur',
    'roles',
    'permissions',
    'matieres',
    'syllabus',
    'tests',
    'resultats',
    'ressources',
    'sequences',
    'activites',
    'evaluations',
    'recommendations',
    'parcours'
  ];

  actions: Action[] = ['créer', 'lire', 'modifier', 'supprimer', 'lister'];



  // 🔁 Nouvelle structure de permissions
  permissions: {
    [role: string]: {
      [table: string]: {
        [action in Action]: boolean;
      };
    };
  } = {};

  selectedRole: string = '';

  ngOnInit(): void {
  this.roles.forEach(role => {
    this.permissions[role] = {};
    this.tables.forEach(table => {
      this.permissions[role][table] = {
        créer: role === 'ADMIN',
        lire: role === 'ADMIN',
        modifier: role === 'ADMIN',
        supprimer: role === 'ADMIN',
        lister: role === 'ADMIN'
      };
    });
  });
}


  togglePermission(role: string, table: string, action: Action): void {
    if (role !== 'ADMIN') {
      this.permissions[role][table][action] = !this.permissions[role][table][action];
    }
  }

  hasPermission(role: string, table: string, action: Action): boolean {
    return this.permissions[role][table][action];
  }

  isReadOnly(role: string): boolean {
    return role === 'ADMIN';
  }
}
