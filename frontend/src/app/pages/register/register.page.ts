import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import {
  IonContent, IonInput, IonButton, IonIcon, IonText, IonSpinner, IonSelect, IonSelectOption,
  IonBackButton, IonHeader, IonToolbar, IonTitle, IonButtons, IonInputPasswordToggle
} from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { personAddOutline, eye, eyeOff } from 'ionicons/icons';
import { AuthService } from '../../services/auth.service';
import { RegisterRequest, ErrorResponse } from '../../models/user.model';
import { HttpErrorResponse } from '@angular/common/http';
import { COUNTRIES, normalizeText } from '../../shared/countries';

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

  filteredCountries: string[] = [];
  showCountryList = false;

  private readonly fieldLabels: Record<string, string> = {
    firstName: 'Nome',
    lastName: 'Sobrenome',
    email: 'E-mail',
    phone: 'Telefone',
    password: 'Senha',
    confirmPassword: 'Confirmação de senha',
    birthDate: 'Data de nascimento',
    gender: 'Sexo',
    country: 'País',
  };

  constructor(private authService: AuthService, private router: Router) {
    addIcons({ personAddOutline, eye, 'eye-off': eyeOff });
  }

  onPhoneInput(event: CustomEvent): void {
    const raw = (event.detail.value as string) ?? '';
    const digits = raw.replace(/\D/g, '').slice(0, 11);
    let formatted = digits;
    if (digits.length > 7) {
      formatted = `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(7)}`;
    } else if (digits.length > 2) {
      formatted = `(${digits.slice(0, 2)}) ${digits.slice(2)}`;
    }
    this.phone = formatted;
    (event.target as HTMLIonInputElement).value = formatted;
  }

  onCountryFocus(): void {
    this.showCountryList = true;
    this.filteredCountries = this.filterCountries(this.country);
  }

  onCountryBlur(): void {
    setTimeout(() => (this.showCountryList = false), 200);
  }

  onCountryInput(event: CustomEvent): void {
    const value = ((event.detail.value as string) ?? '');
    this.country = value;
    this.filteredCountries = this.filterCountries(value);
    this.showCountryList = true;
  }

  selectCountry(name: string): void {
    this.country = name;
    this.showCountryList = false;
  }

  private filterCountries(query: string): string[] {
    const q = normalizeText(query);
    if (!q) return COUNTRIES.slice(0, 50);
    return COUNTRIES.filter(c => normalizeText(c).includes(q)).slice(0, 50);
  }

  onBirthDateInput(event: CustomEvent): void {
    const raw = (event.detail.value as string) ?? '';
    const digits = raw.replace(/\D/g, '').slice(0, 8);
    let formatted = digits;
    if (digits.length > 4) {
      formatted = `${digits.slice(0, 2)}/${digits.slice(2, 4)}/${digits.slice(4)}`;
    } else if (digits.length > 2) {
      formatted = `${digits.slice(0, 2)}/${digits.slice(2)}`;
    }
    this.birthDate = formatted;
    (event.target as HTMLIonInputElement).value = formatted;
  }

  register(): void {
    this.errorMessage = '';
    this.successMessage = '';

    const validationError = this.validate();
    if (validationError) {
      this.errorMessage = validationError;
      return;
    }

    this.loading = true;

    const data: RegisterRequest = {
      firstName: this.firstName.trim(),
      lastName: this.lastName.trim(),
      email: this.email.trim(),
      phone: this.phone.trim(),
      password: this.password,
      confirmPassword: this.confirmPassword,
      birthDate: this.birthDate,
      gender: this.gender,
      country: this.country.trim()
    };

    this.authService.register(data).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = 'Conta criada com sucesso!';
        setTimeout(() => this.router.navigate(['/login']), 1500);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage = this.parseBackendError(err);
      }
    });
  }

  private validate(): string | null {
    if (!this.firstName.trim()) return 'Informe seu nome.';
    if (!this.lastName.trim()) return 'Informe seu sobrenome.';

    if (!this.email.trim()) return 'Informe seu e-mail.';
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email.trim())) {
      return 'Informe um e-mail válido (ex: nome@exemplo.com).';
    }

    const phoneDigits = this.phone.replace(/\D/g, '');
    if (!phoneDigits) return 'Informe seu telefone.';
    if (phoneDigits.length < 10 || phoneDigits.length > 11) {
      return 'Telefone deve ter 10 ou 11 dígitos, no formato (XX) XXXXX-XXXX.';
    }

    if (!this.password) return 'Informe uma senha.';
    if (this.password.length < 8) return 'A senha deve ter no mínimo 8 caracteres.';
    if (!this.confirmPassword) return 'Confirme sua senha.';
    if (this.password !== this.confirmPassword) return 'As senhas não conferem.';

    if (!this.birthDate) return 'Informe sua data de nascimento.';
    if (!/^\d{2}\/\d{2}\/\d{4}$/.test(this.birthDate)) {
      return 'Data de nascimento deve estar no formato dd/mm/aaaa.';
    }
    const [d, m, y] = this.birthDate.split('/').map(Number);
    const dt = new Date(y, m - 1, d);
    const isValidDate =
      dt.getDate() === d && dt.getMonth() === m - 1 && dt.getFullYear() === y;
    if (!isValidDate) return 'Data de nascimento inválida.';
    if (dt >= new Date()) return 'Data de nascimento deve ser no passado.';

    if (!this.gender) return 'Selecione seu sexo.';
    if (!this.country.trim()) return 'Informe seu país.';

    return null;
  }

  private parseBackendError(err: HttpErrorResponse): string {
    if (err.status === 0) {
      return 'Não foi possível conectar ao servidor. Verifique sua internet e tente novamente.';
    }

    const body = err.error as ErrorResponse | undefined;
    const raw = body?.mensagem ?? '';

    const match = raw.match(/Campos inválidos:\s*\[(.+)\]\.?/);
    if (match) {
      const items = match[1]
        .split(';')
        .map(s => s.trim())
        .filter(Boolean)
        .map(part => {
          const colon = part.indexOf(':');
          if (colon === -1) return part;
          const field = part.slice(0, colon).trim();
          const msg = part.slice(colon + 1).trim();
          const label = this.fieldLabels[field] ?? field;
          return `• ${label}: ${msg}`;
        });
      return `Corrija os campos abaixo:\n${items.join('\n')}`;
    }

    return raw || 'Erro ao criar conta. Tente novamente.';
  }
}
