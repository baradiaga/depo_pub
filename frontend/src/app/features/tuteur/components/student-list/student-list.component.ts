import { Component, OnInit } from '@angular/core';
import { StudentService, StudentJourney } from '../../../../services/student.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-student-list',
  templateUrl: './student-list.component.html',
  styleUrls: ['./student-list.component.css']
})
export class StudentListComponent implements OnInit {
  students: StudentJourney[] = [];
  loading = true;
  stats = { aRisque: 0, enProgression: 0, excellent: 0 };

  constructor(private studentService: StudentService, private router: Router) {}

  ngOnInit(): void {
    this.studentService.getStudentsForTeacher().subscribe({
      next: (data) => {
        this.students = data;
        this.calculerStats();
        this.loading = false;
      },
      error: (err) => {
        console.error("Erreur lors de la récupération des étudiants", err);
        this.loading = false;
      }
    });
  }

  calculerStats(): void {
    this.stats = { aRisque: 0, enProgression: 0, excellent: 0 };
    this.students.forEach(s => {
      // On regarde les statuts de tous les chapitres de l'étudiant
      const statuts = s.progressionParCours.map(p => p.statutRecommandation);
      
      if (statuts.includes('Faible')) {
        this.stats.aRisque++;
      } else if (statuts.length > 0 && statuts.every(st => st === 'Bonne maîtrise')) {
        this.stats.excellent++;
      } else {
        this.stats.enProgression++;
      }
    });
  }

  voirProfil(id: number): void {
    this.router.navigate(['/app/tuteur/student', id]);
  }
}
