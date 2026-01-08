import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { StudentService, StudentJourney } from '../../../../services/student.service';

@Component({
  selector: 'app-student-detail',
  templateUrl: './student-detail.component.html',
  styleUrls: ['./student-detail.component.css']
})
export class StudentDetailComponent implements OnInit {
  student?: StudentJourney;
  testHistory: any[] = [];
  types = ['RECOMMANDE', 'CHOISI', 'MIXTE'];
  isUpdating = false;

  constructor(
    private route: ActivatedRoute, 
    private studentService: StudentService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.loadStudentData(id);
    }
  }

  loadStudentData(id: number): void {
    // Récupération du profil et de la progression
    this.studentService.getStudentDetail(id).subscribe(data => {
      this.student = data;
    });

    // Récupération de l'historique technique des tests (Catégorie 3)
    this.studentService.getStudentTestHistory(id).subscribe(history => {
      this.testHistory = history;
    });
  }

  changerParcours(event: any) {
    if (!this.student) return;
    
    const nouveauType = event.target.value;
    this.isUpdating = true;

    this.studentService.updateParcoursType(this.student.studentId, nouveauType).subscribe({
      next: () => {
        this.isUpdating = false;
        // Optionnel : Notification Toast 2026 ici
      },
      error: () => this.isUpdating = false
    });
  }
}
