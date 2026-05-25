# TASK-R9-003 — Corrigir acentuação e textos em português do sistema

## Release
R9 — Suporte, central de ajuda e comunicação

## Complexidade
MÉDIA

## Tipo
Frontend/UX, conteúdo, microcopy e refinamento de qualidade.

## Contexto
Foi percebido que várias palavras no site aparecem sem acentuação, como "Preco", "Simulacao", "Duracao", "Descricao", "Validacao", "Relatorio", "Gestao" e outras. Isso prejudica a percepção profissional do produto e passa sensação de protótipo inacabado.

## Objetivo
Revisar e corrigir textos visíveis da interface para português brasileiro correto, com acentuação adequada e microcopy mais profissional.

## Escopo permitido
- Corrigir labels, títulos, botões, placeholders, mensagens, badges, menus e textos visíveis.
- Corrigir textos em dashboards, formulários, listas, cards e estados vazios.
- Padronizar português do Brasil.
- Criar arquivo de constantes de textos, se fizer sentido.
- Melhorar microcopy sem alterar regra de negócio.
- Corrigir textos no frontend.
- Corrigir mensagens de erro no backend apenas se forem exibidas ao usuário.

## Fora de escopo
- Não alterar nomes técnicos de classes se isso quebrar código.
- Não renomear endpoints.
- Não alterar banco de dados sem necessidade.
- Não reescrever arquitetura.
- Não fazer push.

## Correções esperadas
Substituir textos visíveis como:

- Preco → Preço
- Simulacao → Simulação
- Simulacoes → Simulações
- Duracao → Duração
- Descricao → Descrição
- Validacao → Validação
- Relatorio → Relatório
- Gestao → Gestão
- Operacao → Operação
- Sessao → Sessão
- Saude → Saúde
- Medias → Médias
- Prejuizo → Prejuízo
- Proximos → Próximos
- Alimentacao → Alimentação
- Avaliacao → Avaliação
- Historico → Histórico
- Acoes → Ações
- Configuracao → Configuração
- Usuario → Usuário
- Autenticacao → Autenticação
- Manutencao → Manutenção
- Profissional → Profissional, quando título

## Atenção
Nomes de variáveis, funções, classes e arquivos podem continuar sem acento por convenção técnica, mas textos exibidos ao usuário devem ter acentuação correta.

Exemplo:
- variável: `precoAtual`
- label exibido: Preço atual

## Padrão de microcopy
- Usar português claro, natural e profissional.
- Evitar termos técnicos para usuário final.
- Não mostrar "Failed to fetch".
- Usar frases amigáveis e orientativas.

Exemplos:
- "Nenhuma simulação calculada."
- "Cadastre um procedimento para começar."
- "Não foi possível conectar ao servidor."
- "Preço recomendado calculado com base nos custos informados."
- "Este procedimento está abaixo da margem desejada."

## Critérios de aceite
- Principais telas sem palavras sem acento.
- Labels de formulário com português correto.
- Botões com português correto.
- Estados vazios com português correto.
- Mensagens de erro com português correto.
- Dashboard com português correto.
- Menus com português correto.
- Build/testes passam.
- Não quebrou rotas ou endpoints.

## Validação
1. Rodar busca no código por termos sem acento comuns.
2. Corrigir textos visíveis.
3. Abrir principais telas.
4. Conferir menu, dashboard, formulários e cards.
5. Testar mobile.
6. Rodar build/lint/testes.

## Commit esperado ao executar

```bash
git commit -m "fix(ui): corrigir acentuacao e textos em portugues"
```

## Prompt recomendado

```md
Execute TASK-R9-003 — Corrigir acentuação e textos em português do sistema seguindo o Harness Profissional.
Corrija somente textos visíveis e microcopy, preservando nomes técnicos, endpoints, banco e regras de negócio. Valide buscas por termos sem acento, principais telas, mobile, lint/build/testes e não faça push.
```
