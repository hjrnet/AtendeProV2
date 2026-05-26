# TASKS — AtendePro Profissional

Cada TASK é uma AI Work Order oficial. O agente só pode executar tasks listadas aqui e com arquivo correspondente em `docs/codex/tasks/`.

## R0 — Fundação técnica profissional

| Task | Nome | Descrição | Status |
|---|---|---|---|
| TASK-0001 | Configurar estrutura inicial do repositório | Criar base de arquivos, padrões e validação inicial. | CONCLUIDA |
| TASK-0002 | Validar Harness Profissional | Garantir AGENTS, comandos curtos, protocolo e status. | CONCLUIDA |
| TASK-0003 | Configurar Docker Compose com PostgreSQL | Subir PostgreSQL local e Mailpit. | CONCLUIDA |
| TASK-0004 | Criar backend Spring Boot base | Criar app Spring Boot com dependências base. | CONCLUIDA |
| TASK-0005 | Criar frontend Next.js base | Criar web com Next.js, Tailwind e shadcn/ui. | CONCLUIDA |
| TASK-0006 | Criar padrões de ambiente | Configurar .env, profiles e documentação local. | CONCLUIDA |
| TASK-0007 | Configurar Liquibase | Criar changelog master e primeira migration. | CONCLUIDA |
| TASK-0008 | Configurar OpenAPI/Swagger | Expor documentação da API. | CONCLUIDA |
| TASK-0009 | Criar shared kernel do backend | Money, Percentual, BaseId, datas, paginação. | CONCLUIDA |
| TASK-0010 | Criar padrão global de erros | BusinessException, ValidationException e handler. | CONCLUIDA |
| TASK-0011 | Criar padrão de testes backend | JUnit, Mockito, Testcontainers e smoke tests. | CONCLUIDA |
| TASK-0012 | Criar padrão de API client web | Fetch client, interceptors, erros e base URL. | CONCLUIDA |

## R1 — Auth, tenant e segurança

| Task | Nome | Descrição | Status |
|---|---|---|---|
| TASK-0101 | Criar módulo Auth | Estrutura hexagonal do módulo de autenticação. | CONCLUIDA |
| TASK-0102 | Cadastro de usuário bootstrap | Usuário inicial e senha segura para ambiente local. | CONCLUIDA |
| TASK-0103 | Login com JWT | Autenticação e emissão de access token. | CONCLUIDA |
| TASK-0104 | Refresh token | Renovação de sessão segura. | CONCLUIDA |
| TASK-0105 | Recuperação de senha | Fluxo de reset com token. | CONCLUIDA |
| TASK-0106 | Criar módulo Empresa/Tenant | Cadastro de empresas e isolamento base. | CONCLUIDA |
| TASK-0107 | Criar usuário administrador da empresa | Admin vinculado ao tenant. | CONCLUIDA |
| TASK-0108 | Contexto de tenant | Resolver tenant por token e request. | CONCLUIDA |
| TASK-0109 | Isolamento por tenant_id | Aplicar filtros e validações de tenant. | CONCLUIDA |
| TASK-0110 | Perfis e permissões | Roles e authorities base. | CONCLUIDA |
| TASK-0111 | Tela de login web | Login, botão demo e armazenamento seguro de token. | CONCLUIDA |
| TASK-0112 | Proteção de rotas web | Guardas e redirecionamento. | CONCLUIDA |

## R2 — Admin SaaS, planos e assinaturas

| Task | Nome | Descrição | Status |
|---|---|---|---|
| TASK-0201 | Criar módulo Admin SaaS | Base para gestão da plataforma. | CONCLUIDA |
| TASK-0202 | Dashboard Admin SaaS | MRR, empresas, trials e chamados. | CONCLUIDA |
| TASK-0203 | Gestão de empresas | Listar, detalhar, bloquear e observar empresas. | CONCLUIDA |
| TASK-0204 | Criar módulo de planos | Entidade Plano e regras de módulos. | CONCLUIDA |
| TASK-0205 | Planos padrão | Estudante, Start, Care, Nutri Pro, Beauty Pro, Biomed Pro, Fisio Pro, Business, Spaces, Premium. | CONCLUIDA |
| TASK-0206 | Plano Estudante | Limites e marca d'água acadêmica. | CONCLUIDA |
| TASK-0207 | Trial 30 dias | Trial com vencimento e conversão. | CONCLUIDA |
| TASK-0208 | Assinaturas | Status, upgrade, downgrade, cancelamento e bloqueio. | CONCLUIDA |
| TASK-0209 | Tela Admin de planos | CRUD e limites por plano. | CONCLUIDA |
| TASK-0210 | Dashboard de vendas | MRR, conversão, churn e planos vendidos. | CONCLUIDA |

