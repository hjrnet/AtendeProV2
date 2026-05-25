# TASK-UX-001 — Redesenhar shell, navegacao e experiencia responsiva do AtendePro

## Release
R8 — Fundacao de experiencia profissional do SaaS

## Complexidade
ALTA

## Tipo
Frontend/UX, arquitetura de navegacao e design system base.

## Objetivo
Redesenhar a experiencia do AtendePro para usar um shell profissional responsivo, navegacao por areas, conteudo ativo no painel central e layouts adaptativos para mobile, tablet e desktop.

A interface deve transmitir saude, limpeza, organizacao, profundidade, prosperidade, confianca, tecnologia moderna, produto SaaS premium e clareza operacional.

## Contexto
O AtendePro ja possui areas operacionais, verticais profissionais, precificacao, Admin SaaS e modulos futuros, mas a interface atual se comporta como uma pagina longa, com blocos empilhados e navegacao por scroll/ancora.

Isso torna a experiencia confusa, pouco profissional e ruim para apresentacao, especialmente em telas pequenas.

## Problemas Observados
- Interface longa com informacoes empilhadas.
- Clique no menu leva a scroll em vez de trocar contexto.
- Falta arquitetura clara de navegacao.
- Falta separacao entre shell, menu, conteudo principal e paineis auxiliares.
- Visual clean, mas ainda simples, com pouca profundidade e contraste.
- Verticais aparecem como cards longos em sequencia.
- Precificacao, Admin SaaS e operacao ficam misturados no mesmo fluxo vertical.
- Mobile/tablet exige muita rolagem.
- Usuario nao entende rapidamente onde esta e o que fazer agora.
- Produto precisa parecer SaaS profissional moderno, nao prototipo tecnico.

## Referencias Conceituais Obrigatorias
- Material Design 3: tabs para alternar paineis relacionados.
- Progressive disclosure: mostrar primeiro o essencial e revelar detalhes sob demanda.
- Split view em telas grandes: menu/lista/painel principal.
- Navigation rail/sidebar em desktop e tablet.
- Bottom navigation, drawer ou tabs compactas em mobile.
- WCAG 2.2: foco visivel, contraste adequado e alvos de toque confortaveis.

Nao copiar layout, marca ou textos de sistemas de terceiros.

## Escopo Permitido
- Redesenhar arquitetura de navegacao.
- Criar shell principal responsivo.
- Criar layout com area central de conteudo ativo.
- Remover comportamento de navegacao por scroll/ancora.
- Criar navegacao por abas, rotas ou estado de area ativa.
- Criar componentes reutilizaveis de layout.
- Melhorar visual premium, profundidade, contraste e hierarquia.
- Melhorar mobile/tablet/desktop.
- Reorganizar verticais profissionais.
- Reorganizar Admin SaaS, Operacao, Precificacao e Busca Global.
- Melhorar experiencia de listas, cards e paineis.
- Criar design system base se necessario.

## Fora de Escopo
- Nao alterar regra de negocio.
- Nao alterar calculo financeiro.
- Nao criar funcionalidades novas de backend.
- Nao criar novas verticais.
- Nao criar mobile Expo.
- Nao criar integracao externa.
- Nao fazer push.

## Requisitos Funcionais

### Nova Arquitetura de Navegacao
Quando o usuario clicar em Operacao, Verticais, Precificacao, Busca global ou Admin SaaS, o sistema deve trocar o conteudo do painel principal, nao rolar a pagina.

- Menu lateral nao deve ser link de ancora.
- Menu deve controlar rota ou estado de secao ativa.
- Painel central deve exibir somente a secao ativa.
- Secoes inativas nao devem ficar empilhadas abaixo.
- Usuario deve perceber claramente a secao ativa.
- Em mobile, menu deve virar bottom navigation, drawer ou tabs compactas.

Sugestoes de nomes:
- `secaoAtiva`
- `definirSecaoAtiva`
- `renderizarSecaoAtiva`
- `ShellAtendePro`
- `MenuPrincipal`
- `PainelConteudoAtivo`
- `NavegacaoMobile`
- `BarraSuperiorContextual`

### Shell Profissional
Desktop:
- sidebar fixa a esquerda;
- topbar contextual;
- painel central;
- painel lateral opcional para detalhes/filtros;
- conteudo com largura maxima e respiro.

Tablet:
- navigation rail compacta;
- painel central;
- painel auxiliar colapsavel.

Mobile:
- topbar compacta;
- bottom navigation ou drawer;
- conteudo de uma secao por vez;
- sem listas gigantes empilhadas;
- acoes principais fixas no rodape quando necessario.

### Progressive Disclosure
Nao mostrar tudo ao mesmo tempo.

Verticais devem usar lista/grid compacto, clique em modulo e detalhe em painel, drawer ou rota dedicada, com tabs de Visao geral, Capacidades, Documentos, Regras e Roadmap.

### Verticais Profissionais
A secao de verticais nao deve ser uma sequencia longa de cards completos.

Cada card compacto deve mostrar icone, nome, release, conselho se houver, status e descricao curta. Ao clicar, deve abrir detalhe no painel central, drawer lateral ou rota dedicada.

