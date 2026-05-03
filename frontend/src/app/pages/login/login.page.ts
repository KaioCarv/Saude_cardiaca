import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import {
  IonContent, IonInput, IonButton, IonIcon, IonText, IonSpinner, IonInputPasswordToggle
} from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { heartOutline, logInOutline, eye, eyeOff } from 'ionicons/icons';
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
    addIcons({ heartOutline, logInOutline, eye, 'eye-off': eyeOff });
  }

  login(): void {
    this.errorMessage = '';

    const validationError = this.validate();
    if (validationError) {
      this.errorMessage = validationError;
      return;
    }

    this.loading = true;

    const data: LoginRequest = {
      email: this.email.trim(),
      password: this.password
    };

    this.authService.login(data).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/records']);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage = this.parseLoginError(err);
      }
    });
  }

  private validate(): string | null {
    if (!this.email.trim()) return 'Informe seu e-mail.';
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email.trim())) {
      return 'E-mail inválido.';
    }
    if (!this.password) return 'Informe sua senha.';
    return null;
  }

  private parseLoginError(err: HttpErrorResponse): string {
    if (err.status === 0) {
      return 'Sem conexão com o servidor. Tente novamente.';
    }
    if (err.status === 401 || err.status === 404) {
      return 'E-mail ou senha incorretos.';
    }
    if (err.status === 400) {
      return 'Preencha e-mail e senha corretamente.';
    }
    const body = err.error as ErrorResponse | undefined;
    return body?.mensagem || 'Erro ao fazer login. Tente novamente.';
  }
}
