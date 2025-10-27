import { Component,OnInit } from '@angular/core';
import { ThemeService } from './core/services/theme.service';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  constructor(private themeService: ThemeService) {}
title = 'moscepafront'; 
  ngOnInit(): void {
    this.themeService.applyTheme(this.themeService.getThemeMode());
  }
}
