# Decisões Técnicas

## Backend
Spring Boot + Java 21 + Hexagonal Architecture.

## Frontend
Next.js + React + TypeScript + Tailwind + shadcn/ui.

## Mobile
Expo + React Native.

## Banco
PostgreSQL + Liquibase.

## Estratégia
SaaS modular multiárea, núcleo comum e verticais.

## IA
Codex/Antigravity trabalha por Harness Profissional com commands: status, planejar, seguir, auto, economico, multiagente, autopilot.

## Autopilot por release

O Harness Profissional aceita comandos de autopilot por release, incluindo:

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

Se a release for informada, usar exatamente a release solicitada. Se não for informada, usar a release atual de `docs/RELEASE_STATUS.yaml` ou `docs/codex/SESSION_STATE.md`. Divergência entre arquivos oficiais bloqueia a execução e exige diagnóstico.

O limite padrão de `autopilot release` é de 3 tasks da release atual. Cada task concluída deve gerar commit local individual com Conventional Commits e referência da task. Push permanece manual.

## Isolamento por tenant

A regra de isolamento por `tenant_id` fica na camada de aplicação por `TenantAccessService`, usando o contexto resolvido por request. Usuários sem contexto de tenant ou com perfis globais (`SUPER_ADMIN`, `SUPORTE`) podem executar operações globais; usuários restritos só podem acessar a própria empresa. Violações retornam `TENANT_ACESSO_NEGADO` com HTTP 403.

## Perfis e permissões

Perfis de acesso mapeiam permissões de negócio em `PermissaoAcesso`, com strings de authority publicadas no JWT e na resposta de login. A validação backend inicial acontece por `PermissaoAcessoService` quando existe contexto de tenant; chamadas sem contexto seguem permitidas temporariamente para bootstrap e validação local até a proteção de rotas web/backend avançar.

## Login web

O login web fica em `features/auth`, com página raiz fina, schema Zod e client dedicado para `/api/auth/login`. A sessão usa `sessionStorage` nesta fase para evitar persistência permanente em `localStorage`, com fallback para ambientes de teste que bloqueiam storage. O backend aplica CORS em `/api/**` a partir de `app.cors.allowed-origins` para suportar o desenvolvimento local web + API.

## Proteção de rotas web

A proteção web inicial é client-side porque a sessão atual vive no navegador. `/app` usa `RotaProtegida` e redireciona para `/login?redirectTo=...` quando não há sessão. `/login` usa `RotaPublica` e envia usuários autenticados para `/app`. A raiz `/` redireciona para `/app`, deixando o guarda decidir o destino final.

## Admin SaaS

O módulo Admin SaaS começa em backend hexagonal com permissão dedicada `ACESSAR_ADMIN_SAAS`, use case de status e controller fino em `/api/admin-saas/status`. O acesso inicial fica restrito aos perfis globais `SUPER_ADMIN` e `SUPORTE`; dashboards, gestão de empresas e planos evoluem nas tasks seguintes da R2.

O dashboard Admin SaaS inicial fica em `/api/admin-saas/dashboard`. As metricas de empresas ativas e bloqueadas sao carregadas por adapter JDBC a partir da tabela `empresas`; MRR, trials e chamados permanecem zerados ate existirem os modulos oficiais de planos, trial/assinatura e suporte, evitando tabelas ou regras futuras fora do escopo da TASK-0202.

A gestao Admin SaaS de empresas fica sob `/api/admin-saas/empresas`, separada dos endpoints tenant de `/api/empresas`. A listagem aceita busca por nome, documento ou email; o detalhe nao expoe entidade de persistencia; o bloqueio administrativo altera `empresas.ativo`; e a observacao operacional usa dados reais ja existentes, incluindo usuarios vinculados em `auth_usuarios`.

O modulo de planos usa a entidade de dominio `PlanoAssinatura` e o enum `ModuloPlano` para representar regras de acesso por modulo. A persistencia fica nas tabelas `planos` e `plano_modulos`, com API Admin SaaS em `/api/admin-saas/planos` para criar, listar, detalhar e atualizar. A TASK-0204 nao cadastra planos padrao; isso permanece reservado para a TASK-0205.

Os planos padrao da R2 ficam registrados no catalogo `PlanoPadrao` e persistidos por Liquibase: Estudante, Start, Care, Nutri Pro, Beauty Pro, Biomed Pro, Fisio Pro, Business, Spaces e Premium. A seed usa codigos estaveis e `on conflict` para evitar duplicacao em ambientes locais ja inicializados.

O Plano Estudante possui regra de dominio dedicada: maximo de 1 usuario, 30 clientes e 1 profissional, alem de marca d'agua academica obrigatoria. A informacao e persistida em `planos.estudante` e `planos.marca_dagua_academica`, ficando disponivel pela API de planos para aplicacao nos documentos e telas futuras.

Trials ficam no modulo `assinatura`, em `assinatura_trials`, com validade fixa de 30 dias calculada no dominio `TrialAssinatura`. A conversao da TASK-0207 muda o trial para `CONVERTIDO`; a criacao e gestao de assinaturas pagas completas permanece na TASK-0208.

Assinaturas pagas ficam em `assinaturas`, com status `ATIVA`, `BLOQUEADA` e `CANCELADA`. Upgrade e downgrade usam a mesma operacao de troca de plano, preservando a assinatura e registrando o novo `plano_id`. Bloqueio e cancelamento sao status de assinatura; desbloqueio retorna uma assinatura bloqueada para ativa.

A tela Admin de planos fica em `features/admin-planos`, com client dedicado para `/api/admin-saas/planos`, React Query para cache/invalidation e formulario validado por Zod. A pagina `/app` permanece fina e compoe a feature protegida; o CRUD web nao chama API diretamente dentro da pagina e preserva busca, paginacao e estados de carregamento/vazio.

O dashboard de vendas Admin SaaS fica em `/api/admin-saas/dashboard/vendas`. A aplicacao calcula taxa de conversao de trials e churn a partir de metricas carregadas por porta de saida; o adapter JDBC apenas consulta `assinaturas`, `assinatura_trials` e `planos`. MRR considera assinaturas `ATIVA`, conversao usa trials `CONVERTIDO` sobre trials iniciados, e churn usa assinaturas `CANCELADA` sobre ativas + canceladas.

Clientes/Pacientes ficam no modulo `cliente`, tenant-scoped pela `empresa_id` e acessados por `/api/clientes-pacientes`. O dominio usa `ClientePaciente`, `TipoCliente` e `AreaCliente` para suportar nucleo comum e verticais profissionais. Usuarios globais devem informar `empresaId`; usuarios restritos operam a empresa do contexto. A persistencia fica em `clientes_pacientes` via adapter JDBC.

A agenda operacional fica no modulo `agenda`, tenant-scoped por `empresa_id` e exposta em `/api/agenda/compromissos`. A base de R3 usa `CompromissoAgenda` com profissional opcional, nome do profissional e sala textual para evitar antecipar modulos futuros de equipe/salas. A aplicacao bloqueia conflitos quando ha sobreposicao de horario para o mesmo `profissional_id` ou para a mesma sala na empresa.