## R3 — Núcleo operacional comum

| Task | Nome | Descrição | Status |
|---|---|---|---|
| TASK-0301 | Clientes/Pacientes | Cadastro e listagem multiárea. | CONCLUIDA |
| TASK-0302 | Agenda base | Agenda por profissional e sala. | CONCLUIDA |
| TASK-0303 | Serviços/Procedimentos | Cadastro genérico de serviços. | CONCLUIDA |
| TASK-0304 | Custos gerais | Custos fixos, variáveis e eventuais. | CONCLUIDA |
| TASK-0305 | Alimentação e transporte | Custos operacionais pessoais/profissionais. | CONCLUIDA |
| TASK-0306 | Estoque base | Produtos, lotes e validade. | CONCLUIDA |
| TASK-0307 | Equipamentos base | Valor, vida útil e manutenção. | CONCLUIDA |
| TASK-0308 | Dashboard da empresa | Indicadores operacionais iniciais. | CONCLUIDA |
| TASK-0309 | Busca e filtros globais | Busca em listas e filtros por categoria/status. | CONCLUIDA |
| TASK-0310 | UX premium operacional | Layout web moderno e responsivo. | CONCLUIDA |

## R4 — Custo real, precificação e relatórios

| Task | Nome | Descrição | Status |
|---|---|---|---|
| TASK-0401 | Módulo Precificação | Base hexagonal de cálculo. | CONCLUIDA |
| TASK-0402 | Calculadora de custo real | Insumos, sala, tempo, deslocamento, alimentação, taxas. | CONCLUIDA |
| TASK-0403 | Preço mínimo | Preço para não operar no prejuízo. | CONCLUIDA |
| TASK-0404 | Preço recomendado | Cálculo por margem desejada. | CONCLUIDA |
| TASK-0405 | Margem e lucro | Margem real, lucro estimado e alertas. | CONCLUIDA |
| TASK-0406 | Simulador de preço web | Tela completa com resultado. | CONCLUIDA |
| TASK-0407 | Histórico de simulações | Salvar e editar simulações. | CONCLUIDA |
| TASK-0408 | Relatório imprimível/PDF | PDF de precificação e composição de custos. | CONCLUIDA |
| TASK-0409 | Dashboard de precificação | Indicadores e gráficos. | CONCLUIDA |
| TASK-0410 | Dados demo realistas | Procedimentos e simulações por área. | CONCLUIDA |

## R5 — Sublocação e Spaces

| Task | Nome | Descrição | Status |
|---|---|---|---|
| TASK-0501 | Módulo Spaces | Base para salas, cadeiras, cabines e recursos. | CONCLUIDA |
| TASK-0502 | Cadastro de recursos | Salas, cadeiras, cabines e equipamentos. | CONCLUIDA |
| TASK-0503 | Custo por hora de espaço | Rateio por disponibilidade e custos fixos. | CONCLUIDA |
| TASK-0504 | Pacotes de sublocação | Hora, turno, diária, mensal, fixo + percentual. | CONCLUIDA |
| TASK-0505 | Simulação do parceiro | Lucro do profissional que subloca. | CONCLUIDA |
| TASK-0506 | Agenda de ocupação | Disponibilidade de recursos. | CONCLUIDA |
| TASK-0507 | Relatório de sublocação | PDF e indicadores. | CONCLUIDA |

## R6 — Documentos profissionais e carimbo

| Task | Nome | Descrição | Status |
|---|---|---|---|
| TASK-0601 | Módulo Documento Profissional | Base de documentos com histórico. | CONCLUIDA |
| TASK-0602 | Carimbo profissional | Conselho, UF, número, assinatura e clínica. | CONCLUIDA |
| TASK-0603 | Geração de PDF | Templates e exportação. | CONCLUIDA |
| TASK-0604 | QR Code de validação | Validação pública limitada. | CONCLUIDA |
| TASK-0605 | Marca d'água Plano Estudante | Documento acadêmico sem validade profissional. | CONCLUIDA |
| TASK-0606 | Modelos de documentos gerais | Declaração, relatório, termo, orientação, recibo. | CONCLUIDA |
| TASK-0607 | Histórico e versionamento | Substituição, cancelamento e auditoria. | CONCLUIDA |

## R7 — Verticais profissionais

