import { Component, OnInit, OnDestroy } from '@angular/core'; // Importer OnDestroy
import { Router } from '@angular/router';
import { AuthService } from '../../../../services/auth.service';
import { UserRole } from '../../../../models/user.model';
import { Subscription } from 'rxjs'; // Importer Subscription

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
export class DashboardComponent implements OnInit, OnDestroy { // Implémenter OnDestroy
  role: UserRole | null = null;
  cards: DashboardItem[] = [];
  
  // Variable pour stocker notre souscription
  private authSubscription: Subscription | undefined;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    // AU LIEU de lire une seule fois, nous nous abonnons aux changements.
    // isAuthenticated$ est un bon déclencheur, car il est mis à jour après la connexion.
    this.authSubscription = this.authService.isAuthenticated$.subscribe(isAuthenticated => {
      if (isAuthenticated) {
        // Si l'utilisateur est authentifié, nous récupérons le rôle.
        // À ce stade, nous sommes sûrs que le localStorage est à jour.
        this.role = this.authService.getUserRole();
        this.cards = this.getCardsByRole(this.role);
      } else {
        // Si l'utilisateur se déconnecte, on vide les cartes.
        this.role = null;
        this.cards = [];
      }
    });
  }

  // Bonne pratique : se désabonner pour éviter les fuites de mémoire
  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }

  goTo(path: string): void {
    this.router.navigate([path]);
  }

  // Votre méthode getCardsByRole est parfaite, pas besoin de la changer.
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
      ],
      ETUDIANT: [
        { title: 'Matières', description: 'Reprendre vos cours', icon: '📘', route: '/matieres' },
        { title: 'Parcours recommandé', description: 'Voir vos parcours', icon: '🧭', route: '/Parcourrecommende' },
        { title: 'Évaluations', description: 'Vos tests & résultats', icon: '📊', route: '/tests' }
      ],
      ENSEIGNANT: [
        { title: 'Séquences', description: 'Créer & gérer les séquences', icon: '📺', route: '/sequence' },
        { title: 'Activités', description: 'Concevoir des activités', icon: '📝', route: '/activites' }
      ],
      TUTEUR: [
        { title: 'Suivi étudiants', description: 'Accompagner les étudiants', icon: '👥', route: '/suivi' }
      ],
      TECHNOPEDAGOGUE: [
        { title: 'Ressources pédagogiques', description: 'Publier & gérer les contenus', icon: '📚', route: '/ressources' }
      ],
      RESPONSABLE_FORMATION: [
        { title: 'Reporting', description: 'Suivi des performances', icon: '📈', route: '/reporting' }
      ]
    };
    return allCards[role] || [];
  }
}
