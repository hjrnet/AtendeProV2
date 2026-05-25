# Roadmap por Releases — AtendePro Profissional

## R0 — Fundação técnica profissional

- **TASK-0001 — Configurar estrutura inicial do repositório**: Criar base de arquivos, padrões e validação inicial.
- **TASK-0002 — Validar Harness Profissional**: Garantir AGENTS, comandos curtos, protocolo e status.
- **TASK-0003 — Configurar Docker Compose com PostgreSQL**: Subir PostgreSQL local e Mailpit.
- **TASK-0004 — Criar backend Spring Boot base**: Criar app Spring Boot com dependências base.
- **TASK-0005 — Criar frontend Next.js base**: Criar web com Next.js, Tailwind e shadcn/ui.
- **TASK-0006 — Criar padrões de ambiente**: Configurar .env, profiles e documentação local.
- **TASK-0007 — Configurar Liquibase**: Criar changelog master e primeira migration.
- **TASK-0008 — Configurar OpenAPI/Swagger**: Expor documentação da API.
- **TASK-0009 — Criar shared kernel do backend**: Money, Percentual, BaseId, datas, paginação.
- **TASK-0010 — Criar padrão global de erros**: BusinessException, ValidationException e handler.
- **TASK-0011 — Criar padrão de testes backend**: JUnit, Mockito, Testcontainers e smoke tests.
- **TASK-0012 — Criar padrão de API client web**: Fetch client, interceptors, erros e base URL.

## R1 — Auth, tenant e segurança

- **TASK-0101 — Criar módulo Auth**: Estrutura hexagonal do módulo de autenticação.
- **TASK-0102 — Cadastro de usuário bootstrap**: Usuário inicial e senha segura para ambiente local.
- **TASK-0103 — Login com JWT**: Autenticação e emissão de access token.
- **TASK-0104 — Refresh token**: Renovação de sessão segura.
- **TASK-0105 — Recuperação de senha**: Fluxo de reset com token.
- **TASK-0106 — Criar módulo Empresa/Tenant**: Cadastro de empresas e isolamento base.
- **TASK-0107 — Criar usuário administrador da empresa**: Admin vinculado ao tenant.
- **TASK-0108 — Contexto de tenant**: Resolver tenant por token e request.
- **TASK-0109 — Isolamento por tenant_id**: Aplicar filtros e validações de tenant.
- **TASK-0110 — Perfis e permissões**: Roles e authorities base.
- **TASK-0111 — Tela de login web**: Login, botão demo e armazenamento seguro de token.
- **TASK-0112 — Proteção de rotas web**: Guardas e redirecionamento.

## R2 — Admin SaaS, planos e assinaturas

- **TASK-0201 — Criar módulo Admin SaaS**: Base para gestão da plataforma.
- **TASK-0202 — Dashboard Admin SaaS**: MRR, empresas, trials e chamados.
- **TASK-0203 — Gestão de empresas**: Listar, detalhar, bloquear e observar empresas.
- **TASK-0204 — Criar módulo de planos**: Entidade Plano e regras de módulos.
- **TASK-0205 — Planos padrão**: Estudante, Start, Care, Nutri Pro, Beauty Pro, Biomed Pro, Fisio Pro, Business, Spaces, Premium.
- **TASK-0206 — Plano Estudante**: Limites e marca d'água acadêmica.
- **TASK-0207 — Trial 30 dias**: Trial com vencimento e conversão.
- **TASK-0208 — Assinaturas**: Status, upgrade, downgrade, cancelamento e bloqueio.
- **TASK-0209 — Tela Admin de planos**: CRUD e limites por plano.
- **TASK-0210 — Dashboard de vendas**: MRR, conversão, churn e planos vendidos.

## R3 — Núcleo operacional comum

- **TASK-0301 — Clientes/Pacientes**: Cadastro e listagem multiárea.
- **TASK-0302 — Agenda base**: Agenda por profissional e sala.
- **TASK-0303 — Serviços/Procedimentos**: Cadastro genérico de serviços.
- **TASK-0304 — Custos gerais**: Custos fixos, variáveis e eventuais.
- **TASK-0305 — Alimentação e transporte**: Custos operacionais pessoais/profissionais.
- **TASK-0306 — Estoque base**: Produtos, lotes e validade.
- **TASK-0307 — Equipamentos base**: Valor, vida útil e manutenção.
- **TASK-0308 — Dashboard da empresa**: Indicadores operacionais iniciais.
- **TASK-0309 — Busca e filtros globais**: Busca em listas e filtros por categoria/status.
- **TASK-0310 — UX premium operacional**: Layout web moderno e responsivo.

