# TASK-R9-002 — Criar menu rápido de ações principais do Nutri Pro

## Release
R9 — Suporte, central de ajuda, comunicação e refinamentos de experiência

## Complexidade
MÉDIA

## Tipo
Produto, UX Nutri Pro e futura implementação frontend.

## Contexto
A nutricionista Karol informou que uma das áreas mais usadas no fluxo de nutrição é o menu rápido de adicionar ações relacionadas ao paciente. Nas referências visuais, aparecem opções como adicionar questionário pré-consulta, anamnese, avaliação antropométrica, gastos energéticos, exames laboratoriais, recordatório alimentar, plano alimentar, prescrições e metas.

A Karol indicou que, dessa tela, o que ela mais usa é:
- adicionar gastos energéticos;
- adicionar exames laboratoriais;
- adicionar plano alimentar.

## Objetivo
Criar especificação e futura implementação de um menu rápido de ações do Nutri Pro, priorizando as ações mais usadas no atendimento nutricional.

## Escopo permitido
- Criar documentação do menu rápido Nutri Pro.
- Definir UX do menu rápido.
- Definir cards de ação.
- Definir priorização das ações mais usadas.
- Criar task futura de implementação.
- Se esta task for executada diretamente, criar componente inicial do menu rápido no frontend, se o módulo Nutri Pro já existir.

## Fora de escopo
- Não implementar o Nutri Pro completo.
- Não implementar plano alimentar completo agora, se ainda não existir.
- Não criar cálculo energético completo agora, se ainda não existir.
- Não criar PDF de exames agora, se ainda não existir.
- Não copiar design de terceiros.
- Não fazer push.

## Requisitos funcionais
1. Criar menu rápido chamado:
   - Menu Rápido Nutri Pro;
   - ou Ações rápidas do paciente.

2. O menu deve aparecer no perfil/prontuário do paciente nutricional.

3. Deve ter duas áreas:

### Área 1 — Avaliação
- Adicionar Questionário pré-consulta;
- Adicionar Anamnese;
- Adicionar Avaliação Antropométrica;
- Adicionar Gastos Energéticos;
- Adicionar Exames Laboratoriais;
- Adicionar Recordatório Alimentar;
- Adicionar Avaliação DB360, se for mantido como futuro/opcional.

### Área 2 — Prescrição e acompanhamento
- Adicionar Plano Alimentar;
- Adicionar Prescrições;
- Adicionar Metas;
- Adicionar Lista de Compras, se fizer sentido;
- Adicionar Diário Alimentar, se fizer sentido.

4. As ações mais usadas devem ter destaque:
   - Adicionar Gastos Energéticos;
   - Adicionar Exames Laboratoriais;
   - Adicionar Plano Alimentar.

5. Essas três ações devem aparecer como cards principais, com destaque visual maior.

6. As demais ações podem aparecer como cards secundários.

7. Cada card deve ter:
   - ícone;
   - título;
   - descrição curta;
   - status, se aplicável;
   - ação de clique;
   - indicação se é futuro, rascunho ou disponível.

8. O menu deve ser responsivo:
   - no desktop, grid de cards;
   - no tablet, grid de 2 colunas;
   - no celular, cards empilhados ou carrossel horizontal;
   - botões com toque confortável.

9. Design:
   - moderno;
   - limpo;
   - premium;
   - saúde/nutrição;
   - cores por tipo de ação;
   - sem parecer uma lista solta.

10. O menu deve ter microcopy:

> Recomendamos realizar anamnese, avaliação antropométrica e cálculo de gasto energético para elaborar um plano alimentar mais preciso.

11. No Plano Estudante:
   - ações podem ser simuladas;
   - documentos devem ter marca d'água;
   - recursos oficiais podem ficar bloqueados sem CRN.

## Critérios de aceite
- A documentação do menu rápido é criada.
- As ações principais são listadas.
- As três ações mais usadas pela Karol são priorizadas.
- O design esperado é descrito.
- O fluxo do clique em cada ação é documentado.
- Não há cópia de design de terceiros.
- Backlog de implementação é atualizado.

## Se implementado
- Menu aparece no perfil do paciente.
- Cards são responsivos.
- Ações indisponíveis aparecem como "em breve".
- Ações disponíveis direcionam para fluxo correto.

## Validação
1. Validar que o menu rápido foi documentado como parte do fluxo Nutri Pro.
2. Confirmar que as três ações principais estão destacadas.
3. Confirmar que ações futuras/indisponíveis estão claramente marcadas.
4. Confirmar que o design não copia referências externas.
5. Se houver implementação, testar desktop, tablet e mobile.
6. Rodar build/lint/testes conforme o Harness quando houver código.

## Commit esperado ao executar

```bash
git commit -m "feat(nutri): criar menu rapido de acoes principais"
```

## Prompt recomendado

```md
Execute TASK-R9-002 — Criar menu rápido de ações principais do Nutri Pro seguindo o Harness Profissional.
Priorize a especificação e, se o módulo Nutri Pro já permitir, implemente apenas um componente inicial de menu rápido no perfil do paciente, sem criar o Nutri Pro completo e sem push.
```
