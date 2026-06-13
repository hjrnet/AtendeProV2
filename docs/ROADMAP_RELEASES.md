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
- **TASK-R13-001 — Portal do cliente web**: Agenda, documentos e evolução.
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
- **TASK-1006 — Corrigir responsividade do hero e painel operacional público**: Ajuste visual da landing para eliminar sobreposição/desalinhamento do painel operacional em mudanças de viewport.
- **TASK-1007 — Redesenhar workspace profissional Nutri Pro e navegação por perfil**: Refatoração da área logada para exibir workspace por profissão/plano, navegação real sem scroll/empilhamento, layout responsivo premium e padrão reaplicável às demais verticais.
- **TASK-1008 — Refatorar UX Nutri Pro com telas dedicadas e submenus limpos**: Reduzir poluição visual e criar conteúdo específico por seção, com submenus para plano alimentar, avaliações, agenda, pacientes, prontuário e documentos.
- **TASK-1009 — Refatorar UX Beauty Pro com telas dedicadas e submenus limpos**: Aplicar o padrão aprovado no Nutri Pro ao workspace de estética, separando ficha, protocolos, termos/evidências/produtos, agenda/preços, clientes e precificação em telas e submenus próprios.
- **TASK-1010 — Ativar ações operacionais do Nutri Pro, cadastro de paciente e criação de agenda**: Tornar atalhos do Início Nutri e próximas evoluções acionáveis, cadastrar paciente Nutri e criar agendamento usando o núcleo comum.
- **TASK-1011 — Ajustar espaçamento vertical excessivo do hero público**: Reduzir o vazio acima e abaixo do bloco principal da landing, mantendo proporção premium, painel operacional alinhado e responsividade em desktop, tablet e mobile.

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

## R13 — Portal do cliente web

- **TASK-R13-001 — Portal do cliente web**: Agenda, documentos e evolução.

## R14 — Alinhamento funcional e completude do produto

- **TASK-R14-001 — Correção de governança de conclusão e contrato de status de funcionalidades futuras**: revisar e corrigir `RELEASE_STATUS/TASKS/SESSION_STATE` para que `CONCLUIDA` reflita apenas produção real.
- **TASK-R14-002 — Inventário técnico de lacunas e plano de estabilização de verticais futuras**: mapear o que é catálogo vs operação para Biomed/Fisio/Psico/Fono/Farmácia/Odonto e definir o próximo slice operacional.
- **TASK-R14-003 — Conectar autenticação e shell mobile ao backend real**: login, sessão persistida, tenant/empresa e telas de recuperação de contexto.
- **TASK-R14-004 — Conectar os fluxos mobile (cliente/profissional) a dados reais**: agenda, clientes/pacientes e documentos via API real, com estados de vazio/erro/loading.

## R15 — Beauty Pro estoque, validade e margem operacional

- **TASK-BEAUTY-006 — Criar Estoque Beauty completo no workspace**: cadastro e listagem de produtos, lotes, validade, quantidade, custo unitário, estoque mínimo, filtros por vencimento e estado vazio.
- **TASK-BEAUTY-007 — Criar movimentações e baixa automática de produtos**: entrada, ajuste, saída manual e baixa por sessão/protocolo, preservando histórico por produto, cliente, profissional e atendimento.
- **TASK-BEAUTY-008 — Criar painel de validade, ruptura e consumo**: alertas de produto vencido, vence em 7/15/30 dias, estoque baixo, consumo por procedimento e impacto na margem.
- **TASK-BEAUTY-009 — Criar kits de insumos por procedimento**: configurar produtos esperados por protocolo/serviço para estimar custo, sugerir baixa e alertar divergências.
- **TASK-BEAUTY-010 — Popular demo Beauty com estoque realista**: produtos, lotes, validades próximas, vencidos, estoque baixo, pacotes e sessões para apresentação comercial.

## R16 — Pós-venda moderno para Nutri Pro e Beauty Pro

- **TASK-CRM-001 — Criar núcleo de relacionamento e pós-venda**: carteira de clientes/pacientes, status de acompanhamento, retorno recomendado, aniversários, faltas e clientes inativos.
- **TASK-CRM-002 — Criar tarefas e lembretes pós-atendimento**: tarefas automáticas por evento de agenda, sessão, plano alimentar, protocolo, retorno e ausência.
- **TASK-CRM-003 — Criar templates de mensagem e WhatsApp-ready**: mensagens manuais com variáveis, link direto de WhatsApp, histórico de contato e preparo para integração oficial futura.
- **TASK-CRM-004 — Criar NPS e pesquisa de satisfação**: pesquisa pós-consulta/procedimento, registro de nota, comentário e alerta de insatisfação.
- **TASK-CRM-005 — Criar segmentação e campanhas simples**: filtros por área, serviço, pacote, risco de abandono, produto usado, plano ativo e última consulta.
- **TASK-CRM-006 — Criar dashboard de pós-venda**: retornos pendentes, reativações, faltas, NPS, clientes sem contato e oportunidades de recorrência.