## R4 — Custo real, precificação e relatórios

- **TASK-0401 — Módulo Precificação**: Base hexagonal de cálculo.
- **TASK-0402 — Calculadora de custo real**: Insumos, sala, tempo, deslocamento, alimentação, taxas.
- **TASK-0403 — Preço mínimo**: Preço para não operar no prejuízo.
- **TASK-0404 — Preço recomendado**: Cálculo por margem desejada.
- **TASK-0405 — Margem e lucro**: Margem real, lucro estimado e alertas.
- **TASK-0406 — Simulador de preço web**: Tela completa com resultado.
- **TASK-0407 — Histórico de simulações**: Salvar e editar simulações.
- **TASK-0408 — Relatório imprimível/PDF**: PDF de precificação e composição de custos.
- **TASK-0409 — Dashboard de precificação**: Indicadores e gráficos.
- **TASK-0410 — Dados demo realistas**: Procedimentos e simulações por área.

## R5 — Sublocação e Spaces

- **TASK-0501 — Módulo Spaces**: Base para salas, cadeiras, cabines e recursos.
- **TASK-0502 — Cadastro de recursos**: Salas, cadeiras, cabines e equipamentos.
- **TASK-0503 — Custo por hora de espaço**: Rateio por disponibilidade e custos fixos.
- **TASK-0504 — Pacotes de sublocação**: Hora, turno, diária, mensal, fixo + percentual.
- **TASK-0505 — Simulação do parceiro**: Lucro do profissional que subloca.
- **TASK-0506 — Agenda de ocupação**: Disponibilidade de recursos.
- **TASK-0507 — Relatório de sublocação**: PDF e indicadores.

## R6 — Documentos profissionais e carimbo

- **TASK-0601 — Módulo Documento Profissional**: Base de documentos com histórico.
- **TASK-0602 — Carimbo profissional**: Conselho, UF, número, assinatura e clínica.
- **TASK-0603 — Geração de PDF**: Templates e exportação.
- **TASK-0604 — QR Code de validação**: Validação pública limitada.
- **TASK-0605 — Marca d'água Plano Estudante**: Documento acadêmico sem validade profissional.
- **TASK-0606 — Modelos de documentos gerais**: Declaração, relatório, termo, orientação, recibo.
- **TASK-0607 — Histórico e versionamento**: Substituição, cancelamento e auditoria.

## R7 — Verticais profissionais

- **TASK-NUTRI-001 — Estruturar requisitos completos do Nutri Pro**: Especificação oficial a partir da descrição e referências visuais, sem implementação técnica.
- **TASK-AGD-001 — Estruturar módulo Agenda do AtendePro**: Especificação funcional, regras, UX, entidades e backlog da agenda comum.
- **TASK-0701 — Nutri Pro**: Plano alimentar, diário, exames, suplementação e CRN.
- **TASK-0702 — Beauty Pro**: Protocolos, fotos, termos, pacotes e salão.
- **TASK-0703 — Biomed Pro**: CRBM, habilitações, rastreabilidade e estética biomédica.
- **TASK-0704 — Fisio Pro**: CREFITO, avaliação, plano terapêutico e evolução por sessão.
- **TASK-0705 — Psico Pro futuro**: Documentos psicológicos e evolução, respeitando escopo futuro.
- **TASK-0706 — Fono Pro futuro**: Relatórios, laudos e acompanhamento.
- **TASK-0707 — Farmácia Clínica futuro**: Serviços clínicos e documentos CRF.
- **TASK-0708 — Odonto Pro futuro**: Documentos odontológicos e CRO.

## R8 — Fundação de experiência profissional do SaaS

- **TASK-UX-001 — Redesenhar shell, navegação e experiência responsiva do AtendePro**: Shell profissional responsivo, navegação real por área, progressive disclosure e acabamento premium.
- **TASK-0801 — Portal do cliente web**: Agenda, documentos e evolução.
- **TASK-0802 — App Expo base**: Estrutura mobile.
- **TASK-0803 — App do cliente/paciente**: Agenda, documentos, diário, fotos e mensagens.
- **TASK-0804 — App do profissional**: Agenda do dia, mensagens e acompanhamento.
- **TASK-0805 — Notificações push**: Lembretes e eventos.
- **TASK-0806 — Experiência mobile premium**: Design app-like.

