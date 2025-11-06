import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router'; 
import { AuthService } from '../../../../services/auth.service';
import { UserRole } from '../../../../models/user.model';

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
  
  // On peut déclarer les cartes directement ici
  public cards: DashboardItem[] = [];

  // On injecte les services nécessaires
  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    // LA LOGIQUE SIMPLIFIÉE EST ICI
    // 1. On récupère le rôle de manière synchrone.
    //    À ce stade, le AuthService devrait déjà savoir qui est l'utilisateur.
    const userRole = this.authService.getUserRole();

    // 2. On génère les cartes en fonction de ce rôle.
    this.cards = this.getCardsByRole(userRole);
  }

  goTo(path: string): void {
    this.router.navigate([path]);
  }

  // Votre méthode getCardsByRole reste inchangée, elle est parfaite.
  private getCardsByRole(role: UserRole | null): DashboardItem[] {
    if (!role) {
      return [];
    }
    const allCards: Partial<{ [key in NonNullable<UserRole>]: DashboardItem[] }> = {
      ADMIN: [
        { title: 'Utilisateurs', description: 'Gérer les utilisateurs', icon: '👤', route: 'app/admin/utilisateur' },
        { title: 'Rôles', description: 'Gérer les rôles', icon: '🛡️', route: '/admin/roles' },
        { title: 'Permissions', description: 'Définir les accès', icon: '🔐', route: 'app/admin/permission' },
        { title: 'Fonctionnalités', description: 'Gérer les fonctionnalités', icon: '⚙️', route: 'app/admin/fonctionnalites' },
        { title: 'Gestion des parcours', description: 'Gérer les parcours', icon: '🗺️', route: '/admin/parcours' },
        { title: 'Matières', description: 'Reprendre vos cours', icon: '📘', route: '/matieres' },
      ],
      ETUDIANT: [
        { title: 'Matières', description: 'Reprendre vos cours', icon: '📘', route: '/matieres' },
        { title: 'Parcours recommandé', description: 'Voir vos parcours', icon: '🧭', route: '/Parcourrecommende' },
        { title: 'Évaluations', description: 'Vos tests & résultats', icon: '📊', route: '/tests' }
      ],
      // ... (les autres rôles)
    };
    return allCards[role] || [];
  }
}