| Task | Nome | Descrição | Status |
|---|---|---|---|
| TASK-NUTRI-001 | Estruturar requisitos completos do Nutri Pro | Especificação oficial a partir da descrição e referências visuais, sem implementação técnica. | CONCLUIDA |
| TASK-AGD-001 | Estruturar módulo Agenda do AtendePro | Especificação funcional, regras, UX, entidades e backlog da agenda comum. | CONCLUIDA |
| TASK-0701 | Nutri Pro | Plano alimentar, diário, exames, suplementação e CRN. | CONCLUIDA |
| TASK-0702 | Beauty Pro | Protocolos, fotos, termos, pacotes e salão. | CONCLUIDA |
| TASK-0703 | Biomed Pro | CRBM, habilitações, rastreabilidade e estética biomédica. | CONCLUIDA |
| TASK-0704 | Fisio Pro | CREFITO, avaliação, plano terapêutico e evolução por sessão. | CONCLUIDA |
| TASK-0705 | Psico Pro futuro | Documentos psicológicos e evolução, respeitando escopo futuro. | CONCLUIDA |
| TASK-0706 | Fono Pro futuro | Relatórios, laudos e acompanhamento. | CONCLUIDA |
| TASK-0707 | Farmácia Clínica futuro | Serviços clínicos e documentos CRF. | CONCLUIDA |
| TASK-0708 | Odonto Pro futuro | Documentos odontológicos e CRO. | CONCLUIDA |

## R8 — Fundação de experiência profissional do SaaS

| Task | Nome | Descrição | Status |
|---|---|---|---|
| TASK-UX-001 | Redesenhar shell, navegação e experiência responsiva do AtendePro | Shell profissional responsivo, navegação real por área, progressive disclosure e acabamento premium. | CONCLUIDA |
| TASK-0801 | Portal do cliente web | Agenda, documentos e evolução. | PENDENTE |
| TASK-0802 | App Expo base | Estrutura mobile. | PENDENTE |
| TASK-0803 | App do cliente/paciente | Agenda, documentos, diário, fotos e mensagens. | PENDENTE |
| TASK-0804 | App do profissional | Agenda do dia, mensagens e acompanhamento. | PENDENTE |
| TASK-0805 | Notificações push | Lembretes e eventos. | PENDENTE |
| TASK-0806 | Experiência mobile premium | Design app-like. | PENDENTE |

## R9 — Suporte, central de ajuda, comunicação e refinamentos de experiência

| Task | Nome | Descrição | Status |
|---|---|---|---|
| TASK-0901 | Módulo Chamados | Abertura e acompanhamento de suporte. | CONCLUIDA |
| TASK-0902 | Painel Admin de Suporte | Caixa de entrada, prioridade e status. | CONCLUIDA |
| TASK-0903 | Central de ajuda | Artigos, FAQ e tutoriais. | CONCLUIDA |
| TASK-0904 | Feedback e roadmap | Pedidos de melhoria e priorização. | CONCLUIDA |
| TASK-0905 | Notificações internas | Eventos, avisos e alertas. | CONCLUIDA |
| TASK-R9-001 | Destacar visualmente simulações em alerta na Precificação | Alertas visuais, badges, ícones e filtros para identificar simulações saudáveis, com margem baixa ou em prejuízo. | CONCLUIDA |
| TASK-R9-002 | Criar menu rápido de ações principais do Nutri Pro | Especificar menu rápido no prontuário nutricional, priorizando gastos energéticos, exames laboratoriais e plano alimentar. | CONCLUIDA |
| TASK-R9-003 | Corrigir acentuação e textos em português do sistema | Revisar textos visíveis, labels, botões, estados vazios e microcopy para português brasileiro correto. | CONCLUIDA |
| TASK-R9-004 | Popular ambiente demo com usuários, planos e simulações realistas | Seed local/demo com empresas, usuários, planos, pacientes, procedimentos, simulações e documentação de logins fictícios. | CONCLUIDA |

## R10 — Nutri Pro, Beauty Pro e comercial

