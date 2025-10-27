import { Component, Input, OnChanges } from '@angular/core';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnChanges {
  @Input() items: any[] = [];
  @Input() itemsPerPage: number = 5;

  paginatedItems: any[] = [];
  currentPage: number = 1;
  totalPages: number = 1;
  pages: number[] = [];

  ngOnChanges() {
    this.totalPages = Math.ceil(this.items.length / this.itemsPerPage);
    this.pages = Array(this.totalPages).fill(0).map((_, i) => i + 1);
    this.updatePagination();
  }

  goToPage(page: number) {
    if (page < 1 || page > this.totalPages) return;
    this.currentPage = page;
    this.updatePagination();
  }

  updatePagination() {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    this.paginatedItems = this.items.slice(start, start + this.itemsPerPage);
  }
}
