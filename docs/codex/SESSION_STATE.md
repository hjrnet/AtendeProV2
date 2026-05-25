# SESSION_STATE — AtendePro

## Projeto
AtendePro — SaaS profissional completo.

## Release atual
R1 — Auth, tenant e segurança.

## Última task concluída
TASK-0102 — Cadastro de usuário bootstrap.

## Próxima task recomendada
TASK-0103 — Login com JWT.

## Modo recomendado
multiagente para arquitetura e fundamentos.
economico para ajustes simples.
autopilot release para concluir tasks pendentes de uma release com limite controlado.

## Decisões recentes
- TASK-0102 concluida em modo autopilot multiagente release R1.
- Usuario bootstrap local criado por configuracao `app.bootstrap.admin`, com senha forte, hash BCrypt e persistencia JDBC em `auth_usuarios`.
- Validacao local confirmou Liquibase, healthcheck e usuario `admin@atendepro.local` no Postgres Docker.
- TASK-0101 concluida em modo autopilot multiagente release R1.
- Modulo Auth iniciado com estrutura hexagonal: dominio, commands/results e portas de autenticacao.
- R0 concluida em modo autopilot release.
- TASK-0012 concluida em modo autopilot release R0.
- API client web criado em `web/lib/api` com base URL, interceptors, tratamento de erros e metodos HTTP.
- TASK-0011 concluida em modo autopilot release R0.
- Padrao de testes backend criado com JUnit 5, Mockito via Spring Boot Test, Testcontainers PostgreSQL e smoke tests.
- TASK-0010 concluida em modo autopilot release R0.
- Padrao global de erros criado com `BusinessException`, `ValidationException`, `GlobalExceptionHandler` e DTOs de erro.
- TASK-0009 concluida em modo autopilot release R0.
- Shared kernel backend criado com `Money`, `Percentual`, `BaseId`, `Periodo`, `Paginacao` e `ResultadoPaginado`, com testes unitarios.
- TASK-0008 concluida em modo autopilot release R0.
- OpenAPI/Swagger configurado com springdoc, JSON em `/api/docs` e Swagger UI em `/swagger-ui`.
- TASK-0007 concluida em modo autopilot release R0.
- Liquibase configurado com changelog master e primeira migration tecnica para extensao PostgreSQL `pgcrypto`.
- TASK-0006 concluida em modo autopilot release R0.
- Padroes de ambiente criados com profiles `local` e `test`, exemplos `.env` e documentacao em `docs/deploy/ENVIRONMENT.md`.
- TASK-0005 concluida em modo autopilot release R0.
- Frontend Next.js base criado com App Router, Tailwind, TypeScript, base shadcn/ui e validacao `pnpm lint`, `pnpm typecheck` e `pnpm build`.
- Instalacao web validada com override de proxy npm local: `corepack pnpm --config.proxy=null --config.https-proxy=null install`.
- Harness Profissional atualizado para permitir autopilot por release com comandos como `autopilot release`, `autopilot release R1 até 5 tasks`, `autopilot release R1 até concluir` e `autopilot release até falhar`.
- Limite padrão definido: `autopilot release` executa no máximo 3 tasks da release atual.
- TASK-0005 iniciada em modo autopilot multiagente, mas nao concluida por falha de acesso ao npm registry (`ECONNREFUSED`) durante `corepack pnpm install`.
- Frontend Next.js base ficou como rascunho nao commitado ate a validacao de dependencias, lint e build passar.
- TASK-0004 concluída em modo autopilot multiagente.
- Backend Spring Boot base criado com Java 21, Actuator, Web, Validation e teste de contexto.
- TASK-0003 concluída em modo autopilot multiagente.
- Docker Compose ajustado para nao usar `container_name` fixo, evitando colisao com containers locais existentes.
- TASK-0002 concluída em modo autopilot multiagente.
- Harness Profissional validado em `docs/codex/HARNESS_VALIDATION.md`.
- TASK-0001 concluída em modo autopilot multiagente.
- `.gitignore` inicial criado para proteger ambientes locais, builds e dependências.
- Projeto completo, não MVP-first.
- Backend Spring Boot obrigatório.
- Frontend Next.js.
- Mobile Expo futuro.
- Núcleo comum + verticais profissionais.
- Commit local automático.
- Push manual.