## R17 — Nutri Pro experiência do paciente e acompanhamento contínuo

- **TASK-NUTRI-007 — Publicar plano alimentar no portal/app do paciente**: plano ativo, refeições, horários, suplementos, observações e histórico de versões.
- **TASK-NUTRI-008 — Criar lista de compras do plano alimentar**: geração por plano, agrupamento por categoria, edição e compartilhamento com o paciente.
- **TASK-NUTRI-009 — Criar diário alimentar do paciente**: registro de refeição por texto e evidência segura, status de revisão e visão do nutricionista.
- **TASK-NUTRI-010 — Criar metas, lembretes e evolução do paciente**: hidratação, refeições, peso, medidas, metas semanais e linha do tempo.
- **TASK-NUTRI-011 — Criar recados/chat operacional Nutri**: troca de mensagens assíncronas entre paciente e nutricionista, com status de leitura e contexto do acompanhamento.
- **TASK-NUTRI-012 — Integrar acompanhamento Nutri ao mobile real**: conectar agenda, plano, diário, mensagens e notificações aos endpoints reais.

## R18 — Nutri Pro plano alimentar avançado e produtividade clínica

- **TASK-NUTRI-013 — Criar banco de alimentos e suplementos**: alimentos padrão, alimentos personalizados, suplementos, composição nutricional e origem do item.
- **TASK-NUTRI-014 — Criar editor avançado de plano alimentar**: duplicar plano, versionar, arquivar, substituir, reorganizar refeições e salvar modelos.
- **TASK-NUTRI-015 — Criar substituições e equivalências alimentares**: lista de substituições por refeição, por objetivo e por restrição alimentar.
- **TASK-NUTRI-016 — Criar receitas e materiais educativos**: biblioteca de receitas, orientações, materiais por objetivo e anexos ao plano.
- **TASK-NUTRI-017 — Evoluir antropometria e exames**: circunferências, dobras, bioimpedância, comparação longitudinal e histórico de exames.
- **TASK-NUTRI-018 — Criar relatórios nutricionais gerenciais**: pacientes ativos, evolução, adesão, planos emitidos, retornos e perfil da carteira.

## R19 — Growth, inteligência e refinamento comercial das duas verticais

- **TASK-GROWTH-001 — Criar funil de leads por vertical**: captação, origem, etapa, conversão em cliente/paciente e vínculo com agenda.
- **TASK-GROWTH-002 — Criar automações assistidas por IA para pós-venda**: sugestões de mensagem, risco de abandono, retorno recomendado e oportunidade de pacote.
- **TASK-GROWTH-003 — Criar indicadores de negócio por vertical**: faturamento previsto, ocupação, recorrência, margem, recompra e ticket médio.
- **TASK-GROWTH-004 — Criar apresentações demo por perfil**: roteiros e dados demo para Nutri, Beauty, gestor e investidor.
## R20 — Growth UI e comando comercial

- **TASK-R20-001 — Tela Growth no web app**: Funil visual de leads por vertical, filtros, cadastro rápido, atualização de etapa e vínculo com cliente/agenda.
- **TASK-R20-002 — Dashboard executivo Nutri/Beauty**: Cards e gráficos para faturamento previsto, agenda futura, ticket médio, margem, recompra e recorrência.
- **TASK-R20-003 — Central de pós-venda assistida**: Tela com clientes em risco, mensagem sugerida, oportunidade de pacote e ação recomendada.
- **TASK-R20-004 — Modo apresentação/demo navegável**: Experiência guiada para apresentar Nutri, Beauty, gestor e investidor usando roteiros e dados demo.

## R21 — Beauty Pro estoque avançado, compras e margem real

- **TASK-R21-001 — Reposição e pedidos de compra Beauty**: Sugestão de reposição, lista de compras por estoque mínimo/validade e controle de status do pedido.
- **TASK-R21-002 — Fornecedores e custo por lote**: Cadastro de fornecedores, histórico de custo por lote, comparação de preço e rastreabilidade de compra.
- **TASK-R21-003 — Rotina operacional de validade**: Ações para produtos vencendo/vencidos, descarte, bloqueio de uso e checklist semanal.
- **TASK-R21-004 — Margem real por procedimento Beauty**: Relatório cruzando kits, baixa de produtos, preço do serviço, custo real e lucro por procedimento/pacote.

## R22 — Nutri Pro engajamento, evolução e retenção

