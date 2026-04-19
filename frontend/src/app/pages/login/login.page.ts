import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import {
  IonContent, IonInput, IonButton, IonIcon, IonText, IonSpinner, IonInputPasswordToggle
} from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { heartOutline, logInOutline } from 'ionicons/icons';
import { AuthService } from '../../services/auth.service';
import { LoginRequest, ErrorResponse } from '../../models/user.model';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
  standalone: true,
  imports: [
    CommonModule, FormsModule, RouterLink,
    IonContent, IonInput, IonButton, IonIcon, IonText, IonSpinner, IonInputPasswordToggle
  ]
})
export class LoginPage {

  email = '';
  password = '';
  loading = false;
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {
    addIcons({ heartOutline, logInOutline });
  }

  login(): void {
    this.errorMessage = '';
    this.loading = true;

    const data: LoginRequest = {
      email: this.email,
      password: this.password
    };

    this.authService.login(data).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/records']);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        const error = err.error as ErrorResponse;
        this.errorMessage = error?.mensagem || 'Erro ao fazer login.';
      }
    });
  }
}
