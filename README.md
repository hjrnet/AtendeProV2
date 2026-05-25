# AtendePro — Blueprint Profissional Completo

Plataforma SaaS multiárea para atendimento, acompanhamento, documentos profissionais, gestão operacional, custo real, precificação, sublocação, planos, suporte e administração SaaS.

## Estratégia deste repositório

Este pacote não é um MVP simplificado. Ele é um **blueprint profissional completo** para Codex, Antigravity ou outro agente de IA construir o produto completo por releases e tasks.

A estratégia é:

1. Blueprint completo do produto.
2. Arquitetura moderna e escalável.
3. Harness profissional com multiagentes.
4. Execução por releases.
5. Cada task com planejamento, implementação, testes, Docker/local, revisão, commit local e próxima task.
6. Push sempre manual pelo usuário.

## Stack definida

Backend:
- Spring Boot
- Java 21
- PostgreSQL
- Liquibase
- Spring Security + JWT + Refresh Token
- OpenAPI/Swagger
- JUnit 5, Mockito, Testcontainers
- Arquitetura Hexagonal + Monolito Modular

Web:
- Next.js
- React
- TypeScript
- Tailwind CSS
- shadcn/ui
- TanStack Query
- React Hook Form
- Zod
- Recharts

Mobile:
- Expo
- React Native
- TypeScript
- NativeWind ou Tamagui

Infra:
- Docker Compose
- PostgreSQL
- Mailpit
- MinIO opcional futuro
- GitHub Actions futuro

## Primeiro comando para Codex/Antigravity

Abra este repositório e envie:

```md
status
```

Depois use:

```md
planejar
```

ou:

```md
auto
```

Para execução contínua controlada:

```md
autopilot 3 tasks
```

Para executar tasks pendentes de uma release:

```md
autopilot release R9 até concluir
```

## Comandos curtos

- `status`: diagnostica projeto e próxima task.
- `planejar`: cria plano e aguarda aprovação.
- `seguir`: executa plano aprovado.
- `auto`: executa uma task completa.
- `economico`: executa uma task economizando tokens.
- `multiagente`: executa com papéis de arquitetura, backend, frontend, QA, DevOps e revisão.
- `autopilot 3 tasks`: executa até 3 tasks em sequência.
- `autopilot multiagente 3 tasks`: executa até 3 tasks com revisão por papéis profissionais.
- `autopilot release`: executa tasks pendentes da release atual, uma por vez.
- `autopilot release R9 até concluir`: executa a release informada até concluir ou bloquear.

## Ambiente demo local

O perfil `local` popula dados fictícios para apresentação e validação visual: empresas, usuários, planos, assinaturas, clientes/pacientes, agenda, procedimentos, custos, estoque, equipamentos, Spaces e simulações de precificação com status saudável, margem baixa e prejuízo.

Logins demo locais:

| Perfil | E-mail | Senha |
|---|---|---|
| Super Admin | `admin@atendepro.local` | `AtendePro@123` |
| Nutri Pro | `karol.nutri@atendepro.local` | `AtendePro@123` |
| Beauty Pro | `ana.estetica@atendepro.local` | `AtendePro@123` |
| Biomed Pro | `bianca.biomed@atendepro.local` | `AtendePro@123` |
| Fisio Pro | `felipe.fisio@atendepro.local` | `AtendePro@123` |
| Spaces | `paula.spaces@atendepro.local` | `AtendePro@123` |
| Estudante | `estudante@atendepro.local` | `AtendePro@123` |

Os dados são exclusivamente fictícios e locais. Detalhes: `docs/product/demo-data.md`.

## Regra principal

O AtendePro é um SaaS completo, mas deve ser construído com controle profissional:

**Produto completo → Releases → Tasks → Testes → Docker/local → Revisão → Commit local → Próxima task**
