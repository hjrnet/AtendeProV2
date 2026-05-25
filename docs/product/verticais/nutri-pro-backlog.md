# Nutri Pro — Backlog Detalhado

## Task de Especificação

| Task | Nome | Objetivo | Status |
|---|---|---|---|
| TASK-NUTRI-001 | Estruturar requisitos completos do Nutri Pro | Transformar descrição e referências em especificação oficial. | CONCLUIDA |

## Tasks Futuras Sugeridas

| Task | Nome | Objetivo | Dependências |
|---|---|---|---|
| TASK-NUTRI-002 | Criar módulo Nutri Pro | Criar base backend hexagonal da vertical e status operacional. | TASK-NUTRI-001 |
| TASK-NUTRI-003 | Criar perfil nutricional do paciente | Estender paciente com dados nutricionais e prontuário base. | TASK-NUTRI-002 |
| TASK-NUTRI-004 | Criar plano alimentar por paciente | Modelar plano alimentar com status, objetivo, dias e versão inicial. | TASK-NUTRI-003 |
| TASK-NUTRI-005 | Criar refeições do plano alimentar | Criar refeições, horários, ordenação, observações e substituições. | TASK-NUTRI-004 |
| TASK-NUTRI-006 | Criar banco de alimentos | Modelar alimentos padrão/personalizados sem importar base real ainda. | TASK-NUTRI-002 |
| TASK-NUTRI-007 | Criar cadastro de alimento personalizado | Permitir alimentos tenant-scoped reutilizáveis. | TASK-NUTRI-006 |
| TASK-NUTRI-008 | Criar banco de suplementos e formulações | Modelar suplementos, dose, composição e orientação de uso. | TASK-NUTRI-002 |
| TASK-NUTRI-009 | Calcular energia e macronutrientes do plano | Calcular energia, macros, percentuais e resumos. | TASK-NUTRI-004, TASK-NUTRI-006 |
| TASK-NUTRI-010 | Criar PDF do plano alimentar com carimbo CRN | Gerar PDF com refeições, macros opcionais, carimbo e CRN. | TASK-NUTRI-009 |
| TASK-NUTRI-011 | Criar solicitação de exames laboratoriais | Criar documento de exames por paciente. | TASK-NUTRI-003 |
| TASK-NUTRI-012 | Criar histórico de solicitações de exames | Duplicar, listar e comparar solicitações anteriores. | TASK-NUTRI-011 |
| TASK-NUTRI-013 | Criar avaliação antropométrica | Registrar peso, altura, IMC, objetivo e observações. | TASK-NUTRI-003 |
| TASK-NUTRI-014 | Criar cálculo de TMB e gasto energético | Estimar TMB, GEB e GET com validação profissional. | TASK-NUTRI-013 |
| TASK-NUTRI-015 | Criar prescrição de suplementação | Criar documentos de suplementação/formulações. | TASK-NUTRI-008 |
| TASK-NUTRI-016 | Criar configuração de carimbo profissional | Garantir CRN/UF/profissão e assinatura para Nutri Pro. | R6 documentos |
| TASK-NUTRI-017 | Criar marca d'água para Plano Estudante | Aplicar regras acadêmicas específicas em documentos Nutri. | R6 documentos, R2 planos |
| TASK-NUTRI-018 | Criar lista de compras | Gerar lista baseada no plano alimentar. | TASK-NUTRI-004, TASK-NUTRI-006 |
| TASK-NUTRI-019 | Criar app/portal do paciente para plano alimentar | Disponibilizar plano, refeições e lista de compras. | R8 base mobile/web |
| TASK-NUTRI-020 | Criar diário alimentar | Registro de refeições, fotos e observações do paciente. | TASK-NUTRI-019 |
| TASK-NUTRI-021 | Criar lembretes de refeição e hidratação | Notificações e lembretes configuráveis. | TASK-NUTRI-019 |
| TASK-NUTRI-022 | Criar dashboard Nutri Pro | Indicadores da vertical para profissional/clínica. | TASK-NUTRI-003, TASK-NUTRI-004 |
| TASK-NUTRI-023 | Criar relatórios nutricionais | Relatórios de pacientes, planos, evolução e perfil. | TASK-NUTRI-022 |
| TASK-NUTRI-024 | Criar assistente de refeições | Apoiar montagem de refeições com filtros e sugestões sem decisão automática. | TASK-NUTRI-005, TASK-NUTRI-006 |
| TASK-NUTRI-025 | Criar materiais e receitas | Biblioteca de receitas, materiais e orientações. | TASK-NUTRI-002 |

## Observações de Planejamento

- As tasks futuras ainda precisam de arquivos individuais em `docs/codex/tasks/` quando forem promovidas para execução oficial.
- Nenhuma task futura deve ser implementada dentro da TASK-NUTRI-001.
- Banco alimentar real, assinatura digital avançada, QR Code nutricional e app mobile ficam em fases posteriores.
