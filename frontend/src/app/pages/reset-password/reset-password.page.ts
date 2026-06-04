import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import {
  IonContent, IonInput, IonButton, IonIcon, IonText, IonSpinner, IonInputPasswordToggle
} from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { heartOutline, lockClosedOutline, eye, eyeOff, checkmarkCircleOutline } from 'ionicons/icons';
import { AuthService } from '../../services/auth.service';
import { HttpErrorResponse } from '@angular/common/http';
import { ErrorResponse } from '../../models/user.model';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.page.html',
  styleUrls: ['./reset-password.page.scss'],
  standalone: true,
  imports: [
    CommonModule, FormsModule, RouterLink,
    IonContent, IonInput, IonButton, IonIcon, IonText, IonSpinner, IonInputPasswordToggle
  ]
})
export class ResetPasswordPage implements OnInit {

  email = '';
  code = '';
  newPassword = '';
  confirmPassword = '';
  loading = false;
  errorMessage = '';
  success = false;

  constructor(private authService: AuthService, private router: Router) {
    addIcons({ heartOutline, lockClosedOutline, eye, 'eye-off': eyeOff, checkmarkCircleOutline });
  }

  ngOnInit(): void {
    const nav = this.router.getCurrentNavigation();
    this.email = nav?.extras?.state?.['email'] ?? history.state?.email ?? '';

    if (!this.email) {
      this.router.navigate(['/forgot-password']);
    }
  }

  reset(): void {
    this.errorMessage = '';

    const error = this.validate();
    if (error) {
      this.errorMessage = error;
      return;
    }

    this.loading = true;

    this.authService.resetPassword({
      email: this.email,
      code: this.code.trim(),
      newPassword: this.newPassword,
      confirmPassword: this.confirmPassword
    }).subscribe({
      next: () => {
        this.loading = false;
        this.success = true;
        setTimeout(() => this.router.navigate(['/login']), 2500);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage = this.parseError(err);
      }
    });
  }

  private validate(): string | null {
    if (!this.code.trim() || this.code.trim().length !== 6) return 'Informe o código de 6 dígitos.';
    if (!this.newPassword) return 'Informe a nova senha.';
    if (this.newPassword.length < 6) return 'A senha deve ter pelo menos 6 caracteres.';
    if (this.newPassword !== this.confirmPassword) return 'As senhas não conferem.';
    return null;
  }

  private parseError(err: HttpErrorResponse): string {
    if (err.status === 0) return 'Sem conexão com o servidor. Tente novamente.';
    if (err.status === 400) {
      const body = err.error as ErrorResponse | undefined;
      return body?.mensagem || 'Código inválido ou expirado.';
    }
    const body = err.error as ErrorResponse | undefined;
    return body?.mensagem || 'Erro ao redefinir senha. Tente novamente.';
  }
}