| Task | Nome | Descrição | Status |
|---|---|---|---|
| TASK-NUTRI-002 | Criar módulo Nutri Pro operacional | Backend hexagonal, rotas web e base de domínio para Nutri Pro real. | CONCLUIDA |
| TASK-NUTRI-003 | Criar prontuário nutricional e menu rápido funcional | Perfil nutricional do paciente com ações rápidas reais. | CONCLUIDA |
| TASK-NUTRI-004 | Criar avaliação antropométrica e gasto energético | Registrar avaliação, IMC, TMB/GEB/GET e metas. | CONCLUIDA |
| TASK-NUTRI-005 | Criar plano alimentar com refeições, alimentos e suplementos | Plano alimentar operacional com refeições, alimentos personalizados, suplementos e macros iniciais. | CONCLUIDA |
| TASK-NUTRI-006 | Criar documentos, exames, prescrições e dashboard Nutri Pro | Solicitações, prescrições, PDF com CRN/carimbo e indicadores da vertical. | CONCLUIDA |
| TASK-BEAUTY-001 | Criar módulo Beauty Pro operacional | Backend hexagonal, rotas web e base de domínio para estética/beleza real. | CONCLUIDA |
| TASK-BEAUTY-002 | Criar ficha estética, anamnese e avaliação | Perfil estético do cliente com anamnese, objetivos, contraindicações e registros iniciais. | CONCLUIDA |
| TASK-BEAUTY-003 | Criar protocolos, sessões e evolução Beauty Pro | Protocolos faciais/corporais, pacote de sessões, execução e evolução. | CONCLUIDA |
| TASK-BEAUTY-004 | Criar termos, fotos placeholder, produtos/lotes e dashboard Beauty Pro | Termos, rastreabilidade simples, evidências seguras sem fotos reais e indicadores da vertical. | CONCLUIDA |
| TASK-BEAUTY-005 | Integrar Beauty Pro com agenda, precificação e experiência web | Tela operacional completa, agenda, serviços, custos e precificação integrados. | CONCLUIDA |
| TASK-1001 | Landing page pública | Produto, dor, solução e CTA. | CONCLUIDA |
| TASK-1002 | Páginas por vertical | Nutri, Estética, Biomed, Fisio, Spaces. | CONCLUIDA |
| TASK-1003 | Página de planos | Comparação e trial 30 dias. | CONCLUIDA |
| TASK-1004 | Calculadora gratuita | Lead magnet de preço ideal. | CONCLUIDA |
| TASK-1005 | Formulário de lead | Captação de interessados. | CONCLUIDA |
| TASK-1006 | Corrigir responsividade do hero e painel operacional público | Ajustar layout da landing para evitar sobreposição/desalinhamento do painel operacional ao mudar tamanho da tela, mantendo experiência premium em mobile, tablet e desktop. | CONCLUIDA |
| TASK-1007 | Redesenhar workspace profissional Nutri Pro e navegação por perfil | Refatorar a experiência interna do profissional para abrir Nutri Pro como área principal do usuário de nutrição, ocultar verticais sem sentido para o perfil, trocar conteúdo por navegação real e corrigir desalinhamentos do painel operacional. | CONCLUIDA |
| TASK-1008 | Refatorar UX Nutri Pro com telas dedicadas e submenus limpos | Separar Início, Agenda, Pacientes, Prontuário, Plano alimentar, Avaliações e Documentos em telas reais, com submenus, menos poluição visual, sem conteúdo abrindo ao lado/abaixo e com layout responsivo organizado. | CONCLUIDA |
| TASK-1009 | Refatorar UX Beauty Pro com telas dedicadas e submenus limpos | Aplicar ao Beauty Pro a mesma organização aprovada no Nutri Pro, separando início, agenda/preços, clientes, ficha estética, protocolos/sessões, termos/evidências/produtos, precificação e busca em telas focadas, com submenus e layout responsivo sem empilhamento vertical poluído. | CONCLUIDA |
| TASK-1010 | Ativar ações operacionais do Nutri Pro, cadastro de paciente e criação de agenda | Tornar ações prioritárias e próximas evoluções clicáveis, cadastrar paciente Nutri, criar agendamento Nutri e ajustar a usabilidade sem poluir ou desalinhavar o workspace. | CONCLUIDA |
| TASK-1011 | Ajustar espaçamento vertical excessivo do hero público | Reduzir o vazio acima e abaixo do bloco principal da landing, mantendo proporção premium, painel operacional alinhado e responsividade em desktop, tablet e mobile. | CONCLUIDA |

## R11 — IA, automações e integrações

| Task | Nome | Descrição | Status |
|---|---|---|---|
| TASK-1101 | IA de precificação | Sugestões e alertas assistidos. | PENDENTE |
| TASK-1102 | IA de suporte | Respostas e triagem de chamados. | PENDENTE |
| TASK-1103 | WhatsApp futuro | Integração oficial quando configurada. | PENDENTE |
| TASK-1104 | Pagamentos | Integração futura com gateway. | PENDENTE |
| TASK-1105 | Assinatura digital avançada | ICP-Brasil/ferramenta parceira futura. | PENDENTE |

## R12 — Escala, observabilidade e produção

| Task | Nome | Descrição | Status |
|---|---|---|---|
| TASK-1201 | CI/CD | GitHub Actions. | PENDENTE |
| TASK-1202 | Observabilidade | Logs estruturados, métricas e traces. | PENDENTE |
| TASK-1203 | Backups e restore | Estratégia de banco. | PENDENTE |
| TASK-1204 | Hardening de segurança | Headers, rate limit e auditoria. | PENDENTE |
| TASK-1205 | Deploy produção | Ambiente cloud/VPS. | PENDENTE |
