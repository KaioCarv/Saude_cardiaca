import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import {
  IonContent, IonInput, IonButton, IonIcon, IonText, IonSpinner
} from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { heartOutline, mailOutline, arrowBackOutline } from 'ionicons/icons';
import { AuthService } from '../../services/auth.service';
import { HttpErrorResponse } from '@angular/common/http';
import { ErrorResponse } from '../../models/user.model';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.page.html',
  styleUrls: ['./forgot-password.page.scss'],
  standalone: true,
  imports: [
    CommonModule, FormsModule, RouterLink,
    IonContent, IonInput, IonButton, IonIcon, IonText, IonSpinner
  ]
})
export class ForgotPasswordPage {

  email = '';
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(private authService: AuthService, private router: Router) {
    addIcons({ heartOutline, mailOutline, arrowBackOutline });
  }

  send(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.email.trim()) {
      this.errorMessage = 'Informe seu e-mail.';
      return;
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email.trim())) {
      this.errorMessage = 'E-mail inválido.';
      return;
    }

    this.loading = true;

    this.authService.forgotPassword({ email: this.email.trim() }).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/reset-password'], { state: { email: this.email.trim() } });
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage = this.parseError(err);
      }
    });
  }

  private parseError(err: HttpErrorResponse): string {
    if (err.status === 0) return 'Sem conexão com o servidor. Tente novamente.';
    if (err.status === 404) return 'E-mail não cadastrado.';
    const body = err.error as ErrorResponse | undefined;
    return body?.mensagem || 'Erro ao enviar código. Tente novamente.';
  }
}
