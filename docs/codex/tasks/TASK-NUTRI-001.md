# TASK-NUTRI-001 — Estruturar requisitos completos do Nutri Pro a partir da descrição e referências visuais

## Release
R7 — Verticais profissionais

## Complexidade
MÉDIA

## Objetivo
Transformar a descrição e as referências visuais do Nutri Pro em especificação oficial de produto, definindo requisitos, fluxos, telas, regras de negócio, entidades/conceitos e futuras tasks de implementação.

## Contexto
Foi descrito o fluxo necessário para atender a área de Nutrição no AtendePro, com apoio de prints de referência de mercado. As imagens mostram funcionalidades como planos comerciais, app do paciente, agenda, pacientes, relatórios, perfil do paciente, plano alimentar, refeições, resumo de macronutrientes, avaliação antropométrica, gasto energético, solicitações de exames, prescrições, chat, lista de compras e ferramentas de fidelização.

As referências visuais são apenas inspiração funcional e de UX. Não copiar layout, marca, textos, identidade visual ou design de terceiros. O AtendePro deve ter identidade própria: premium, limpo, moderno, focado em saúde, gestão, acompanhamento e custo real.

## Escopo Permitido
- Criar documentação oficial do Nutri Pro.
- Organizar requisitos funcionais.
- Organizar regras de negócio.
- Definir fluxos principais.
- Definir telas necessárias.
- Definir entidades/conceitos de domínio.
- Definir futuras tasks de implementação.
- Atualizar roadmap e backlog quando necessário.

## Fora de Escopo
- Não implementar backend.
- Não implementar frontend.
- Não criar banco.
- Não importar tabelas alimentares reais.
- Não criar PDF.
- Não criar app mobile.
- Não copiar design de terceiros.
- Não fazer push.

## Documentos Esperados
- `docs/product/verticais/nutri-pro.md`
- `docs/product/verticais/nutri-pro-requisitos.md`
- `docs/product/verticais/nutri-pro-fluxos.md`
- `docs/product/verticais/nutri-pro-telas.md`
- `docs/product/verticais/nutri-pro-regras-negocio.md`
- `docs/product/verticais/nutri-pro-backlog.md`
- `docs/TASKS.md`
- `docs/ROADMAP_RELEASES.md`
- `docs/RELEASE_STATUS.yaml`

## Critérios de Aceite
- A descrição funcional foi transformada em documentação oficial.
- As referências visuais foram analisadas sem copiar design.
- Requisitos foram organizados por tema.
- Fluxos principais foram documentados.
- Telas futuras foram listadas.
- Regras de negócio foram documentadas.
- Tasks futuras foram sugeridas.
- Roadmap/backlog foi atualizado.
- Nenhuma implementação técnica foi feita fora do escopo.
- Commit local foi criado.
- Push não foi realizado.

## Validação
- Verificar existência dos documentos criados.
- Verificar que a task está registrada em `docs/TASKS.md`.
- Verificar que a release R7 foi atualizada em `docs/ROADMAP_RELEASES.md` e `docs/RELEASE_STATUS.yaml`.
- Conferir `git status` antes do commit.

## Prompt Recomendado

```md
Execute TASK-NUTRI-001 — Estruturar requisitos completos do Nutri Pro a partir da descrição e referências visuais seguindo o Harness Profissional.
Crie somente documentação oficial e atualize roadmap/backlog/status. Não implemente backend, frontend, banco, PDF ou mobile. Faça commit local e não faça push.
```
