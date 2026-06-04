# Saúde Cardíaca — Frontend

Interface web e mobile do sistema de monitoramento de saúde cardiovascular, desenvolvida com **Angular 20** e **Ionic 8**, com suporte a Android via **Capacitor 8**.

---

## Sumário

1. [Pré-requisitos](#1-pré-requisitos)
2. [Instalação](#2-instalação)
3. [Configuração do ambiente](#3-configuração-do-ambiente)
4. [Executar em modo desenvolvimento (web)](#4-executar-em-modo-desenvolvimento-web)
5. [Executar testes](#5-executar-testes)
6. [Build para produção (web)](#6-build-para-produção-web)
7. [Executar no Android (Capacitor)](#7-executar-no-android-capacitor)
8. [Estrutura do projeto](#8-estrutura-do-projeto)
9. [Rotas disponíveis](#9-rotas-disponíveis)
10. [Problemas comuns](#10-problemas-comuns)

---

## 1. Pré-requisitos

Certifique-se de ter instalado:

| Ferramenta       | Versão mínima | Download                                      |
|------------------|---------------|-----------------------------------------------|
| Node.js          | 20.x (LTS)    | https://nodejs.org                            |
| npm              | 10.x          | Incluído com o Node.js                        |
| Angular CLI      | 20.x          | `npm install -g @angular/cli`                 |
| Ionic CLI        | 7.x           | `npm install -g @ionic/cli`                   |
| Java JDK         | 17            | Necessário para rodar o backend               |

Para desenvolvimento Android, adicionalmente:

| Ferramenta         | Obs                                              |
|--------------------|--------------------------------------------------|
| Android Studio     | https://developer.android.com/studio             |
| Android SDK 34+    | Instalado via Android Studio (SDK Manager)       |
| `ANDROID_HOME`     | Variável de ambiente apontando para o SDK        |

> **Atenção:** o frontend consome a API do backend Spring Boot. Certifique-se de que o backend está rodando antes de iniciar o frontend. Consulte o `README` ou `ARCHITECTURE.md` na raiz do projeto para instruções do backend.

---

## 2. Instalação

Clone o repositório (se ainda não tiver feito):

```bash
git clone <url-do-repositorio>
cd Saude_Cardiaca/frontend
```

Instale as dependências:

```bash
npm install
```

---

## 3. Configuração do ambiente

O arquivo de configuração fica em [src/environments/environment.ts](src/environments/environment.ts).

```typescript
export const environment = {
  production: false,
  apiUrl: Capacitor.isNativePlatform()
    ? 'http://172.19.193.142:8080'   // IP usado no Android físico
    : 'http://localhost:8080',        // URL usada no navegador
};
```

### Rodando no navegador (padrão)

Nenhuma alteração necessária. O app já aponta para `http://localhost:8080` quando executado no browser.

### Rodando no Android com dispositivo físico

Substitua o IP `172.19.193.142` pelo IP da máquina onde o backend está rodando na sua rede local.

**Como descobrir seu IP local:**

```bash
# Windows
ipconfig
# Procure "Endereço IPv4" na interface de rede ativa

# Linux / macOS
ip a
```

Edite [src/environments/environment.ts](src/environments/environment.ts):

```typescript
apiUrl: Capacitor.isNativePlatform()
  ? 'http://SEU_IP_LOCAL:8080'
  : 'http://localhost:8080',
```

Faça o mesmo em [src/environments/environment.prod.ts](src/environments/environment.prod.ts) se for gerar um build de produção para Android.

---

## 4. Executar em modo desenvolvimento (web)

Com o backend rodando na porta 8080, execute:

```bash
npm start
# ou equivalentemente:
ng serve
```

Acesse no navegador: **http://localhost:4200**

O servidor recarrega automaticamente ao salvar qualquer arquivo (`live reload`).

### Opções úteis

```bash
# Porta customizada
ng serve --port 4300

# Acessível na rede local (útil para testar no celular via Wi-Fi)
ng serve --host 0.0.0.0

# Usar configuração de produção no servidor de dev
ng serve --configuration production
```

---

## 5. Executar testes

### Testes unitários (Karma + Jasmine)

```bash
npm test
# ou:
ng test
```

Abre o navegador com o runner Karma e exibe os resultados em tempo real.

```bash
# Executar uma única vez sem watch (útil para CI)
ng test --watch=false --browsers=ChromeHeadless
```

### Lint (ESLint)

```bash
npm run lint
# ou:
ng lint
```

---

## 6. Build para produção (web)

```bash
npm run build
# ou:
ng build --configuration production
```

Os arquivos gerados ficam em `frontend/www/browser/`. Para servir com qualquer servidor HTTP estático:

```bash
# Exemplo com http-server (instalar uma vez: npm install -g http-server)
http-server www/browser -p 8080
```

---

## 7. Executar no Android (Capacitor)

### Passo a passo completo

**1. Gerar o build web:**

```bash
npm run build
```

**2. Sincronizar os arquivos com o projeto Android:**

```bash
npx cap sync android
```

> O comando `sync` copia os arquivos do `www/` para o projeto Android e atualiza os plugins nativos.

**3. Abrir no Android Studio:**

```bash
npx cap open android
```

**4. No Android Studio:**
- Aguarde o Gradle sincronizar o projeto.
- Selecione um dispositivo físico conectado via USB ou um emulador AVD.
- Clique em **Run** (▶) para instalar e executar o app.

### Atualizar após mudanças no código

Após qualquer alteração no frontend:

```bash
npm run build && npx cap sync android
```

Em seguida, clique em **Run** novamente no Android Studio.

### Configurações do app no Android

O arquivo [capacitor.config.ts](capacitor.config.ts) define:

```typescript
{
  appId: 'com.saudecardiaca.app',
  appName: 'Saude Cardiaca',
  webDir: 'www',
  server: {
    cleartext: true,        // permite HTTP (não apenas HTTPS) no Android
    androidScheme: 'http'
  }
}
```

> `cleartext: true` é necessário para comunicação com o backend em HTTP. Em produção, configure o backend com HTTPS e remova essa opção.

---

## 8. Estrutura do projeto

```
frontend/
├── src/
│   ├── app/
│   │   ├── guards/
│   │   │   └── auth.guard.ts          # Protege rotas autenticadas
│   │   ├── interceptors/
│   │   │   └── auth.interceptor.ts    # Anexa JWT em todas as requisições
│   │   ├── models/
│   │   │   ├── user.model.ts          # Interfaces de usuário
│   │   │   └── heart-health.model.ts  # Interfaces de registros e relatórios
│   │   ├── pages/
│   │   │   ├── login/                 # Tela de login
│   │   │   ├── register/              # Cadastro de usuário
│   │   │   ├── records/               # Registros de saúde cardíaca
│   │   │   ├── reports/               # Relatórios e análise de IA
│   │   │   ├── forgot-password/       # Solicitar reset de senha
│   │   │   ├── reset-password/        # Confirmar novo password
│   │   │   └── about/                 # Sobre o app
│   │   ├── services/
│   │   │   ├── auth.service.ts        # Login, cadastro, token JWT
│   │   │   └── heart-health.service.ts# CRUD de registros e relatórios
│   │   ├── app.component.ts
│   │   └── app.routes.ts              # Definição das rotas
│   ├── environments/
│   │   ├── environment.ts             # Dev: aponta para localhost:8080
│   │   └── environment.prod.ts        # Prod: aponta para IP configurado
│   ├── assets/                        # Imagens e recursos estáticos
│   ├── global.scss                    # Estilos globais
│   └── theme/
│       └── variables.scss             # Variáveis de tema Ionic
├── android/                           # Projeto Android gerado pelo Capacitor
├── www/                               # Build gerado (não versionar)
├── capacitor.config.ts                # Configuração do Capacitor
├── angular.json                       # Configuração do Angular CLI
├── package.json                       # Dependências e scripts
└── tsconfig.json                      # Configuração do TypeScript
```

---

## 9. Rotas disponíveis

| Rota                | Autenticação | Descrição                                  |
|---------------------|--------------|--------------------------------------------|
| `/login`            | Pública      | Tela inicial de autenticação               |
| `/register`         | Pública      | Cadastro de novo usuário                   |
| `/forgot-password`  | Pública      | Solicitar código de reset por e-mail       |
| `/reset-password`   | Pública      | Inserir código e definir nova senha        |
| `/records`          | Protegida    | Registrar e visualizar sinais vitais       |
| `/reports`          | Protegida    | Relatórios estatísticos e análise por IA   |
| `/about`            | Pública      | Informações sobre o aplicativo             |

Rotas protegidas redirecionam para `/login` caso não haja token JWT válido no `localStorage`.

---

## 10. Problemas comuns

### `CORS error` ao chamar a API

**Causa:** O backend não está rodando ou a URL em `environment.ts` está incorreta.

**Solução:**
1. Confirme que o backend Spring Boot está rodando na porta 8080.
2. Verifique o IP configurado em `environment.ts` para plataforma nativa.

---

### `net::ERR_CLEARTEXT_NOT_PERMITTED` no Android

**Causa:** O Android bloqueia conexões HTTP por padrão a partir do nível 28.

**Solução:** O `capacitor.config.ts` já tem `cleartext: true`. Se o erro persistir, verifique se `npx cap sync android` foi executado após a última alteração.

---

### Tela em branco após `ng serve`

**Causa:** Erro de compilação TypeScript ou rota inválida.

**Solução:** Verifique o terminal onde o `ng serve` está rodando. Erros de compilação são exibidos lá.

---

### `npm install` falha com erros de permissão (Windows)

**Solução:** Execute o terminal como **Administrador** ou use:

```bash
npm install --legacy-peer-deps
```

---

### App no Android não conecta ao backend

**Causa:** O dispositivo Android e o computador estão em redes diferentes, ou o IP mudou.

**Solução:**
1. Certifique-se de que o celular está na mesma rede Wi-Fi do computador.
2. Descubra o IP atual com `ipconfig` (Windows) ou `ip a` (Linux/macOS).
3. Atualize `environment.ts` com o novo IP.
4. Execute `npm run build && npx cap sync android` e rode o app novamente.

---

*Frontend — Saúde Cardíaca | Angular 20 + Ionic 8 + Capacitor 8*


# Saúde Cardíaca — Arquitetura e Modularização do Backend

## Sumário

1. [Visão Geral da Arquitetura](#1-visão-geral-da-arquitetura)
2. [Stack Tecnológica](#2-stack-tecnológica)
3. [Estrutura de Módulos do Backend](#3-estrutura-de-módulos-do-backend)
4. [Camadas da Aplicação](#4-camadas-da-aplicação)
   - 4.1 [Controllers](#41-controllers)
   - 4.2 [Services](#42-services)
   - 4.3 [Repositories](#43-repositories)
   - 4.4 [Models (Entidades JPA)](#44-models-entidades-jpa)
   - 4.5 [DTOs](#45-dtos)
   - 4.6 [Security](#46-security)
   - 4.7 [Configuration](#47-configuration)
   - 4.8 [Exception Handling](#48-exception-handling)
5. [Fluxo de Autenticação](#5-fluxo-de-autenticação)
6. [Fluxo de Registro de Saúde Cardíaca](#6-fluxo-de-registro-de-saúde-cardíaca)
7. [Integração com IA (Gemini)](#7-integração-com-ia-gemini)
8. [Banco de Dados](#8-banco-de-dados)
9. [Segurança](#9-segurança)
10. [Testes](#10-testes)
11. [Diagrama de Pacotes](#11-diagrama-de-pacotes)

---

## 1. Visão Geral da Arquitetura

O projeto **Saúde Cardíaca** é uma aplicação full-stack para monitoramento de saúde cardiovascular. A arquitetura segue o padrão **cliente-servidor** com separação clara entre frontend e backend.

```
┌──────────────────────────────────────────────────────────┐
│                      CLIENTE                             │
│         Angular 20 + Ionic 8 (Web / Android)            │
│   Login │ Cadastro │ Registros │ Relatórios │ Reset       │
└──────────────────┬───────────────────────────────────────┘
                   │  HTTP/REST (JSON) + JWT Bearer Token
                   ▼
┌──────────────────────────────────────────────────────────┐
│                     BACKEND                              │
│              Spring Boot 3.2.5 — porta 8080             │
│                                                          │
│  ┌────────────┐  ┌─────────────┐  ┌──────────────────┐  │
│  │ Controllers│→ │  Services   │→ │  Repositories    │  │
│  └────────────┘  └──────┬──────┘  └────────┬─────────┘  │
│                         │                  │             │
│                  ┌──────▼──────┐    ┌──────▼──────┐     │
│                  │ GeminiAPI   │    │  Banco H2   │     │
│                  │ (Google AI) │    │ (PostgreSQL) │     │
│                  └─────────────┘    └─────────────┘     │
└──────────────────────────────────────────────────────────┘
```

O backend segue o padrão arquitetural **Layered Architecture (Arquitetura em Camadas)**, onde cada camada tem responsabilidade única e depende apenas da camada imediatamente inferior:

```
Controller  →  Service  →  Repository  →  Database
    ↑               ↑
  DTOs           Entities / Models
```

---

## 2. Stack Tecnológica

### Backend
| Componente          | Tecnologia                         | Versão   |
|---------------------|------------------------------------|----------|
| Linguagem           | Java                               | 17       |
| Framework           | Spring Boot                        | 3.2.5    |
| Segurança           | Spring Security + JWT (jjwt)       | 0.12.5   |
| ORM                 | Spring Data JPA / Hibernate        | —        |
| Banco (dev)         | H2 (in-memory)                     | —        |
| Banco (prod)        | PostgreSQL                         | —        |
| Documentação API    | SpringDoc OpenAPI (Swagger)        | —        |
| Email               | Spring Mail (Gmail SMTP)           | —        |
| IA                  | Google Gemini API (gemini-2.5-flash)| —       |
| Build               | Maven                              | —        |
| Testes              | JUnit 5 + Mockito                  | —        |

### Frontend
| Componente  | Tecnologia           | Versão |
|-------------|----------------------|--------|
| Framework   | Angular              | 20     |
| UI Kit      | Ionic                | 8      |
| Mobile      | Capacitor (Android)  | —      |
| Linguagem   | TypeScript           | 5.9    |

---

## 3. Estrutura de Módulos do Backend

```
backend/
└── src/
    └── main/
        ├── java/com/saudecardiaca/
        │   ├── controller/          # Endpoints REST (entrada HTTP)
        │   ├── service/             # Regras de negócio
        │   ├── repository/          # Acesso a dados (JPA)
        │   ├── model/               # Entidades JPA (tabelas)
        │   ├── dto/                 # Objetos de transferência de dados
        │   │   ├── request/         #   → dados recebidos do cliente
        │   │   └── response/        #   → dados enviados ao cliente
        │   ├── security/            # JWT, filtros e configuração de segurança
        │   ├── config/              # Beans de configuração gerais
        │   └── exception/           # Tratamento centralizado de erros
        └── resources/
            ├── application.properties       # Configuração principal
            └── application-test.properties  # Configuração para testes
```

---

## 4. Camadas da Aplicação

### 4.1 Controllers

Responsáveis por **receber as requisições HTTP**, validar os dados de entrada via anotações (`@Valid`) e delegar o processamento para os Services. Não contêm lógica de negócio.

| Classe                           | Rota base              | Responsabilidade                                  |
|----------------------------------|------------------------|---------------------------------------------------|
| `AuthController`                 | `/auth`                | Registro de usuário e login                       |
| `HeartHealthRecordController`    | `/heart-health-records`| CRUD de registros de saúde cardíaca               |
| `HeartHealthReportController`    | `/heart-health-reports`| Geração e recuperação de relatórios               |
| `PasswordResetController`        | `/auth`                | Solicitação e conclusão de reset de senha         |

**Exemplo de rota:**
```
POST /auth/register     → AuthController.register()
POST /auth/login        → AuthController.login()
POST /heart-health-records → HeartHealthRecordController.create()
GET  /heart-health-records → HeartHealthRecordController.list()
GET  /heart-health-reports → HeartHealthReportController.getReport()
POST /auth/forgot-password → PasswordResetController.forgotPassword()
POST /auth/reset-password  → PasswordResetController.resetPassword()
```

---

### 4.2 Services

Camada de **regras de negócio**. Cada service encapsula um domínio funcional específico e pode se comunicar com múltiplos repositories ou outros services.

| Classe                       | Responsabilidade                                                        |
|------------------------------|-------------------------------------------------------------------------|
| `AuthService`                | Registrar usuário, validar credenciais, emitir JWT                      |
| `HeartHealthRecordService`   | Criar e listar registros com suporte a filtros por data                 |
| `HeartHealthReportService`   | Calcular estatísticas, score de risco cardiovascular e chamar Gemini AI |
| `PasswordResetService`       | Gerar e validar tokens de reset, atualizar senha com hash BCrypt        |
| `EmailService`               | Enviar e-mails transacionais (reset de senha) via SMTP Gmail            |
| `GeminiService`              | Montar prompt com dados clínicos e obter análise da IA do Google        |

**Dependências entre services:**
```
HeartHealthReportService
    ├── HeartHealthRecordRepository  (dados brutos)
    └── GeminiService                (análise por IA)

PasswordResetService
    ├── PasswordResetTokenRepository
    ├── UserRepository
    └── EmailService

AuthService
    └── UserRepository
```

---

### 4.3 Repositories

Interfaces que estendem `JpaRepository`, fornecendo **acesso ao banco de dados** via Spring Data JPA sem implementação manual de SQL. Métodos customizados são declarados com `@Query` ou seguindo convenção de nome de método.

| Interface                          | Entidade mapeada        | Métodos relevantes                            |
|------------------------------------|-------------------------|-----------------------------------------------|
| `UserRepository`                   | `User`                  | `findByEmail(String)`                         |
| `HeartHealthRecordRepository`      | `HeartHealthRecord`     | `findByUserAndCreatedAtBetween(...)` (range)  |
| `PasswordResetTokenRepository`     | `PasswordResetToken`    | `findByToken(String)`, `deleteByUser(...)`    |

---

### 4.4 Models (Entidades JPA)

Classes anotadas com `@Entity` que representam **tabelas no banco de dados**.

#### `User`
```
users
├── id           (UUID, PK)
├── first_name
├── last_name
├── email        (unique)
├── phone
├── password     (BCrypt hash)
├── birth_date
├── gender
├── country
└── created_at
```

#### `HeartHealthRecord`
```
heart_health_records
├── id              (UUID, PK)
├── user_id         (FK → users)
├── systolic_bp     (pressão sistólica, mmHg)
├── diastolic_bp    (pressão diastólica, mmHg)
├── heart_rate      (bpm)
├── o2_saturation   (SpO2, %)
├── weight          (kg)
├── created_at
└── record_symptoms (collection: lista de sintomas)
```

#### `PasswordResetToken`
```
password_reset_tokens
├── id
├── token       (string aleatória única)
├── user_id     (FK → users)
└── expires_at
```

---

### 4.5 DTOs

Objetos usados para **transferência de dados** entre cliente e servidor, evitando exposição direta das entidades JPA.

**Requests (entrada):**
| DTO                          | Uso                                          |
|------------------------------|----------------------------------------------|
| `RegisterRequest`            | Dados para cadastro de novo usuário          |
| `LoginRequest`               | E-mail e senha para autenticação             |
| `HeartHealthRecordRequest`   | Sinais vitais e sintomas para novo registro  |
| `ForgotPasswordRequest`      | E-mail para solicitar reset                  |
| `ResetPasswordRequest`       | Token + nova senha para confirmar reset      |

**Responses (saída):**
| DTO                              | Conteúdo retornado                               |
|----------------------------------|--------------------------------------------------|
| `UserResponse`                   | Dados públicos do usuário (sem senha)            |
| `LoginResponse`                  | JWT token + dados do usuário                     |
| `HeartHealthRecordResponse`      | Registro individual com todos os campos          |
| `HeartHealthRecordListResponse`  | Lista paginada de registros                      |
| `HeartHealthReportResponse`      | Estatísticas + score de risco + análise da IA    |
| `ErrorResponse`                  | Mensagem de erro padronizada com código HTTP     |

---

### 4.6 Security

Pacote responsável por toda a camada de **segurança da API**.

| Classe                          | Responsabilidade                                                     |
|---------------------------------|----------------------------------------------------------------------|
| `JwtUtil`                       | Geração de token JWT (HMAC-SHA), extração de claims, validação       |
| `JwtAuthenticationFilter`       | Filtro HTTP: extrai Bearer token e autentica no SecurityContext      |
| `SecurityConfig`                | Configura Spring Security: rotas públicas/protegidas, CORS, BCrypt   |
| `CustomAuthenticationEntryPoint`| Retorna JSON padronizado (`401 Unauthorized`) em vez de HTML         |

**Fluxo do filtro JWT:**
```
Requisição HTTP
      ↓
JwtAuthenticationFilter
      ├── Extrai "Authorization: Bearer <token>"
      ├── JwtUtil.validateToken()
      ├── Se válido → seta Authentication no SecurityContext
      └── Se inválido → retorna 401 via CustomAuthenticationEntryPoint
```

**Rotas públicas** (sem autenticação):
```
POST /auth/register
POST /auth/login
POST /auth/forgot-password
POST /auth/reset-password
GET  /swagger-ui/**
GET  /v3/api-docs/**
GET  /h2-console/**
```

**Rotas protegidas** (exigem JWT válido):
```
POST /heart-health-records
GET  /heart-health-records
GET  /heart-health-reports
```

---

### 4.7 Configuration

Beans de configuração Spring que ajustam o comportamento da aplicação.

| Classe          | Responsabilidade                                                   |
|-----------------|--------------------------------------------------------------------|
| `SecurityConfig`| Define a cadeia de filtros de segurança (incluída na seção 4.6)   |
| `CorsConfig`    | Permite requisições de qualquer origem (CORS aberto para dev)      |
| `JacksonConfig` | Configuração do serializador JSON (datas, campos nulos etc.)       |
| `OpenApiConfig` | Define metadados da documentação Swagger e suporte a Bearer Token  |

**Parâmetros de configuração relevantes (`application.properties`):**
```properties
server.address=0.0.0.0
server.port=8080

# Banco de dados
spring.datasource.url=jdbc:h2:mem:saudecardiaca
spring.h2.console.enabled=true

# JWT
jwt.secret=<chave-secreta>
jwt.expiration=86400000   # 24 horas em ms

# Gemini AI
gemini.api.key=<chave-api>
gemini.timeout=30

# E-mail (Gmail SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
```

---

### 4.8 Exception Handling

Tratamento **centralizado e padronizado** de erros para toda a API.

| Classe                    | Responsabilidade                                                    |
|---------------------------|---------------------------------------------------------------------|
| `GlobalExceptionHandler`  | `@RestControllerAdvice` que intercepta exceções e retorna JSON      |
| `ApiException`            | Exceção customizada com código HTTP e mensagem descritiva           |

O handler mapeia erros de validação (`@Valid`), `ApiException`, `AuthenticationException` e exceções genéricas para respostas padronizadas no formato `ErrorResponse`, incluindo:
- Código HTTP (`400`, `401`, `404`, `409`, `500`)
- Mensagem legível
- Lista de campos inválidos (para erros de validação)

---

## 5. Fluxo de Autenticação

```
Cliente                      Backend                         Banco
  │                             │                              │
  │── POST /auth/register ──────►│                              │
  │   {name, email, password}   │── verifica e-mail único ────►│
  │                             │◄── usuário não existe ───────│
  │                             │── hashBCrypt(password)        │
  │                             │── save(user) ────────────────►│
  │◄── 201 {user data} ─────────│                              │
  │                             │                              │
  │── POST /auth/login ─────────►│                              │
  │   {email, password}         │── findByEmail(email) ────────►│
  │                             │◄── user ─────────────────────│
  │                             │── BCrypt.matches(pwd, hash)   │
  │                             │── JwtUtil.generateToken()     │
  │◄── 200 {token, user} ───────│                              │
  │                             │                              │
  │── GET /heart-health-records  │                              │
  │   Authorization: Bearer <jwt>│                              │
  │                             │── JwtAuthFilter valida JWT    │
  │                             │── extrai userId do token      │
  │                             │── busca registros ───────────►│
  │◄── 200 [{records...}] ──────│                              │
```

---

## 6. Fluxo de Registro de Saúde Cardíaca

```
Cliente                      Backend                      Gemini AI
  │                             │                              │
  │── POST /heart-health-records►│                              │
  │   {sistólica, diastólica,   │── valida DTO (@Valid)         │
  │    BPM, SpO2, peso,         │── associa ao usuário logado   │
  │    sintomas[]}              │── persiste no banco           │
  │◄── 201 {record} ────────────│                              │
  │                             │                              │
  │── GET /heart-health-reports ►│                              │
  │   ?startDate=&endDate=      │── filtra registros por data   │
  │                             │── calcula médias e desvios    │
  │                             │── calcula score de risco      │
  │                             │── monta prompt clínico ──────►│
  │                             │◄── análise em português ──────│
  │◄── 200 {stats, risk, ai} ───│                              │
```

**Cálculo de Score de Risco:**

O `HeartHealthReportService` analisa os registros do período selecionado e gera um **score de risco cardiovascular** baseado em:
- Pressão sistólica/diastólica (hipertensão por estágios)
- Frequência cardíaca (bradicardia / taquicardia)
- Saturação de O2 (hipoxemia)
- Peso corporal
- Frequência e tipo de sintomas registrados

---

## 7. Integração com IA (Gemini)

O `GeminiService` se comunica com a API do **Google Gemini** (`gemini-2.5-flash`) para gerar análises clínicas personalizadas em linguagem natural (português).

**Fluxo:**
```
HeartHealthReportService
       │
       │── agrega dados vitais do período
       │── calcula médias e desvios padrão
       ▼
GeminiService.generateInsight(dados)
       │
       │── monta prompt estruturado em português com:
       │       • médias de pressão, BPM, SpO2, peso
       │       • sintomas mais frequentes
       │       • contexto clínico
       │── POST https://generativelanguage.googleapis.com/...
       │── timeout: 30s
       ▼
   Retorna texto com:
       • Avaliação dos sinais vitais
       • Alertas e pontos de atenção
       • Sugestões de hábitos saudáveis
       • Recomendação de consulta médica (se necessário)
```

---

## 8. Banco de Dados

### Ambiente de Desenvolvimento
- **H2 in-memory**: banco criado automaticamente ao iniciar a aplicação.
- Console web disponível em: `http://localhost:8080/h2-console`
- Schema gerado automaticamente pelo Hibernate via `spring.jpa.hibernate.ddl-auto=create-drop`

### Ambiente de Produção
- **PostgreSQL**: configurado via variáveis de ambiente.
- Perfil de produção ativa configuração alternativa de datasource.

### Diagrama ER Simplificado
```
users
  │  1
  │  ├──────────────────────────── N  heart_health_records
  │  │                                      │
  │  │                                      └──── N  record_symptoms
  │  │
  └──┴──────────────────────────── N  password_reset_tokens
```

---

## 9. Segurança

| Aspecto                  | Implementação                                            |
|--------------------------|----------------------------------------------------------|
| Autenticação             | JWT (HMAC-SHA), token válido por 24 horas                |
| Armazenamento de senha   | BCrypt com fator de custo 12 (sem reversão possível)     |
| Proteção de rotas        | Spring Security com `SecurityFilterChain` stateless      |
| CORS                     | Habilitado para todas as origens (configurável por env)  |
| CSRF                     | Desabilitado (API stateless, sem cookies de sessão)      |
| Cabeçalhos de segurança  | X-XSS-Protection via Spring Security                     |
| Erros de autenticação    | JSON padronizado (sem stack trace exposto ao cliente)    |
| Variáveis sensíveis      | Chaves de API e credenciais de e-mail via variáveis de env|

---

## 10. Testes

A suíte de testes cobre as camadas mais críticas da aplicação.

### Testes Unitários
| Classe de Teste                   | O que verifica                                           |
|-----------------------------------|----------------------------------------------------------|
| `AuthServiceTest`                 | Registro, login, duplicidade de e-mail, senha inválida   |
| `HeartHealthRecordServiceTest`    | Criação de registro, filtros de data, vínculo ao usuário |
| `HeartHealthReportServiceTest`    | Cálculo de médias, score de risco, chamada ao Gemini     |

### Testes de Integração
| Classe de Teste              | O que verifica                                                  |
|------------------------------|-----------------------------------------------------------------|
| `AuthIntegrationTest`        | Fluxo completo de registro e login via HTTP real com H2         |
| `HeartHealthIntegrationTest` | Criação de registro e recuperação de relatório com JWT real     |

**Banco de dados para testes:** H2 in-memory com configuração isolada em `application-test.properties`.

---

## 11. Diagrama de Pacotes

```
com.saudecardiaca
│
├── controller
│   ├── AuthController
│   ├── HeartHealthRecordController
│   ├── HeartHealthReportController
│   └── PasswordResetController
│
├── service
│   ├── AuthService
│   ├── HeartHealthRecordService
│   ├── HeartHealthReportService
│   ├── PasswordResetService
│   ├── EmailService
│   └── GeminiService
│
├── repository
│   ├── UserRepository
│   ├── HeartHealthRecordRepository
│   └── PasswordResetTokenRepository
│
├── model
│   ├── User
│   ├── HeartHealthRecord
│   └── PasswordResetToken
│
├── dto
│   ├── request
│   │   ├── RegisterRequest
│   │   ├── LoginRequest
│   │   ├── HeartHealthRecordRequest
│   │   ├── ForgotPasswordRequest
│   │   └── ResetPasswordRequest
│   └── response
│       ├── UserResponse
│       ├── LoginResponse
│       ├── HeartHealthRecordResponse
│       ├── HeartHealthRecordListResponse
│       ├── HeartHealthReportResponse
│       └── ErrorResponse
│
├── security
│   ├── JwtUtil
│   ├── JwtAuthenticationFilter
│   ├── SecurityConfig
│   └── CustomAuthenticationEntryPoint
│
├── config
│   ├── CorsConfig
│   ├── JacksonConfig
│   └── OpenApiConfig
│
└── exception
    ├── GlobalExceptionHandler
    └── ApiException
```

---

*Documento gerado em 2026-06-04 — Projeto Saúde Cardíaca*

