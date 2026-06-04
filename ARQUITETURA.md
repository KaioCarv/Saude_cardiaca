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
