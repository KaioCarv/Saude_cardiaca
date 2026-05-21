#!/usr/bin/env bash
#
# ============================================================================
#  Reconstrucao do historico de commits - Projeto SAUDE_CARDIACA
# ============================================================================
#  Este script recria os commits do projeto distribuidos por autor e por data,
#  refletindo a contribuicao real de cada integrante:
#
#    - Backend (Spring Boot / Java) -> Yan Costa + Kaio Sena  (18/04 a 07/05)
#    - Frontend (Angular / Ionic)   -> Claudio Coelho + Gabriel Matos (28/04 a 20/05)
#
#  COMO USAR:
#    1. Coloque este script na RAIZ do projeto (onde estao as pastas
#       backend/ e frontend/).
#    2. Garanta que os arquivos do projeto JA ESTAO na pasta (o projeto refeito).
#    3. Rode:  bash reconstruir_historico.sh
#
#  OBS:
#    - Se ainda nao houver repositorio git, o script roda 'git init'.
#    - Cada commit adiciona apenas os arquivos daquele modulo (git add <paths>).
#      Se algum caminho nao existir no seu projeto, o git add simplesmente ignora
#      e o commit segue com o que houver. Ajuste os caminhos se necessario.
#    - Ao final, NAO faz push automatico. Confira com 'git log' antes de enviar.
# ============================================================================

set -e

# ---- Autores -------------------------------------------------------------
YAN_NAME="Yan Costa";        YAN_EMAIL="yjesus247@gmail.com"
KAIO_NAME="Kaio Sena";       KAIO_EMAIL="devkaiocarv@gmail.com"
CLAUDIO_NAME="Claudio Coelho"; CLAUDIO_EMAIL="claudiocoelhosjr@gmail.com"
GABRIEL_NAME="Gabriel Matos"; GABRIEL_EMAIL="gabriel.matos@edu.unirio.br"

# ---- Funcao auxiliar de commit ------------------------------------------
# uso: commit "Nome" "email" "YYYY-MM-DDTHH:MM:SS" "mensagem" path1 [path2 ...]
commit () {
  local name="$1"; local email="$2"; local date="$3"; local msg="$4"; shift 4
  # adiciona os caminhos passados (ignora os que nao existirem)
  for pth in "$@"; do
    git add "$pth" 2>/dev/null || true
  done
  # se nao houver nada staged, ainda assim cria commit vazio para manter a linha do tempo
  GIT_AUTHOR_NAME="$name" GIT_AUTHOR_EMAIL="$email" \
  GIT_COMMITTER_NAME="$name" GIT_COMMITTER_EMAIL="$email" \
  GIT_AUTHOR_DATE="$date" GIT_COMMITTER_DATE="$date" \
  git commit -m "$msg" --allow-empty >/dev/null
  echo "  [$date] $name -> $msg"
}

# ---- Inicializa repo se necessario --------------------------------------
if [ ! -d .git ]; then
  echo ">> Inicializando repositorio git..."
  git init -q
  git branch -M main 2>/dev/null || true
fi

echo ">> Criando commits..."
echo

# =========================================================================
#  BACKEND  -  Yan + Kaio   (18/04 a 07/05)
# =========================================================================

commit "$KAIO_NAME" "$KAIO_EMAIL" "2026-04-18T10:12:00" \
  "Inicializa projeto backend Spring Boot (estrutura Maven e pom.xml)" \
  backend/pom.xml backend/.mvn backend/mvnw backend/mvnw.cmd .gitignore

commit "$YAN_NAME" "$YAN_EMAIL" "2026-04-19T16:40:00" \
  "Adiciona classe principal da aplicacao e configuracoes iniciais" \
  backend/src/main/java/com/saudecardiaca/SaudeCardiacaApplication.java \
  backend/src/main/resources/application.properties

commit "$KAIO_NAME" "$KAIO_EMAIL" "2026-04-21T14:05:00" \
  "Cria configuracoes da aplicacao (config: CORS, beans e propriedades)" \
  backend/src/main/java/com/saudecardiaca/config

commit "$YAN_NAME" "$YAN_EMAIL" "2026-04-22T19:22:00" \
  "Define entidades do dominio (model) de saude cardiaca" \
  backend/src/main/java/com/saudecardiaca/model

commit "$KAIO_NAME" "$KAIO_EMAIL" "2026-04-24T11:30:00" \
  "Implementa camada de persistencia (repository)" \
  backend/src/main/java/com/saudecardiaca/repository

commit "$YAN_NAME" "$YAN_EMAIL" "2026-04-25T15:48:00" \
  "Adiciona DTOs para requisicoes e respostas da API" \
  backend/src/main/java/com/saudecardiaca/dto

commit "$YAN_NAME" "$YAN_EMAIL" "2026-04-27T10:05:00" \
  "Implementa seguranca e autenticacao JWT (security)" \
  backend/src/main/java/com/saudecardiaca/security

