import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import {
  IonContent, IonHeader, IonToolbar, IonTitle, IonButtons, IonBackButton,
  IonCard, IonCardHeader, IonCardTitle, IonCardContent, IonSpinner, IonIcon,
  IonInput, IonButton, IonBadge, IonChip, IonLabel
} from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import {
  heartOutline, pulseOutline, waterOutline, fitnessOutline,
  alertCircleOutline, shieldCheckmarkOutline, searchOutline, warningOutline
} from 'ionicons/icons';
import { HeartHealthService } from '../../services/heart-health.service';
import { HeartHealthReport } from '../../models/heart-health.model';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.page.html',
  styleUrls: ['./reports.page.scss'],
  standalone: true,
  imports: [
    CommonModule, FormsModule,
    IonContent, IonHeader, IonToolbar, IonTitle, IonButtons, IonBackButton,
    IonCard, IonCardHeader, IonCardTitle, IonCardContent, IonSpinner, IonIcon,
    IonInput, IonButton, IonBadge, IonChip, IonLabel
  ]
})
export class ReportsPage implements OnInit {

  report: HeartHealthReport | null = null;
  loading = false;
  errorMessage = '';
  startDate = '';
  endDate = '';

  constructor(private healthService: HeartHealthService, private router: Router) {
    addIcons({
      heartOutline, pulseOutline, waterOutline, fitnessOutline,
      alertCircleOutline, shieldCheckmarkOutline, searchOutline, warningOutline
    });
  }

  ngOnInit(): void {
    const now = new Date();
    const monthAgo = new Date(now.getFullYear(), now.getMonth() - 1, now.getDate());
    const pad = (n: number) => n.toString().padStart(2, '0');
    this.startDate = `${pad(monthAgo.getDate())}/${pad(monthAgo.getMonth() + 1)}/${monthAgo.getFullYear()}`;
    this.endDate = `${pad(now.getDate())}/${pad(now.getMonth() + 1)}/${now.getFullYear()}`;
    this.loadReport();
  }

  loadReport(): void {
    this.loading = true;
    this.errorMessage = '';
    this.report = null;

    this.healthService.getReport(this.startDate, this.endDate).subscribe({
      next: (res) => {
        this.report = res;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.mensagem || 'Erro ao gerar relatorio.';
      }
    });
  }

  getRiskColor(): string {
    if (!this.report) return 'medium';
    switch (this.report.riskLevel) {
      case 'baixo': return 'success';
      case 'moderado': return 'warning';
      case 'alto': return 'danger';
      case 'crítico': return 'danger';
      default: return 'medium';
    }
  }

  getRiskIcon(): string {
    if (!this.report) return 'shield-checkmark-outline';
    return this.report.riskLevel === 'baixo' ? 'shield-checkmark-outline' : 'warning-outline';
  }

  get symptomKeys(): string[] {
    return this.report ? Object.keys(this.report.symptomOccurrences) : [];
  }
}
