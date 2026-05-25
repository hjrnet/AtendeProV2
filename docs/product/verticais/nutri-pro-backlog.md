# Nutri Pro — Backlog Detalhado

## Task de Especificação

| Task | Nome | Objetivo | Status |
|---|---|---|---|
| TASK-NUTRI-001 | Estruturar requisitos completos do Nutri Pro | Transformar descrição e referências em especificação oficial. | CONCLUIDA |

## Tasks Futuras Sugeridas

| Task | Nome | Objetivo | Dependências |
|---|---|---|---|
| TASK-NUTRI-002 | Criar módulo Nutri Pro operacional | Criar base backend hexagonal da vertical, rotas web e status operacional. | TASK-NUTRI-001 |
| TASK-NUTRI-003 | Criar prontuário nutricional e menu rápido funcional | Estender paciente com dados nutricionais, prontuário base e ações rápidas reais. | TASK-NUTRI-002 |
| TASK-NUTRI-004 | Criar avaliação antropométrica e gasto energético | Registrar avaliação, IMC, TMB/GEB/GET e metas. | TASK-NUTRI-003 |
| TASK-NUTRI-005 | Criar plano alimentar com refeições, alimentos e suplementos | Modelar plano alimentar operacional, refeições, alimentos personalizados, suplementos e macros iniciais. | TASK-NUTRI-003, TASK-NUTRI-004 |
| TASK-NUTRI-006 | Criar documentos, exames, prescrições e dashboard Nutri Pro | Solicitações, prescrições, PDF com CRN/carimbo e indicadores da vertical. | TASK-NUTRI-005 |
| TASK-NUTRI-007 | Criar cadastro de alimento personalizado | Permitir alimentos tenant-scoped reutilizáveis. | TASK-NUTRI-005 |
| TASK-NUTRI-008 | Criar banco de suplementos e formulações | Modelar suplementos, dose, composição e orientação de uso. | TASK-NUTRI-005 |
| TASK-NUTRI-009 | Calcular energia e macronutrientes do plano | Calcular energia, macros, percentuais e resumos. | TASK-NUTRI-005 |
| TASK-NUTRI-010 | Criar PDF do plano alimentar com carimbo CRN | Gerar PDF com refeições, macros opcionais, carimbo e CRN. | TASK-NUTRI-006 |
| TASK-NUTRI-011 | Criar solicitação de exames laboratoriais | Criar documento de exames por paciente. | TASK-NUTRI-006 |
| TASK-NUTRI-012 | Criar histórico de solicitações de exames | Duplicar, listar e comparar solicitações anteriores. | TASK-NUTRI-011 |
| TASK-NUTRI-013 | Criar avaliação antropométrica avançada | Evoluir circunferências, dobras, bioimpedância e comparação. | TASK-NUTRI-004 |
| TASK-NUTRI-014 | Criar cálculo de TMB e gasto energético avançado | Evoluir fórmulas configuráveis e histórico de estimativas. | TASK-NUTRI-004 |
| TASK-NUTRI-015 | Criar prescrição de suplementação avançada | Criar documentos de suplementação/formulações mais completos. | TASK-NUTRI-006, TASK-NUTRI-008 |
| TASK-NUTRI-016 | Criar configuração de carimbo profissional | Garantir CRN/UF/profissão e assinatura para Nutri Pro. | R6 documentos |
| TASK-NUTRI-017 | Criar marca d'água para Plano Estudante | Aplicar regras acadêmicas específicas em documentos Nutri. | R6 documentos, R2 planos |
| TASK-NUTRI-018 | Criar lista de compras | Gerar lista baseada no plano alimentar. | TASK-NUTRI-005 |
| TASK-NUTRI-019 | Criar app/portal do paciente para plano alimentar | Disponibilizar plano, refeições e lista de compras. | R8 base mobile/web |
| TASK-NUTRI-020 | Criar diário alimentar | Registro de refeições, fotos e observações do paciente. | TASK-NUTRI-019 |
| TASK-NUTRI-021 | Criar lembretes de refeição e hidratação | Notificações e lembretes configuráveis. | TASK-NUTRI-019 |
| TASK-NUTRI-022 | Criar dashboard Nutri Pro avançado | Evoluir indicadores da vertical para profissional/clínica. | TASK-NUTRI-006 |
| TASK-NUTRI-023 | Criar relatórios nutricionais | Relatórios de pacientes, planos, evolução e perfil. | TASK-NUTRI-022 |
| TASK-NUTRI-024 | Criar assistente de refeições | Apoiar montagem de refeições com filtros e sugestões sem decisão automática. | TASK-NUTRI-005 |
| TASK-NUTRI-025 | Criar materiais e receitas | Biblioteca de receitas, materiais e orientações. | TASK-NUTRI-002 |
| TASK-NUTRI-026 | Implementar menu rápido no prontuário nutricional | Evoluir ações rápidas reais no perfil do paciente. | TASK-NUTRI-003 |

## Observações de Planejamento

- `TASK-NUTRI-002` a `TASK-NUTRI-006` foram promovidas para execução oficial na R10.
- As tasks futuras após `TASK-NUTRI-006` permanecem como evolução posterior.
- Nenhuma task futura deve ser implementada dentro da TASK-NUTRI-001.
- Banco alimentar real, assinatura digital avançada, QR Code nutricional e app mobile ficam em fases posteriores.
