import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {
  users: any[] = [];
  paginatedUsers: any[] = [];

  currentPage: number = 1;
  itemsPerPage: number = 5;

  ngOnInit() {
    // Exemple de données (peux être remplacé par un appel API)
    this.users = [
      { id: 1, name: 'Alice' },
      { id: 2, name: 'Bob' },
      { id: 3, name: 'Charlie' },
      { id: 4, name: 'David' },
      { id: 5, name: 'Emma' },
      { id: 6, name: 'Frank' },
      { id: 7, name: 'Grace' },
      { id: 8, name: 'Hannah' },
      { id: 9, name: 'Isaac' },
      { id: 10, name: 'Julia' },
      { id: 11, name: 'Kevin' }
    ];

    this.updatePagination();
  }

  updatePagination() {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    this.paginatedUsers = this.users.slice(startIndex, startIndex + this.itemsPerPage);
  }

  goToPage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.updatePagination();
    }
  }

  get totalPages(): number {
    return Math.ceil(this.users.length / this.itemsPerPage);
  }

  get pages(): number[] {
    return Array(this.totalPages).fill(0).map((_, i) => i + 1);
  }
}