- **TASK-R22-001 — Painel de adesão do paciente Nutri**: Consolidar diário, metas, plano alimentar, mensagens e alertas de baixa adesão.
- **TASK-R22-002 — Evolução clínica visual Nutri**: Gráficos longitudinais de peso, medidas, exames, metas e comparação de períodos.
- **TASK-R22-003 — Renovação e retorno inteligente Nutri**: Fluxo de renovação de plano, retorno recomendado, pacotes de acompanhamento e mensagens assistidas.
- **TASK-R22-004 — Biblioteca profissional Nutri exportável**: Materiais, receitas e orientações com vínculo ao plano, carimbo profissional e exportação segura.

## R23 — Mobile real paciente/profissional

- **TASK-R23-001 — Resolver login e shell mobile real**: Conectar autenticação, sessão, tenant e shell Expo ao backend real, fechando a dívida da R14 mobile.
- **TASK-R23-002 — App paciente real Nutri/Beauty**: Agenda, plano/rotina, pós-venda, mensagens e documentos consumindo APIs reais.
- **TASK-R23-003 — App profissional real**: Agenda do dia, clientes prioritários, tarefas, checklists e ações rápidas para Nutri/Beauty.
- **TASK-R23-004 — Notificações e resiliência mobile**: Preparar push/local notifications, estados offline básicos, loading, erro e retry.

## R24 — Comercialização SaaS, onboarding e métricas admin

- **TASK-R24-001 — Checkout e assinatura self-service**: Preparar fluxo de escolha de plano, trial, upgrade/downgrade e integração futura com gateway real.
- **TASK-R24-002 — Onboarding guiado de empresa**: Wizard de configuração inicial por vertical, usuários, serviços, agenda e dados demo opcionais.
- **TASK-R24-003 — Métricas SaaS no Admin**: MRR, churn, trials, conversão, planos ativos, vertical de maior tração e alertas operacionais.
- **TASK-R24-004 — Ambiente demo/reset por perfil**: Gerar e resetar dados demo seguros para Nutri, Beauty, gestor, investidor e suporte.

## R20 concluida em 2026-06-11

- Funil visual de leads por vertical no web app.
- Dashboard executivo Nutri/Beauty com indicadores Growth.
- Central de pos-venda assistida com sugestoes e oportunidades.
- Modo apresentacao/demo navegavel por perfil.

## R21 concluida em 2026-06-11

- Reposicao e pedidos de compra Beauty por minimo, validade e custo estimado.
- Fornecedor, documento, pedido, data e status de compra persistidos por lote.
- Checklist operacional para vencidos, vencendo, descarte, bloqueio e reposicao.
- Margem real por procedimento Beauty cruzando kits, custo dos lotes e simulacoes de preco.

## R22 concluida em 2026-06-11

- Entrega: painel de adesao Nutri, evolucao clinica visual, retorno inteligente e biblioteca profissional exportavel com carimbo/documento.
- Proxima release: R23 Mobile real paciente/profissional.

## R23 concluida em 2026-06-11

- Entrega: mobile real paciente/profissional consumindo auth, agenda, documentos, Nutri, Beauty, pos-venda, Growth e notificacoes agregadas.
- Risco tecnico: criar endpoint mobile/me para eliminar resolucao provisoria de primeiro paciente/cliente em producao.
- Proxima release: R24 Comercializacao SaaS, onboarding e metricas admin.

## R24 concluida em 2026-06-11

- Entrega: comercializacao SaaS, onboarding, metricas admin e ambiente demo/reset seguro por perfil no Admin SaaS web.
- Proxima etapa: definir backlog R25 com endpoint mobile/me, gateway real ou reset demo backend conforme prioridade de negocio.
## R25 — Mobile seguro por usuario autenticado

Status: CONCLUIDA em 2026-06-11.

Objetivo: fechar a principal divida de producao multiusuario deixada pela R23, criando um contrato mobile dedicado para resolver usuario, empresa e vinculos de cliente/paciente sem depender do primeiro cliente do tenant.

Entregas:

- `GET /api/mobile/me` com perfil, empresa ativa, papel principal e clientes vinculados por email dentro do tenant.
- App mobile validando o perfil mobile apos login.
- Resolvers Nutri/Beauty passam a bloquear fallback tenant quando o usuario autenticado exige vinculo de cliente.
- Documentacao oficial da release e tasks R25.

Proxima etapa recomendada: R26 Admin SaaS real com gateway/assinaturas ou R26 reset demo backend controlado por perfil.

## R26 — Reset demo backend controlado por perfil

Status: CONCLUIDA em 2026-06-11.

Objetivo: permitir que o Admin SaaS repopule a massa demo local por perfil de apresentacao sem acionar gateway real, webhooks ou deletes destrutivos.

