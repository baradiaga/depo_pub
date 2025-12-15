import { Pipe, PipeTransform } from '@angular/core';
import { Formation } from '../services/enseignant.service';

@Pipe({
  name: 'formationFilter'
})
export class FormationFilterPipe implements PipeTransform {
  transform(formations: Formation[] | null, searchText: string, statusFilter: string): Formation[] {
    if (!formations) return [];
    return formations.filter(f => {
      const matchesSearch = !searchText || f.nom.toLowerCase().includes(searchText.toLowerCase());
      const matchesStatus = !statusFilter || f.statut === statusFilter;
      return matchesSearch && matchesStatus;
    });
  }
}
