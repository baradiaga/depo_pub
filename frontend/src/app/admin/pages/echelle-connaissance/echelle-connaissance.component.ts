import { Component } from '@angular/core';

interface EchelleConnaissance {
  id: number;
  intervalle: string;
  description: string;
  recommandation: string;
}

@Component({
  selector: 'app-echelle-connaissance',
  templateUrl: './echelle-connaissance.component.html',
  styleUrls: ['./echelle-connaissance.component.css']
})
export class EchelleConnaissanceComponent {
  echelles: EchelleConnaissance[] = [
    { id: 1, intervalle: '[0% - 34%]', description: 'Faible maîtrise', recommandation: 'Chapitre recommandé automatiquement' },
    { id: 2, intervalle: '[34% - 66%]', description: 'Maîtrise partielle', recommandation: 'Chapitre recommandé automatiquement' },
    { id: 3, intervalle: '[67% - 100%]', description: 'Bonne maîtrise', recommandation: "Chapitre au choix de l'étudiant" }
  ];

  newEchelle: EchelleConnaissance = { id: 0, intervalle: '', description: '', recommandation: '' };
  editingId: number | null = null;

  addOrUpdateEchelle() {
    if (this.editingId) {
      const index = this.echelles.findIndex(e => e.id === this.editingId);
      if (index !== -1) {
        this.echelles[index] = { ...this.newEchelle, id: this.editingId };
      }
      this.editingId = null;
    } else {
      const newId = Math.max(...this.echelles.map(e => e.id), 0) + 1;
      this.echelles.push({ ...this.newEchelle, id: newId });
    }
    this.resetForm();
  }

  editEchelle(echelle: EchelleConnaissance) {
    this.editingId = echelle.id;
    this.newEchelle = { ...echelle };
  }

  deleteEchelle(id: number) {
    this.echelles = this.echelles.filter(e => e.id !== id);
  }

  resetForm() {
    this.newEchelle = { id: 0, intervalle: '', description: '', recommandation: '' };
  }
}
