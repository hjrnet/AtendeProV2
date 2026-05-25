# Nutri Pro — Visão Oficial

## Propósito

O Nutri Pro é o módulo do AtendePro para nutricionistas, clínicas de nutrição, consultórios, estudantes de nutrição e equipes que precisam acompanhar pacientes com plano alimentar, avaliação antropométrica, gasto energético, documentos nutricionais e experiência digital do paciente.

O módulo deve funcionar como uma vertical profissional sobre o núcleo comum do AtendePro, evitando duplicação de cadastros e aproveitando login, tenant, usuários, clientes/pacientes, agenda, documentos, custos, precificação, relatórios, dashboard, planos, permissões e app do cliente/profissional.

## Princípios de Produto

- O AtendePro deve ter identidade própria: premium, limpo, moderno, focado em saúde, gestão, acompanhamento e custo real.
- As referências visuais de mercado servem apenas como inspiração funcional e de UX. Não copiar layout, marca, textos, identidade visual, fluxos proprietários ou design de terceiros.
- O sistema apoia o nutricionista, mas não decide conduta clínica automaticamente.
- Documentos, prescrições, planos e orientações são responsabilidade técnica do profissional.
- Cálculos nutricionais são estimativos e devem ser validados pelo nutricionista.
- A experiência deve ser forte em desktop para atendimento e planejamento, mas responsiva para tablet/celular e preparada para app do paciente.

## Núcleo Reaproveitado

- Login e segurança.
- Empresa/tenant.
- Usuários e permissões.
- Clientes/pacientes.
- Agenda.
- Documentos profissionais e carimbo.
- Custos, precificação e relatórios.
- Dashboard operacional.
- Planos, assinaturas e Plano Estudante.
- App do cliente/profissional em release futura.

## Capacidades Específicas

- Perfil nutricional do paciente.
- Prontuário nutricional centralizado.
- Plano alimentar por paciente.
- Refeições, horários, alimentos, suplementos e substituições.
- Banco de alimentos padrão e personalizado.
- Banco de suplementos e formulações.
- Cálculo de energia, macronutrientes e resumo diário.
- Avaliação antropométrica.
- Estimativa de TMB, GEB e GET.
- Solicitação de exames laboratoriais.
- Prescrições dietéticas, suplementares e fitoterápicas quando habilitado.
- PDF do plano alimentar com carimbo, CRN e assinatura virtual.
- Lista de compras.
- Receitas e materiais educativos.
- App/portal do paciente.
- App do profissional.
- Relatórios nutricionais.

## Entidades e Conceitos de Domínio

Conceitos iniciais a considerar nas futuras tasks de implementação:

- Paciente nutricional.
- Prontuário nutricional.
- Anamnese nutricional.
- Plano alimentar.
- Dia do plano.
- Refeição.
- Item alimentar da refeição.
- Substituição alimentar.
- Alimento padrão.
- Alimento personalizado.
- Suplemento/formulação.
- Prescrição nutricional.
- Avaliação antropométrica.
- Gasto energético.
- Solicitação de exame.
- Documento nutricional.
- Carimbo CRN.
- Lista de compras.
- Diário alimentar.
- Meta nutricional.
- Receita/material educativo.
- Configuração de exibição do plano.
- Histórico de acompanhamento.

Esses conceitos devem ser refinados em Commands, Results, UseCases, InputPorts, OutputPorts e Adapters quando as tasks técnicas forem promovidas.

## Fora de Escopo Desta Especificação

- Não implementar backend.
- Não implementar frontend.
- Não criar banco.
- Não importar tabelas alimentares reais.
- Não criar PDF.
- Não criar app mobile.
- Não copiar design de terceiros.

## Documentos Relacionados

- `docs/product/verticais/nutri-pro-requisitos.md`
- `docs/product/verticais/nutri-pro-fluxos.md`
- `docs/product/verticais/nutri-pro-telas.md`
- `docs/product/verticais/nutri-pro-regras-negocio.md`
- `docs/product/verticais/nutri-pro-backlog.md`
