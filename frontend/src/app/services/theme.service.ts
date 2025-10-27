import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ThemeService {
  storedTheme: string | undefined;
  init() {
    throw new Error('Method not implemented.');
  }
  private modeKey = 'theme-mode';

  constructor() {
    const savedMode = this.getThemeMode();
    this.applyTheme(savedMode || 'auto');
  }

  getThemeMode(): 'light' | 'dark' | 'auto' {
    return (localStorage.getItem(this.modeKey) as 'light' | 'dark' | 'auto') || 'auto';
  }

  setThemeMode(mode: 'light' | 'dark' | 'auto') {
    localStorage.setItem(this.modeKey, mode);
    this.applyTheme(mode);
  }

  applyTheme(mode: 'light' | 'dark' | 'auto') {
    let effectiveMode = mode;

    if (mode === 'auto') {
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      effectiveMode = prefersDark ? 'dark' : 'light';
    }

    document.body.classList.remove('light-theme', 'dark-theme');
    document.body.classList.add(`${effectiveMode}-theme`);
  }
}
