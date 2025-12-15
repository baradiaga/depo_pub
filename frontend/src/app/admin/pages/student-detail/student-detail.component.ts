import { Component, Input, Output, EventEmitter } from '@angular/core';
import { StudentJourney } from '../models/student-journey.model';
import { CourseProgressDto } from '../models/course-progress.model';

@Component({
  selector: 'app-student-detail',
  templateUrl: './student-detail.component.html',
  styleUrls: ['./student-detail.component.css']
})
export class StudentDetailComponent {
  @Input() studentJourney!: StudentJourney; // le parcours reçu depuis le parent
  @Output() close = new EventEmitter<void>(); // pour fermer la vue

  closeDetail() {
    this.close.emit();
  }

  getCourseProgressList(): CourseProgressDto[] {
    return this.studentJourney?.progressionParCours || [];
  }
}
