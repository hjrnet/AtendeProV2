# AGENTS.md — AtendePro Profissional

## Missão

Construir o AtendePro como SaaS profissional completo, multiempresa, multiárea, com backend Spring Boot, web Next.js e mobile Expo.

## Regra central

O agente deve atuar como equipe profissional de engenharia e execução por task. Não construa o sistema inteiro em uma única resposta.

## Estratégia oficial

Produto completo → Roadmap → Releases → TASKs / AI Work Orders → Planejamento → Execução → Testes → Docker/local → Revisão → Commit local automático → Próxima task.

## Push

Push automático está habilitado ao final de cada task concluída e revisada (incluindo testes obrigatórios e validação Docker/local quando aplicável).
O push pode ser pausado em caso de bloqueio, conflito ou revisão adicional solicitada.

## Commit local

Ao final de cada task, se tudo passar:

1. testes obrigatórios passaram;
2. Docker/local validado ou justificativa registrada;
3. revisão de arquitetura aprovada;
4. escopo respeitado;
5. não houve alteração indevida;

então faça commit local automaticamente com Conventional Commits incluindo a task e faça push automático da branch atual.

Exemplo:

```bash
git add .
git commit -m "feat(auth): implementar login com jwt e refresh token (TASK-0303)"
git push
```

## Comandos curtos

### status
Diagnosticar projeto, release atual, última task concluída, próxima task pendente, comandos disponíveis e riscos.

### planejar
Criar Implementation Plan da próxima task e aguardar aprovação. Não alterar arquivos.

### seguir
Executar o plano já aprovado. Se houver Implementation Plan pendente, ele está aprovado.

### auto
Planejar internamente e executar uma task completa. Não pedir aprovação intermediária.

### economico
Executar uma task com economia de tokens. Ler apenas arquivos necessários e gerar relatório curto.

### multiagente
Executar uma task com papéis profissionais: Produto, Arquitetura, Backend, Frontend/UX, QA, DevOps e Revisão.

### autopilot
Executar múltiplas tasks em sequência, uma por vez, até limite, erro, bloqueio ou solicitação do usuário.

### autopilot release
Executar tasks pendentes de uma release específica em sequência, uma por vez, seguindo o protocolo completo.

Formas aceitas:
- `autopilot release`
- `autopilot release atual`
- `autopilot da release`
- `executar release`
- `concluir release`
- `autopilot R0`
- `autopilot R1`
- `autopilot release R2`
- `autopilot release R1 até 5 tasks`
- `autopilot release R1 até concluir`
- `autopilot release até falhar`

Regras:
- Se a release for informada, use exatamente essa release.
- Se a release não for informada, use a release atual de `docs/RELEASE_STATUS.yaml` ou `docs/codex/SESSION_STATE.md`.
- Se houver divergência entre status, sessão, backlog e task individual, pare e gere diagnóstico de inconsistência.
- O limite padrão de `autopilot release` é de no máximo 3 tasks da release atual.
- `até N tasks` limita a execução a N tasks.
- `até concluir` executa até concluir a release ou encontrar erro/bloqueio.
- `até falhar` executa até erro, bloqueio ou conclusão.
- Cada task deve ser validada, revisada, registrada, commitada localmente e enviada com push automático antes da próxima.

## Políticas de leitura para economizar tokens

Antes de ler documentos longos, leia:

1. `docs/codex/SESSION_STATE.md`
2. `docs/codex/COMANDOS_CURTOS.md`
3. task atual em `docs/codex/tasks/`
4. arquivos impactados

Não repita conteúdo grande no relatório.

## Nomenclatura

Código de negócio deve ser em português claro e intuitivo.

Exemplos bons:
- cadastrarEmpresa
- autenticarUsuario
- calcularPrecoRecomendado
- registrarInsumoDoProcedimento
- gerarRelatorioDePrecificacao
- aplicarMarcaDaguaPlanoEstudante

Evitar:
- process
- handle
- doIt
- data
- obj
- manager
- getAll genérico

Termos técnicos podem permanecer em inglês quando forem padrões/framework:
- Controller
- Repository
- Adapter
- Mapper
- Request
- Response
- DTO
- UseCase
- InputPort
- OutputPort

## Backend

Stack:
- Java 21
- Spring Boot
- PostgreSQL
- Liquibase
- Spring Security
- JWT + Refresh Token
- OpenAPI
- JUnit + Mockito + Testcontainers

Arquitetura obrigatória:
- Monolito modular
- Arquitetura Hexagonal
- Clean Code
- Domain, Application, Ports, Adapters, Config
- Controller fino
- Request/Response DTO
- Command/Result
- UseCase/InputPort
- OutputPort
- Adapter de persistência
- Sem entidade JPA exposta na API
- BigDecimal para dinheiro

## Frontend Web

Stack:
- Next.js
- React
- TypeScript
- Tailwind
- shadcn/ui
- TanStack Query
- React Hook Form
- Zod
- Recharts

Regras:
- Feature architecture
- Mobile-first
- Design premium, clean, profundo, moderno e adequado para saúde
- Toda lista deve ter busca, scroll/paginação e estado vazio
- Forms com validação
- Componentes reutilizáveis
- Não chamar API diretamente dentro de página quando houver client/hook da feature

## Mobile

Stack futura:
- Expo
- React Native
- TypeScript
- NativeWind ou Tamagui

Regras:
- app do cliente/paciente
- app do profissional
- API client compartilhado
- design tokens compartilhados

## Produto

O AtendePro deve ser SaaS modular multiárea com núcleo comum e verticais profissionais.

Núcleo comum:
- auth
- tenant/empresa
- usuários/permissões
- clientes/pacientes
- agenda
- serviços/procedimentos
- custos
- precificação
- estoque
- equipamentos
- documentos
- carimbo profissional
- sublocação
- dashboard
- planos/assinaturas
- suporte/chamados
- Admin SaaS

Verticais:
- Nutri Pro
- Beauty Pro
- Biomed Pro
- Fisio Pro
- Spaces
- Psico Pro futuro
- Fono Pro futuro
- Farmácia Clínica futuro
- Odonto Pro futuro
- Terapias e Saúde Integrativa futuro

## Relatório obrigatório ao final de cada task

1. Release atual
2. Task executada
3. Resumo da implementação
4. Arquivos criados
5. Arquivos alterados
6. Arquivos fora do escopo
7. Comandos executados
8. Resultado dos testes
9. Docker e execução local
10. Revisão da arquitetura
11. Status da task
12. Commit local
13. Próxima task recomendada
14. Prompt pronto para próxima task
15. Push: informar sempre que não foi realizado

## Definition of Done

A task só está pronta se:

- escopo respeitado;
- código compila;
- testes passam;
- Docker/local validado;
- arquitetura respeitada;
- sem entidade JPA exposta;
- sem task futura implementada;
- commit local criado;
- próxima task recomendada.
