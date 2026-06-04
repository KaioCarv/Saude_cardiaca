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
