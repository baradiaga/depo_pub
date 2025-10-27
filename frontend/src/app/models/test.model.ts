export interface Question {
  id: number;
  questionText: string;
  options: string[];
  correctAnswerIndex: number;
}

export interface Test {
  id: number;
  title: string;
  questions: Question[];
}
