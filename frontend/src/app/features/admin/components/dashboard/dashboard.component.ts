import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../../services/auth.service';

interface DashboardItem {
  title: string;
  description: string;
  icon: string;
  route: string;
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  public cards: DashboardItem[] = [];
  public isNotEtudiant: boolean = false;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    const role = this.authService.getUserRole();

    if (!role) {
      // Redirection si pas connecté
      this.router.navigate(['/auth/login']);
      return;
    }

    this.isNotEtudiant = role !== 'ETUDIANT';
    this.cards = this.getCardsByRole(role);

    // Redirection pour étudiants vers la page de matières
    if (role === 'ETUDIANT') {
      this.router.navigate(['/app/curriculum/matieres']);
    }
  }

  goTo(path: string): void {
    this.router.navigate([path]);
  }

  private getCardsByRole(role: string): DashboardItem[] {
    const allCards: Record<string, DashboardItem[]> = {
      ADMIN: [
        { title: 'Utilisateurs', description: 'Gérer les utilisateurs', icon: '👤', route: '/app/admin/dashboard' },
        { title: 'Rôles', description: 'Gérer les rôles', icon: '🛡️', route: '/app/admin/roles' },
        { title: 'Permissions', description: 'Définir les accès', icon: '🔐', route: '/app/admin/permissions' },
        { title: 'Utilisateurs', description: 'Gérer les utilisateurs', icon: '👤', route: '/app/admin/dashboard' },
        { title: 'Rôles', description: 'Gérer les rôles', icon: '🛡️', route: '/app/admin/roles' },
        { title: 'Permissions', description: 'Définir les accès', icon: '🔐', route: '/app/admin/permissions' },
      ],
      ENSEIGNANT: [
        { title: 'Cours', description: 'Gérer vos cours', icon: '📘', route: '/enseignant/dashboard' },
        { title: 'Parcours', description: 'Organiser les parcours', icon: '🗺️', route: '/enseignant/parcours' },
        { title: 'Évaluations', description: 'Gérer les tests et notes', icon: '📊', route: '/enseignant/tests' },
      ],

      // Extrait de getCardsByRole dans ton DashboardComponent
TUTEUR: [
  { 
    title: 'Suivi des Étudiants', 
    description: 'Visualiser les parcours et la progression des élèves', 
    icon: '👨‍🎓', 
    // Redirige vers StudentListComponent
    route: '/app/tuteur/listeetudiant' 
  },
  { 
    title: 'Gestion des Parcours', 
    description: 'Définir le type de parcours (Choisi, Recommandé, Mixte)', 
    icon: '🗺️', 
    // Redirige vers la liste (le tuteur choisit l'élève puis change son parcours)
    route: '/app/tuteur/student/:id' 
  },
  { 
    title: 'Banque d\'Évaluations', 
    description: 'Transformer vos questionnaires en tests interactifs', 
    icon: '📝', 
    // Redirige vers TestManagerComponent
    route: '/app/tuteur/tests' 
  }
]
,

      ETUDIANT: [] // pas de cartes
    };

    return allCards[role] || [];
  }
}