## R9 — Suporte, central de ajuda, comunicação e refinamentos de experiência

- **TASK-0901 — Módulo Chamados**: Abertura e acompanhamento de suporte.
- **TASK-0902 — Painel Admin de Suporte**: Caixa de entrada, prioridade e status.
- **TASK-0903 — Central de ajuda**: Artigos, FAQ e tutoriais.
- **TASK-0904 — Feedback e roadmap**: Pedidos de melhoria e priorização.
- **TASK-0905 — Notificações internas**: Eventos, avisos e alertas.
- **TASK-R9-001 — Destacar visualmente simulações em alerta na Precificação**: Alertas visuais, badges, ícones e filtros para cenários saudáveis, margem baixa e prejuízo.
- **TASK-R9-002 — Criar menu rápido de ações principais do Nutri Pro**: Ações rápidas do paciente nutricional, priorizando gastos energéticos, exames laboratoriais e plano alimentar.
- **TASK-R9-003 — Corrigir acentuação e textos em português do sistema**: Revisão de textos visíveis e microcopy para português brasileiro correto.
- **TASK-R9-004 — Popular ambiente demo com usuários, planos e simulações realistas**: Seed local/demo com dados fictícios profissionais para apresentações, testes e validação visual.

## R10 — Nutri Pro, Beauty Pro e comercial

- **TASK-NUTRI-002 — Criar módulo Nutri Pro operacional**: Backend hexagonal, rotas web e base de domínio para Nutri Pro real.
- **TASK-NUTRI-003 — Criar prontuário nutricional e menu rápido funcional**: Perfil nutricional do paciente com ações rápidas reais.
- **TASK-NUTRI-004 — Criar avaliação antropométrica e gasto energético**: Registrar avaliação, IMC, TMB/GEB/GET e metas.
- **TASK-NUTRI-005 — Criar plano alimentar com refeições, alimentos e suplementos**: Plano alimentar operacional com refeições, alimentos personalizados, suplementos e macros iniciais.
- **TASK-NUTRI-006 — Criar documentos, exames, prescrições e dashboard Nutri Pro**: Solicitações, prescrições, PDF com CRN/carimbo e indicadores da vertical.
- **TASK-BEAUTY-001 — Criar módulo Beauty Pro operacional**: Backend hexagonal, rotas web e base de domínio para estética/beleza real.
- **TASK-BEAUTY-002 — Criar ficha estética, anamnese e avaliação**: Perfil estético do cliente com anamnese, objetivos, contraindicações e registros iniciais.
- **TASK-BEAUTY-003 — Criar protocolos, sessões e evolução Beauty Pro**: Protocolos faciais/corporais, pacote de sessões, execução e evolução.
- **TASK-BEAUTY-004 — Criar termos, fotos placeholder, produtos/lotes e dashboard Beauty Pro**: Termos, rastreabilidade simples, evidências seguras sem fotos reais e indicadores da vertical.
- **TASK-BEAUTY-005 — Integrar Beauty Pro com agenda, precificação e experiência web**: Tela operacional completa, agenda, serviços, custos e precificação integrados.
- **TASK-1001 — Landing page pública**: Produto, dor, solução e CTA.
- **TASK-1002 — Páginas por vertical**: Nutri, Estética, Biomed, Fisio, Spaces.
- **TASK-1003 — Página de planos**: Comparação e trial 30 dias.
- **TASK-1004 — Calculadora gratuita**: Lead magnet de preço ideal.
- **TASK-1005 — Formulário de lead**: Captação de interessados.

## R11 — IA, automações e integrações

- **TASK-1101 — IA de precificação**: Sugestões e alertas assistidos.
- **TASK-1102 — IA de suporte**: Respostas e triagem de chamados.
- **TASK-1103 — WhatsApp futuro**: Integração oficial quando configurada.
- **TASK-1104 — Pagamentos**: Integração futura com gateway.
- **TASK-1105 — Assinatura digital avançada**: ICP-Brasil/ferramenta parceira futura.

## R12 — Escala, observabilidade e produção

- **TASK-1201 — CI/CD**: GitHub Actions.
- **TASK-1202 — Observabilidade**: Logs estruturados, métricas e traces.
- **TASK-1203 — Backups e restore**: Estratégia de banco.
- **TASK-1204 — Hardening de segurança**: Headers, rate limit e auditoria.
- **TASK-1205 — Deploy produção**: Ambiente cloud/VPS.
