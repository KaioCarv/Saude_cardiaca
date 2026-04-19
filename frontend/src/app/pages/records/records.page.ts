import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import {
  IonContent, IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, IonIcon,
  IonFab, IonFabButton, IonModal, IonInput, IonSpinner, IonText, IonChip, IonLabel,
  IonCard, IonCardHeader, IonCardTitle, IonCardContent, IonRefresher, IonRefresherContent
} from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import {
  addOutline, heartOutline, pulseOutline, waterOutline, fitnessOutline,
  logOutOutline, statsChartOutline, closeOutline, peopleOutline, alertCircleOutline
} from 'ionicons/icons';
import { HeartHealthService } from '../../services/heart-health.service';
import { AuthService } from '../../services/auth.service';
import { HeartHealthRecord, HeartHealthRecordRequest } from '../../models/heart-health.model';
import { ErrorResponse } from '../../models/user.model';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-records',
  templateUrl: './records.page.html',
  styleUrls: ['./records.page.scss'],
  standalone: true,
  imports: [
    CommonModule, FormsModule,
    IonContent, IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, IonIcon,
    IonFab, IonFabButton, IonModal, IonInput, IonSpinner, IonText, IonChip, IonLabel,
    IonCard, IonCardHeader, IonCardTitle, IonCardContent, IonRefresher, IonRefresherContent,
  ]
})
export class RecordsPage implements OnInit {

  records: HeartHealthRecord[] = [];
  loading = false;
  showModal = false;
  saving = false;
  errorMessage = '';
  userName = '';

  // Form fields
  systolic: number | null = null;
  diastolic: number | null = null;
  heartRate: number | null = null;
  oxygenSaturation: number | null = null;
  bodyWeight: number | null = null;
  symptomInput = '';
  symptoms: string[] = [];

  constructor(
    private healthService: HeartHealthService,
    private authService: AuthService,
    private router: Router
  ) {
    addIcons({
      addOutline, heartOutline, pulseOutline, waterOutline, fitnessOutline,
      logOutOutline, statsChartOutline, closeOutline, peopleOutline, alertCircleOutline
    });
  }

  ngOnInit(): void {
    const user = this.authService.getUser();
    this.userName = user ? user.firstName : '';
    this.loadRecords();
  }

  loadRecords(): void {
    this.loading = true;
    this.healthService.getRecords().subscribe({
      next: (res) => {
        this.records = res.items;
        this.loading = false;
      },
      error: () => {
        this.records = [];
        this.loading = false;
      }
    });
  }

  refreshRecords(event: any): void {
    this.healthService.getRecords().subscribe({
      next: (res) => {
        this.records = res.items;
        event.target.complete();
      },
      error: () => {
        this.records = [];
        event.target.complete();
      }
    });
  }

  addSymptom(): void {
    const s = this.symptomInput.trim();
    if (s && !this.symptoms.includes(s)) {
      this.symptoms.push(s);
    }
    this.symptomInput = '';
  }

  removeSymptom(index: number): void {
    this.symptoms.splice(index, 1);
  }

  saveRecord(): void {
    this.errorMessage = '';
    this.saving = true;

    const now = new Date();
    const pad = (n: number) => n.toString().padStart(2, '0');
    const recordedAt = `${pad(now.getDate())}/${pad(now.getMonth() + 1)}/${now.getFullYear()} ${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`;

    const data: HeartHealthRecordRequest = {
      bloodPressureSystolic: this.systolic!,
      bloodPressureDiastolic: this.diastolic!,
      heartRate: this.heartRate!,
      oxygenSaturation: this.oxygenSaturation!,
      bodyWeight: this.bodyWeight || undefined,
      symptoms: this.symptoms.length > 0 ? this.symptoms : undefined,
      recordedAt
    };

    this.healthService.createRecord(data).subscribe({
      next: () => {
        this.saving = false;
        this.showModal = false;
        this.resetForm();
        this.loadRecords();
      },
      error: (err: HttpErrorResponse) => {
        this.saving = false;
        const error = err.error as ErrorResponse;
        this.errorMessage = error?.mensagem || 'Erro ao salvar registro.';
      }
    });
  }

  resetForm(): void {
    this.systolic = null;
    this.diastolic = null;
    this.heartRate = null;
    this.oxygenSaturation = null;
    this.bodyWeight = null;
    this.symptoms = [];
    this.symptomInput = '';
    this.errorMessage = '';
  }

  goToReports(): void {
    this.router.navigate(['/reports']);
  }

  goToAbout(): void {
    this.router.navigate(['/about']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  get formValid(): boolean {
    return !!(this.systolic && this.diastolic && this.heartRate && this.oxygenSaturation);
  }
}
