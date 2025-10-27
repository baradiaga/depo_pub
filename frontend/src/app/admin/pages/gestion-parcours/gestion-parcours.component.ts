import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

interface Etudiant {
  nomComplet: string;
  email: string;
  numero: string;
  niveau: string;
  classe: string;
}

interface Chapitre {
  titre: string;
  etudiants: Etudiant[];
}

interface Matiere {
  nom: string;
  chapitres: Chapitre[];
}

interface Parcours {
  id: number;
  type: 'RECOMMANDE' | 'CHOISI' | 'MIXTE';
  titre: string;
  description: string;
  dateCreation: string;
  matieres: Matiere[];
}

@Component({
  selector: 'app-gestion-parcours',
  templateUrl: './gestion-parcours.component.html',
  styleUrls: ['./gestion-parcours.component.css']
})
export class GestionParcoursComponent implements OnInit {
  parcoursList: Parcours[] = [];
  filteredParcours: Parcours[] = [];
  typeParcours: 'RECOMMANDE' | 'CHOISI' | 'MIXTE' | '' = '';

  
  showEtudiants: { [matiereNom: string]: boolean } = {};

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    // Simulation de données
    this.parcoursList = [ {
    id: 1,
    type: 'RECOMMANDE',
    titre: 'Parcours Intelligence Artificielle',
    description: 'Parcours recommandé par le système basé sur l’analyse des résultats.',
    dateCreation: '2025-05-15',
    matieres: [
      {
        nom: 'Machine Learning',
        chapitres: [
          {
            titre: 'Introduction au ML',
            etudiants: [
              {
                nomComplet: 'Fatou Ndiaye',
                email: 'fatou.ndiaye@example.com',
                numero: 'ETU100',
                niveau: 'Master 1',
                classe: 'M1-IA'
              },
              {
                nomComplet: 'Cheikh Diallo',
                email: 'cheikh.diallo@example.com',
                numero: 'ETU101',
                niveau: 'Master 1',
                classe: 'M1-IA'
              }
            ]
          },
          {
            titre: 'Réseaux de neurones',
            etudiants: []
          }
        ]
      }
    ]
  },
  {
    id: 2,
    type: 'CHOISI',
    titre: 'Parcours Développement Mobile',
    description: 'Parcours choisi par l’étudiant selon ses préférences.',
    dateCreation: '2025-06-10',
    matieres: [
      {
        nom: 'Android',
        chapitres: [
          {
            titre: 'Kotlin de base',
            etudiants: [
              {
                nomComplet: 'Aminata Sow',
                email: 'aminata.sow@example.com',
                numero: 'ETU200',
                niveau: 'Licence 3',
                classe: 'L3-DM'
              }
            ]
          }
        ]
      },
      {
        nom: 'iOS',
        chapitres: [
          {
            titre: 'Swift avancé',
            etudiants: []
          }
        ]
      }
    ]
  },
  {
    id: 3,
    type: 'MIXTE',
    titre: 'Parcours Data Science et Web',
    description: 'Combinaison mixte entre parcours recommandé et choisi.',
    dateCreation: '2025-07-05',
    matieres: [
      {
        nom: 'Data Science',
        chapitres: [
          {
            titre: 'Analyse de données',
            etudiants: [
              {
                nomComplet: 'Mamadou Ba',
                email: 'mamadou.ba@example.com',
                numero: 'ETU300',
                niveau: 'Master 2',
                classe: 'M2-DS'
              }
            ]
          }
        ]
      },
      {
        nom: 'Développement Web',
        chapitres: [
          {
            titre: 'Angular avancé',
            etudiants: [ {
                nomComplet: 'Mariama Faye',
                email: 'mariama.faye@example.com',
                numero: 'ETU301',
                niveau: 'Master 2',
                classe: 'M2-DS'
              }]
          },
          {
            titre: 'Node.js',
            etudiants: [
              {
                nomComplet: 'Mariama Faye',
                email: 'mariama.faye@example.com',
                numero: 'ETU301',
                niveau: 'Master 2',
                classe: 'M2-DS'
              }
            ]
          }
        ]
      }
    ]
  }];

    this.route.queryParams.subscribe(params => {
      this.typeParcours = params['type']?.toUpperCase() ?? '';
      this.filteredParcours = this.typeParcours
        ? this.parcoursList.filter(p => p.type === this.typeParcours)
        : this.parcoursList;
    });
  }

  toggleEtudiants(matiereNom: string): void {
    this.showEtudiants[matiereNom] = !this.showEtudiants[matiereNom];
  }

  isEtudiantsVisible(matiereNom: string): boolean {
    return !!this.showEtudiants[matiereNom];
  }

  
}