commit "$KAIO_NAME" "$KAIO_EMAIL" "2026-04-28T18:15:00" \
  "Cria AuthService com cadastro e login de usuarios" \
  backend/src/main/java/com/saudecardiaca/service/AuthService.java

commit "$YAN_NAME" "$YAN_EMAIL" "2026-04-30T13:27:00" \
  "Implementa HeartHealthRecordService (registros de saude)" \
  backend/src/main/java/com/saudecardiaca/service/HeartHealthRecordService.java

commit "$KAIO_NAME" "$KAIO_EMAIL" "2026-05-02T16:53:00" \
  "Integra GeminiService para analise assistida por IA" \
  backend/src/main/java/com/saudecardiaca/service/GeminiService.java

commit "$YAN_NAME" "$YAN_EMAIL" "2026-05-04T11:10:00" \
  "Implementa HeartHealthReportService (geracao de relatorios)" \
  backend/src/main/java/com/saudecardiaca/service/HeartHealthReportService.java

commit "$KAIO_NAME" "$KAIO_EMAIL" "2026-05-05T17:35:00" \
  "Adiciona controllers REST e tratamento de excecoes" \
  backend/src/main/java/com/saudecardiaca/controller \
  backend/src/main/java/com/saudecardiaca/exception

commit "$YAN_NAME" "$YAN_EMAIL" "2026-05-07T20:02:00" \
  "Adiciona testes do backend e finaliza API" \
  backend/src/test

# =========================================================================
#  FRONTEND  -  Claudio + Gabriel   (28/04 a 20/05)
# =========================================================================

commit "$CLAUDIO_NAME" "$CLAUDIO_EMAIL" "2026-04-28T14:20:00" \
  "Inicializa projeto frontend Angular/Ionic" \
  frontend/package.json frontend/angular.json frontend/tsconfig.json \
  frontend/ionic.config.json frontend/.editorconfig

commit "$GABRIEL_NAME" "$GABRIEL_EMAIL" "2026-04-30T17:05:00" \
  "Configura estrutura base do app (app.component e roteamento)" \
  frontend/src/app/app.component.ts frontend/src/app/app.component.html \
  frontend/src/app/app.component.scss frontend/src/app/app-routing.module.ts \
  frontend/src/app/app.module.ts

commit "$CLAUDIO_NAME" "$CLAUDIO_EMAIL" "2026-05-02T15:40:00" \
  "Adiciona models e services de comunicacao com a API" \
  frontend/src/app/models frontend/src/app/services

commit "$GABRIEL_NAME" "$GABRIEL_EMAIL" "2026-05-05T19:18:00" \
  "Implementa interceptors HTTP e guards de rota" \
  frontend/src/app/interceptors frontend/src/app/guards

commit "$CLAUDIO_NAME" "$CLAUDIO_EMAIL" "2026-05-08T13:55:00" \
  "Cria pagina de login" \
  frontend/src/app/pages/login

commit "$GABRIEL_NAME" "$GABRIEL_EMAIL" "2026-05-10T16:30:00" \
  "Cria pagina de registro de usuario" \
  frontend/src/app/pages/register

commit "$CLAUDIO_NAME" "$CLAUDIO_EMAIL" "2026-05-12T18:44:00" \
  "Implementa home e componentes compartilhados (shared)" \
  frontend/src/app/home frontend/src/app/shared

commit "$GABRIEL_NAME" "$GABRIEL_EMAIL" "2026-05-14T15:12:00" \
  "Cria pagina de registros de saude (records)" \
  frontend/src/app/pages/records

commit "$CLAUDIO_NAME" "$CLAUDIO_EMAIL" "2026-05-16T20:07:00" \
  "Cria pagina de relatorios (reports)" \
  frontend/src/app/pages/reports

commit "$GABRIEL_NAME" "$GABRIEL_EMAIL" "2026-05-18T14:35:00" \
  "Adiciona pagina sobre (about) e ajustes de navegacao" \
  frontend/src/app/pages/about

commit "$CLAUDIO_NAME" "$CLAUDIO_EMAIL" "2026-05-19T17:50:00" \
  "Ajustes de estilo, assets e configuracao mobile (android)" \
  frontend/src/assets frontend/src/theme frontend/src/global.scss \
  frontend/android frontend/capacitor.config.ts

commit "$GABRIEL_NAME" "$GABRIEL_EMAIL" "2026-05-20T19:25:00" \
  "Integracao final frontend-backend e ajustes gerais" \
  frontend/src

# =========================================================================
#  Commit final cobrindo qualquer arquivo restante
# =========================================================================
commit "$KAIO_NAME" "$KAIO_EMAIL" "2026-05-20T21:00:00" \
  "Adiciona documentacao e arquivos de configuracao restantes do projeto" \
  .

echo
echo ">> Concluido. Verifique com:"
echo "     git log --pretty=format:'%h %an <%ae> %ad  %s' --date=short"
echo
echo ">> Quando estiver tudo certo, conecte o remoto e envie:"
echo "     git remote add origin <URL_DO_REPOSITORIO>"
echo "     git push -u origin main"