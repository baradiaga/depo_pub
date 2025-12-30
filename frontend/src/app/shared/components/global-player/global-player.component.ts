import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { YouTubePlayerModule } from '@angular/youtube-player';
import { PlayerService } from '../../../services/player.service'; // Vérifiez ce chemin

@Component({
  selector: 'app-global-player',
  standalone: true,
  imports: [CommonModule, YouTubePlayerModule],
  templateUrl: './global-player.component.html',
  styleUrls: ['./global-player.component.css']
})
export class GlobalPlayerComponent {
  constructor(public playerService: PlayerService) {}
}
