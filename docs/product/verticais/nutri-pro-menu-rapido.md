# Nutri Pro — Menu Rápido de Ações do Paciente

## Visão

O Menu Rápido Nutri Pro é o conjunto de ações mais usadas no perfil/prontuário nutricional do paciente. Ele deve reduzir cliques no atendimento e guiar o nutricionista para registrar avaliação, gasto energético, exames e plano alimentar sem transformar o prontuário em uma página longa.

As referências visuais são apenas inspiração funcional e de UX. O AtendePro deve manter identidade própria, premium, limpa, moderna e focada em saúde.

## Posicionamento

O menu deve aparecer no perfil/prontuário do paciente nutricional, acima do histórico detalhado e próximo ao resumo do paciente.

Fluxo esperado:

1. Nutricionista acessa lista de pacientes.
2. Seleciona o paciente.
3. Entra no perfil/prontuário nutricional.
4. Visualiza ações rápidas priorizadas.
5. Clica em uma ação.
6. O sistema abre painel, modal, drawer ou rota do fluxo correspondente.
7. Ao salvar, o registro volta para o histórico do paciente.

## Ações Prioritárias

As três ações com maior destaque inicial são:

- Adicionar gastos energéticos.
- Adicionar exames laboratoriais.
- Adicionar plano alimentar.

Essas ações devem aparecer como cards maiores, com ícone, descrição curta, status e alvo de toque confortável em desktop, tablet e celular.

## Área 1 — Avaliação

- Adicionar questionário pré-consulta.
- Adicionar anamnese.
- Adicionar avaliação antropométrica.
- Adicionar gastos energéticos.
- Adicionar exames laboratoriais.
- Adicionar recordatório alimentar.
- Adicionar avaliação DB360, se for mantida como opção futura.

## Área 2 — Prescrição e acompanhamento

- Adicionar plano alimentar.
- Adicionar prescrições.
- Adicionar metas.
- Adicionar lista de compras.
- Adicionar diário alimentar.

## Microcopy Obrigatória

> Recomendamos realizar anamnese, avaliação antropométrica e cálculo de gasto energético para elaborar um plano alimentar mais preciso.

## Estados das Ações

- `Prioritária`: ação central do fluxo da nutricionista.
- `Planejada`: ação prevista para o módulo Nutri Pro, mas dependente de task específica.
- `Em breve`: ação futura ou dependente de app/portal/persistência ainda não implementados.
- `Bloqueada`: ação não permitida no plano atual ou sem CRN quando exigir documento oficial.

## UX Responsiva

Desktop:
- Cards principais em 3 colunas.
- Ações secundárias em duas áreas.
- Detalhes podem abrir em painel lateral ou drawer.

Tablet:
- Cards principais em 2 colunas quando necessário.
- Ações secundárias compactas.
- Alvos de toque confortáveis.

Mobile:
- Uma seção por vez.
- Cards empilhados ou carrossel horizontal.
- Botões grandes.
- Formulários posteriores em etapas ou bottom sheet.

## Plano Estudante

No Plano Estudante:

- Ações podem funcionar em modo simulado quando fizer sentido.
- Documentos oficiais devem ser bloqueados sem CRN.
- Documentos acadêmicos devem sair com marca d'água.
- O sistema deve informar claramente quando uma ação é acadêmica, simulada ou bloqueada.

## Regras de Escopo

- O menu rápido não decide conduta nutricional.
- O sistema apenas organiza registros e documentos.
- Cálculos energéticos são estimativos e exigem validação do profissional.
- Nenhuma ação deve gerar PDF oficial sem regras de carimbo, CRN e assinatura.
- Ações futuras devem aparecer como `Em breve`, sem prometer fluxo inexistente.

## Backlog Relacionado

- `TASK-NUTRI-003` deve criar o perfil/prontuário nutricional.
- `TASK-NUTRI-004` deve criar o plano alimentar por paciente.
- `TASK-NUTRI-011` deve criar solicitações de exames laboratoriais.
- `TASK-NUTRI-013` deve criar avaliação antropométrica.
- `TASK-NUTRI-014` deve criar cálculo de TMB e gasto energético.
- `TASK-NUTRI-026` deve implementar o menu rápido no prontuário nutricional real quando o prontuário existir.
