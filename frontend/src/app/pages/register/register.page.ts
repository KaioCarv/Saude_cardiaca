import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import {
  IonContent, IonInput, IonButton, IonIcon, IonText, IonSpinner, IonSelect, IonSelectOption,
  IonBackButton, IonHeader, IonToolbar, IonTitle, IonButtons, IonInputPasswordToggle
} from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { personAddOutline } from 'ionicons/icons';
import { AuthService } from '../../services/auth.service';
import { RegisterRequest, ErrorResponse } from '../../models/user.model';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-register',
  templateUrl: './register.page.html',
  styleUrls: ['./register.page.scss'],
  standalone: true,
  imports: [
    CommonModule, FormsModule, RouterLink,
    IonContent, IonInput, IonButton, IonIcon, IonText, IonSpinner, IonSelect, IonSelectOption,
    IonBackButton, IonHeader, IonToolbar, IonTitle, IonButtons, IonInputPasswordToggle
  ]
})
export class RegisterPage {

  firstName = '';
  lastName = '';
  email = '';
  phone = '';
  password = '';
  confirmPassword = '';
  birthDate = '';
  gender = '';
  country = '';
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(private authService: AuthService, private router: Router) {
    addIcons({ personAddOutline });
  }

  register(): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.loading = true;

    const data: RegisterRequest = {
      firstName: this.firstName,
      lastName: this.lastName,
      email: this.email,
      phone: this.phone,
      password: this.password,
      confirmPassword: this.confirmPassword,
      birthDate: this.birthDate,
      gender: this.gender,
      country: this.country
    };

    this.authService.register(data).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = 'Conta criada com sucesso!';
        setTimeout(() => this.router.navigate(['/login']), 1500);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        const error = err.error as ErrorResponse;
        this.errorMessage = error?.mensagem || 'Erro ao criar conta.';
      }
    });
  }
}