Entregas:

- Endpoint local `POST /api/admin-saas/demo/reset`.
- Perfis Nutri, Beauty, Gestor, Investidor e Suporte com etapas, credenciais e avisos.
- Reuso da massa demo deterministica existente em `DadosDemoLocalRunner`.
- Cockpit Admin SaaS executando reset seguro e exibindo resultado operacional.

Proxima etapa recomendada: R27 automacao de sincronizacao GitHub/observability ou gateway real com escopo aprovado.

## R27 — Observability e sincronizacao GitHub automatica

Status: CONCLUIDA em 2026-06-11.

Objetivo: impedir divergencia entre releases concluidas localmente e GitHub Issues/Milestones abertas.

Entregas:

- `scripts/github-release-finalize.ps1` para finalizar releases no GitHub com dry-run, `-Apply` e `-EnsureIssues`.
- `scripts/github-project-sync.ps1` atualizado para releases recentes e labels dinamicas por release.
- Documentacao do fluxo anti-divergencia em GitHub Projects e Observability.
- Dogfood previsto: R27 usa o proprio finalizador apos merge.

Proxima etapa recomendada: R28 gateway real com decisao explicita ou Admin SaaS auditoria operacional.
## R28 — Auditoria operacional Admin SaaS

Status: CONCLUIDA em 2026-06-11.

Objetivo: criar rastreabilidade operacional para o Admin SaaS antes de avancar para cobranca/gateway real.

Entregas:

- Tabela `admin_saas_auditoria_eventos` para eventos administrativos sensiveis.
- Endpoint `GET /api/admin-saas/auditoria/operacional` com indicadores, checklist e eventos recentes.
- Registro automatico de bloqueio/desbloqueio de empresa e reset demo local.
- Aba web `Auditoria R28` no Admin SaaS com visao executiva e operacional.
- Auditoria documental strict mantida como criterio de fechamento.

Proxima etapa recomendada: R29 gateway real de assinaturas/pagamentos com provedor definido.

## R29 — Gateway real de assinaturas e pagamentos

Status: CONCLUIDA em 2026-06-13.

Objetivo: preparar e iniciar a integracao real de assinaturas/pagamentos com provedor definido, preservando governanca, seguranca operacional, auditoria e modo sandbox antes de efeitos financeiros reais.

Entregas planejadas:

- Governanca R29 formalizada, com push manual e auditoria anti-divergencia ampliada.
- Provedor de pagamento definido com contrato tecnico, variaveis de ambiente, webhooks e modo sandbox.
- Implementacao futura deve manter gateway externo isolado por portas/adapters e nao acionar cobranca real sem configuracao explicita.
- Contrato tecnico registrado em `docs/architecture/PAGAMENTOS_GATEWAY_R29.md`, com Asaas como provedor aprovado.

Tasks:

- TASK-R29-001: Formalizar governanca R29 e politica de push manual — CONCLUIDA.
- TASK-R29-002: Definir provedor e contrato tecnico do gateway — CONCLUIDA.

Proxima etapa recomendada: R30 implementacao segura do modulo de pagamentos em sandbox.

## R30 — Pagamentos sandbox com arquitetura hexagonal

Status: CONCLUIDA em 2026-06-13.

Objetivo: implementar a primeira base tecnica do modulo de pagamentos em sandbox, usando o contrato definido na R29 e preservando isolamento por portas/adapters antes de qualquer cobranca real.

Entregas planejadas:

- Modulo backend `pagamento` em arquitetura hexagonal.
- Contratos para checkout, assinatura, pagamento e webhook em sandbox.
- Persistencia minima para assinaturas, pagamentos e eventos externos.
- Adapter Asaas em sandbox, com integracao desabilitada por padrao e sem credenciais reais versionadas.
- Auditoria operacional para eventos sensiveis de pagamento.

Tasks:

- TASK-R30-001: Criar modulo pagamento sandbox — CONCLUIDA.

## R31 — Admin SaaS Web com pagamentos sandbox

Status: CONCLUIDA em 2026-06-13.

Objetivo: conectar o cockpit Admin SaaS ao modulo `pagamento` criado na R30, permitindo preparar checkout sandbox, simular webhook seguro e visualizar status operacional sem cobranca real.

Entregas planejadas:

- Consulta backend de pagamentos sandbox para o Admin SaaS.
- API client web tipado para checkout, webhook e listagem de pagamentos.
- Painel web no cockpit Admin SaaS com status, eventos e acoes sandbox.
- Validacao backend/web/local sem producao e sem credenciais reais.

Tasks:

- TASK-R31-001: Integrar cockpit Admin SaaS ao pagamento sandbox — CONCLUIDA.
