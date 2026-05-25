# TASK-R9-001 — Destacar visualmente simulações em alerta na Precificação

## Release
R9 — Suporte, central de ajuda, comunicação e refinamentos de experiência

## Complexidade
MÉDIA

## Tipo
Frontend/UX, refinamento visual e experiência de precificação.

## Contexto
Na tela de precificação, o dashboard mostra "Simulações em alerta", mas na lista de simulações não fica visualmente claro qual item está em alerta. O usuário precisa identificar rapidamente quais simulações estão saudáveis, quais estão com margem baixa e quais estão em prejuízo.

## Problema
O indicador superior mostra quantidade de alertas, mas a lista abaixo não destaca claramente qual simulação está problemática.

## Objetivo
Melhorar a experiência visual da seção de precificação para que simulações em alerta fiquem destacadas de forma clara, moderna e intuitiva.

## Escopo permitido
- Melhorar cards/lista de simulações.
- Adicionar destaque visual por status.
- Adicionar badges, cores, ícones e microcopy.
- Melhorar dashboard de precificação.
- Melhorar tooltip/explicação dos alertas.
- Ajustar frontend.
- Ajustar regra de classificação visual, se já existir dado retornado pela API.

## Fora de escopo
- Não alterar fórmula de cálculo financeiro.
- Não recriar módulo de precificação.
- Não alterar backend sem necessidade.
- Não criar nova funcionalidade fora de precificação.
- Não fazer push.

## Requisitos funcionais
1. A lista de simulações deve indicar visualmente o status de cada simulação:
   - Saudável;
   - Margem baixa;
   - Prejuízo;
   - Sem simulação.

2. Quando uma simulação estiver em alerta, o card deve ter:
   - borda âmbar ou vermelha, conforme gravidade;
   - badge visível;
   - ícone de alerta;
   - texto curto explicando o motivo.

3. Textos sugeridos:
   - "Margem baixa";
   - "Preço abaixo do recomendado";
   - "Venda abaixo do custo";
   - "Em prejuízo".

4. Quando estiver saudável:
   - badge verde;
   - borda neutra ou verde suave;
   - texto "Saudável".

5. O card da simulação em alerta deve ser facilmente encontrado na lista.

6. O dashboard superior deve conversar com a lista:
   - se existe 1 alerta, o item em alerta deve estar destacado;
   - se clicar no card "Simulações em alerta", pode filtrar ou destacar os alertas, se viável.

7. Criar filtro rápido, se fizer sentido:
   - Todas;
   - Saudáveis;
   - Em alerta;
   - Em prejuízo.

8. No mobile, o alerta deve ser visível sem depender de hover.

9. Não depender apenas de cor. Usar também texto e ícone.

## Critérios de aceite
- O usuário consegue identificar imediatamente qual simulação está em alerta.
- Simulações saudáveis aparecem com status positivo.
- Simulações com prejuízo aparecem com destaque crítico.
- Simulações com margem baixa aparecem com alerta intermediário.
- O dashboard e a lista ficam coerentes.
- Funciona em desktop, tablet e celular.
- Build/testes passam.

## Validação
1. Criar ou usar simulações saudáveis.
2. Criar ou usar simulação com margem baixa.
3. Criar ou usar simulação em prejuízo.
4. Confirmar que o contador de alertas bate com a lista.
5. Confirmar destaque visual.
6. Testar no mobile.
7. Rodar build/lint/testes conforme o Harness.

## Commit esperado ao executar

```bash
git commit -m "feat(precificacao): destacar simulacoes em alerta visualmente"
```

## Execução

Status: CONCLUIDA.

Resumo:
- Histórico de simulações passou a exibir status visual por card, com badge, ícone, borda e microcopy para saudável, margem baixa, sem lucro/equilíbrio e prejuízo.
- Foram adicionados filtros rápidos para `Todas`, `Saudáveis`, `Em alerta` e `Em prejuízo`.
- O card de `Simulações em alerta` no dashboard ganhou destaque e descrição contextual.
- Salvamento/atualização de simulação passou a invalidar também o dashboard de precificação, mantendo contador e lista coerentes.

Validação:
- `corepack pnpm typecheck`
- `corepack pnpm lint`
- `corepack pnpm build`
- `docker compose ps`
- `GET http://127.0.0.1:8080/actuator/health`
- `GET http://127.0.0.1:3000/app`
- Browser em `/app`: criada simulação saudável, margem baixa e prejuízo; filtros `Em alerta` e `Em prejuízo` validados; console sem erros.

## Prompt recomendado

```md
Execute TASK-R9-001 — Destacar visualmente simulações em alerta na Precificação seguindo o Harness Profissional.
Implemente somente o refinamento visual da lista/dashboard de precificação, sem alterar fórmulas financeiras, sem recriar o módulo e sem push. Valide desktop, tablet, mobile, lint/build/testes e Docker/local quando aplicável.
```
