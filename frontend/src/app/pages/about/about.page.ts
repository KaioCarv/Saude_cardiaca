import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  IonContent, IonHeader, IonToolbar, IonTitle, IonButtons, IonBackButton,
  IonCard, IonCardContent, IonIcon, IonList, IonItem, IonLabel, IonAvatar
} from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { heartOutline, personOutline } from 'ionicons/icons';

@Component({
  selector: 'app-about',
  templateUrl: './about.page.html',
  styleUrls: ['./about.page.scss'],
  standalone: true,
  imports: [
    CommonModule,
    IonContent, IonHeader, IonToolbar, IonTitle, IonButtons, IonBackButton,
    IonCard, IonCardContent, IonIcon, IonList, IonItem, IonLabel, IonAvatar
  ]
})
export class AboutPage {

  members = [
    { name: 'Claudio Coelho', role: 'Desenvolvedor' },
    { name: 'Gabriel Matos', role: 'Desenvolvedor' },
    { name: 'Kaio Sena', role: 'Desenvolvedor' },
    { name: 'Yan Costa', role: 'Desenvolvedor' }
  ];

  constructor() {
    addIcons({ heartOutline, personOutline });
  }
}