### Precificacao
Precificacao deve virar area propria com dashboard, simulador, historico, resultado e relatorio.

Desktop deve priorizar simulador a esquerda, resultado a direita e historico abaixo ou lateral. Mobile deve usar tabs como Simular, Resultado e Historico.

### Admin SaaS
Admin SaaS deve ser secao propria com Planos e limites, Empresas, Assinaturas, Suporte e Relatorios.

Listas longas devem ter busca, filtro, scroll interno, paginacao ou lista compacta. Edicao deve acontecer em drawer/modal, nao em formulario gigante abaixo da lista.

### Design Visual Premium
Direcao visual:
- fundo geral off-white/verde muito claro;
- cards brancos com sombra suave;
- bordas com contraste elegante;
- gradientes sutis;
- verde profundo como primario;
- azul petroleo para contraste;
- menta suave para estados neutros;
- ambar para alerta;
- vermelho apenas para erro/prejuizo;
- tipografia com hierarquia forte;
- icones discretos;
- chips de status;
- foco visivel;
- microinteracoes leves.

Sensacao desejada: um consultorio/clinica premium transformado em software.

### Mobile-First
No mobile: uma secao por tela, menu inferior ou drawer, cards compactos, graficos compactos, botoes grandes, filtros em drawer/bottom sheet, formularios em etapas quando houver, sem scroll infinito, sem tabelas largas.

### Tablet
Tablet deve aproveitar largura com duas colunas, paineis centrais e laterais quando fizer sentido, navigation rail compacta e alvos de toque confortaveis.

### Busca e Listas
Toda lista relevante deve prever busca, filtro, ordenacao, estado vazio, scroll interno e paginacao quando necessario.

Aplicar em verticais, planos, procedimentos, simulacoes, empresas, clientes/pacientes, agenda e documentos.

### Acessibilidade
- foco visivel;
- contraste adequado;
- botoes com area de toque confortavel;
- labels claros;
- nao depender apenas de cor para status;
- navegacao por teclado quando possivel;
- evitar componentes escondidos sem semantica.

## Entregaveis Esperados
- `ShellAtendePro`
- `MenuPrincipal`
- `NavegacaoMobile`
- `PainelConteudoAtivo`
- `SecaoOperacao`
- `SecaoVerticais`
- `DetalheVertical`
- `SecaoPrecificacao`
- `SecaoAdminSaas`
- `ListaComBusca`
- `CardModulo`
- `CardIndicador`
- `EmptyState`
- `LoadingState`

## Criterios de Aceite
- Menu principal nao faz scroll para secoes.
- Clique no menu troca conteudo ativo no painel central.
- Pagina nao renderiza todas as secoes empilhadas.
- Verticais nao ficam em sequencia longa com todos os detalhes abertos.
- Precificacao abre como secao propria.
- Admin SaaS abre como secao propria.
- Mobile mostra uma secao por vez.
- Tablet tem layout adaptado.
- Desktop tem shell profissional.
- Design tem mais profundidade, contraste e acabamento premium.
- Listas tem busca, filtro ou scroll quando aplicavel.
- Experiencia parece SaaS moderno, nao formulario tecnico.
- Build/testes passam.
- Docker/local validado.

## Validacao Obrigatoria
Testar larguras:
- 320px;
- 375px;
- 430px;
- 768px;
- 1024px;
- desktop.

Validar navegacao lateral, navegacao mobile, Operacao, Verticais, clique em vertical, Precificacao, Admin SaaS, Busca global, listas com scroll, ausencia de scroll automatico por ancora, build frontend e Docker/local.

## Comandos Recomendados
Backend, se nao houver alteracao backend:

```powershell
cd E:\Projetos\AtendeProV2\backend
$env:JAVA_HOME="C:\Program Files\Zulu\zulu-21"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
mvn test
```

Web:

```powershell
cd E:\Projetos\AtendeProV2\web
corepack pnpm lint
corepack pnpm typecheck
corepack pnpm build
corepack pnpm dev --host 0.0.0.0
```

Docker/local:

```powershell
cd E:\Projetos\AtendeProV2
docker compose up -d --build
docker compose ps
```

## Commit Esperado da Implementacao

```bash
git commit -m "feat(ux): redesenhar shell e navegacao profissional do AtendePro"
```

## Execucao

Status: CONCLUIDA.

Validacao realizada:
- `corepack pnpm lint`
- `corepack pnpm typecheck`
- `corepack pnpm build`
- `mvn test` em `backend`
- `docker compose ps`
- backend local `/actuator/health`
- web local `/app`
- navegador em 320px, 375px, 430px, 768px, 1024px e desktop.

## Prompt Recomendado

```md
Execute TASK-UX-001 — Redesenhar shell, navegacao e experiencia responsiva do AtendePro seguindo o Harness Profissional.
Implemente somente a fundacao de UX/shell da R8, sem alterar regras de negocio, calculos financeiros, backend funcional, novas verticais, Expo ou integracoes externas. Valide desktop/tablet/mobile, testes/build, Docker/local, faça commit local e nao faça push.
```
