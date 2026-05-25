# TASK-R9-004 — Popular ambiente demo com usuários, planos e simulações realistas

## Release
R9 — Suporte, central de ajuda, comunicação e refinamentos de experiência

## Complexidade
ALTA

## Tipo
Backend, seed/demo data, dados de apresentação, documentação local e validação visual.

## Contexto
O AtendePro precisa ter dados realistas para apresentação, testes e validação visual. Hoje algumas áreas aparecem vazias ou com dados muito simples. Precisamos popular o ambiente local/demo com empresas, usuários, planos, profissionais, pacientes/clientes, procedimentos, simulações e dados específicos das áreas de Nutrição, Estética, Biomedicina, Fisioterapia, Spaces e planos comerciais.

## Objetivo
Criar uma base demo profissional e realista para que, ao acessar o sistema local, seja possível visualizar dashboards, planos, usuários, verticais, procedimentos, simulações, alertas, histórico, cards, relatórios e fluxos com aparência de produto real.

## Escopo permitido
- Criar seed/demo data local.
- Criar empresas demo.
- Criar usuários demo por perfil.
- Criar profissionais demo por área.
- Criar pacientes/clientes demo fictícios.
- Criar planos comerciais demo.
- Criar assinaturas demo por plano.
- Criar procedimentos e serviços por vertical.
- Criar simulações de preço realistas.
- Criar dados de nutrição, estética, biomedicina, fisioterapia e spaces.
- Popular dashboards com dados coerentes.
- Criar dados que demonstrem status saudável, margem baixa e prejuízo.
- Criar documentação de login demo.
- Criar opção de resetar dados demo, se fizer sentido.
- Atualizar README/LOCAL_RUNBOOK com usuários demo.

## Fora de escopo
- Não usar dados reais de pacientes.
- Não usar CPFs reais.
- Não usar e-mails pessoais reais.
- Não criar integração de pagamento real.
- Não criar gateway externo.
- Não criar IA.
- Não criar WhatsApp real.
- Não fazer push.

## Regras gerais
1. Todos os dados devem ser fictícios.
2. Não usar dados pessoais reais.
3. Dados devem parecer realistas para apresentação.
4. Criar dados suficientes para evitar telas vazias.
5. As simulações devem ter variedade de cenários.
6. Os usuários devem representar perfis e planos diferentes.
7. Os dados devem respeitar multiempresa/tenant.
8. Se houver profile local/demo, usar apenas nesse profile.
9. Não afetar produção.
10. Documentar claramente que são dados demo.

## Empresas demo
Criar empresas fictícias:

| Empresa | Área principal | Plano | Status | Cidade |
|---|---|---|---|---|
| Clínica Nutri Vida | Nutrição | Nutri Pro | Ativa | Rio de Janeiro/RJ |
| Studio Aesthetic Premium | Estética/Beauty | Beauty Pro | Ativa | Duque de Caxias/RJ |
| Clínica Biomed Glow | Biomedicina Estética | Biomed Pro | Ativa | Rio de Janeiro/RJ |
| Movimento Fisio Center | Fisioterapia | Fisio Pro | Ativa | Niterói/RJ |
| Espaço Compartilhado Pro | Spaces/Sublocação | Spaces | Ativa | Rio de Janeiro/RJ |
| Salão Bella Forma | Beleza/Salão | Beauty Pro ou Business | Ativa | Rio de Janeiro/RJ |
| Conta Estudante Demo | Estudante | Estudante | Ativa | Rio de Janeiro/RJ |

## Usuários demo
Criar usuários fictícios:

| Nome | E-mail | Senha | Papel | Observação |
|---|---|---|---|---|
| Administrador AtendePro | admin@atendepro.local | AtendePro@123 | SUPER_ADMIN | Admin global |
| Karol Nutricionista Demo | karol.nutri@atendepro.local | AtendePro@123 | ADMIN_EMPRESA / PROFISSIONAL | CRN, Nutri Pro |
| Ana Esteticista Demo | ana.estetica@atendepro.local | AtendePro@123 | ADMIN_EMPRESA / PROFISSIONAL | Beauty Pro |
| Bianca Biomédica Demo | bianca.biomed@atendepro.local | AtendePro@123 | ADMIN_EMPRESA / PROFISSIONAL | CRBM, Biomed Pro |
| Felipe Fisioterapeuta Demo | felipe.fisio@atendepro.local | AtendePro@123 | ADMIN_EMPRESA / PROFISSIONAL | CREFITO, Fisio Pro |
| Paula Gestora de Espaços | paula.spaces@atendepro.local | AtendePro@123 | ADMIN_EMPRESA | Spaces |
| Estudante Demo | estudante@atendepro.local | AtendePro@123 | ESTUDANTE | Plano Estudante |

## Planos comerciais demo
Popular planos:
- Estudante: R$ 29,90
- Start: R$ 79,90
- Care: R$ 119,90
- Nutri Pro: R$ 149,90
- Beauty Pro: R$ 149,90
- Biomed Pro: R$ 179,90
- Fisio Pro: R$ 149,90
- Business: R$ 249,90
- Spaces: R$ 299,90
- Premium: R$ 499,90

Cada plano deve ter nome, código, preço mensal, limite de usuários, limite de clientes/pacientes, limite de profissionais, módulos liberados, status, descrição curta e recursos principais.

## Pacientes/clientes demo
Criar clientes/pacientes fictícios por empresa:

- Nutrição: Mariana Silva, Ana Victoria, Camila Oliveira, João Pedro Santos, Vanessa Miranda.
- Estética: Juliana Costa, Renata Almeida, Bruna Martins, Larissa Souza, Patrícia Ramos.
- Biomedicina: Amanda Rocha, Letícia Castro, Fernanda Lima, Marcela Nunes.
- Fisioterapia: Carlos Henrique, Pedro Almeida, Helena Duarte, Beatriz Campos.
- Salão/Beleza: Carla Mendes, Nicole Ferreira, Aline Barbosa.
- Spaces: Dra. Juliana R., Profissional Parceiro 1, Profissional Parceiro 2.

## Procedimentos e serviços por área
Criar procedimentos suficientes para alimentar listas, dashboards e simulações.

### Nutrição
- Consulta nutricional inicial
- Consulta de retorno
- Avaliação com bioimpedância
- Plano alimentar personalizado
- Pacote de acompanhamento mensal
- Acompanhamento de emagrecimento
- Acompanhamento de performance
- Consulta online

### Estética / Beauty Pro
- Limpeza de pele
- Limpeza de pele premium
- Peeling químico superficial
- Peeling clareador
- Microagulhamento facial
- Radiofrequência facial
- Radiofrequência corporal
- Drenagem linfática
- Massagem modeladora
- Massagem relaxante
- Protocolo corporal redutor
- Protocolo para acne
- Protocolo para manchas
- Terapia capilar
- Design de sobrancelhas
- Extensão de cílios
- Lash lifting
- Brow lamination

### Biomed Pro
- Avaliação estética biomédica
- Microagulhamento biomédico
- Peeling biomédico estético
- Bioestimulador
- Laser estético
- Intradermoterapia
- Protocolo capilar
- Procedimento corporal estético
- Harmonização facial simulada

### Fisio Pro
- Avaliação fisioterapêutica
- Sessão de fisioterapia ortopédica
- Sessão de fisioterapia desportiva
- RPG
- Pilates clínico
- Terapia manual
- Liberação miofascial
- Drenagem pós-operatória
- Fisioterapia domiciliar
- Reabilitação funcional

### Spaces
- Sala estética por hora
- Sala de atendimento nutricional por hora
- Sala de fisioterapia por hora
- Cadeira de salão por turno
- Cabine estética por diária
- Uso de equipamento por sessão
- Consultório premium por período

### Salão/Beleza
- Corte feminino
- Corte masculino
- Escova
- Hidratação capilar
- Coloração
- Luzes/mechas
- Progressiva
- Manicure
- Pedicure
- Alongamento de unhas
- Maquiagem social

## Insumos demo
Criar insumos fictícios e vincular a procedimentos quando possível:
- Luva descartável
- Máscara descartável
- Touca descartável
- Lençol descartável
- Algodão
- Gaze
- Álcool 70
- Sérum facial
- Máscara calmante
- Ácido mandélico
- Ácido glicólico
- Gel condutor
- Creme de massagem
- Óleo corporal
- Produto capilar
- Tintura
- Oxidante
- Shampoo profissional
- Esmalte
- Removedor
- Lâmina descartável
- Ponteira descartável
- Kit avaliação nutricional
- Impresso de orientação
- Elástico terapêutico
- Fita cinesiológica
- Creme terapêutico

## Simulações de preço realistas
Criar pelo menos:
- 5 simulações saudáveis, com preço praticado maior ou igual ao preço recomendado.
- 5 simulações com margem baixa, com preço praticado acima do custo e abaixo do recomendado.
- 5 simulações em prejuízo, com preço praticado menor que o custo total.

Exemplos mínimos:

| Procedimento | Preço praticado | Custo total | Preço recomendado | Status |
|---|---:|---:|---:|---|
| Limpeza de pele premium | R$ 220,00 | R$ 110,00 | R$ 157,14 | Saudável |
| Consulta nutricional inicial | R$ 180,00 | R$ 95,00 | R$ 135,71 | Saudável |
| Massagem relaxante | R$ 120,00 | R$ 105,00 | R$ 150,00 | Margem baixa |
| Consulta profissional | R$ 240,00 | R$ 275,00 | R$ 392,86 | Prejuízo |
| Sala estética por hora | R$ 60,00 | R$ 42,00 | R$ 60,00 | Saudável |
| Fisioterapia domiciliar | R$ 160,00 | R$ 145,00 | R$ 207,14 | Margem baixa |
| Bioestimulador | R$ 950,00 | R$ 720,00 | R$ 1.028,57 | Margem baixa |
| Corte feminino | R$ 90,00 | R$ 35,00 | R$ 50,00 | Saudável |
| Protocolo capilar | R$ 180,00 | R$ 210,00 | R$ 300,00 | Prejuízo |
| Drenagem linfática | R$ 130,00 | R$ 80,00 | R$ 114,29 | Saudável |

## Dados para dashboard
Popular dados suficientes para dashboards mostrarem:
- procedimentos cadastrados;
- clientes/pacientes ativos;
- agenda de hoje;
- próximos 7 dias;
- custos ativos;
- estoque baixo;
- validade em 30 dias;
- manutenção em 30 dias;
- simulações saudáveis;
- simulações em alerta;
- lucro médio;
- margem média;
- preço recomendado médio.

## Dados específicos por vertical

### Nutri Pro
Criar exemplos fictícios para visualização futura:
- plano alimentar exemplo;
- refeições: café da manhã, colação, almoço, lanche da tarde e jantar;
- alimentos fictícios: pão integral, ovo, frango, arroz, feijão, banana, aveia e iogurte;
- suplementos: whey protein e creatina;
- avaliação antropométrica fictícia;
- gasto energético fictício;
- solicitação de exames fictícia;
- prescrição de suplementação fictícia.

Se o módulo Nutri ainda não estiver implementado, documentar como mock/dados planejados ou preparar seed apenas quando tabelas existirem.

### Beauty Pro
Criar exemplos fictícios:
- protocolo facial;
- protocolo corporal;
- termo de consentimento simulado;
- fotos de evolução fictícias apenas como placeholder, sem imagem real de pessoa;
- pacote de sessões;
- produtos/lotes fictícios;
- alertas de validade.

### Biomed Pro
Criar exemplos fictícios:
- habilitação CRBM demo;
- procedimento estético biomédico;
- rastreabilidade de produto/lote;
- registro de intercorrência fictício;
- termo de consentimento biomédico.

### Fisio Pro
Criar exemplos fictícios:
- avaliação funcional;
- plano terapêutico;
- evolução por sessão;
- pacote de sessões;
- relatório fisioterapêutico.

### Spaces
Criar exemplos fictícios:
- sala;
- cadeira;
- cabine;
- equipamento;
- preço por hora;
- preço por turno;
- diária;
- simulação de parceiro;
- ocupação de agenda.

## Documentação a atualizar durante a execução
- `README.md`
- `docs/deploy/LOCAL_RUNBOOK.md`
- `docs/product/demo-data.md`

Documentar logins:
- `admin@atendepro.local / AtendePro@123`
- `karol.nutri@atendepro.local / AtendePro@123`
- `ana.estetica@atendepro.local / AtendePro@123`
- `bianca.biomed@atendepro.local / AtendePro@123`
- `felipe.fisio@atendepro.local / AtendePro@123`
- `paula.spaces@atendepro.local / AtendePro@123`
- `estudante@atendepro.local / AtendePro@123`

## Critérios de aceite
- Usuários demo existem.
- Empresas demo existem.
- Planos demo existem.
- Cada usuário está vinculado à empresa correta.
- Login demo funciona.
- Dashboard não fica vazio.
- Lista de procedimentos contém dados variados.
- Existem simulações saudáveis, em alerta e em prejuízo.
- Cards/indicadores mostram dados reais do seed.
- Precificação permite visualizar cenários diferentes.
- Verticais mostram exemplos coerentes.
- Dados são fictícios.
- Testes passam.
- Docker/local sobe.
- Não foi feito push.

## Validação obrigatória
1. Rodar testes backend.
2. Rodar build/testes frontend, se aplicável.
3. Subir Docker/local.
4. Logar com cada usuário demo principal.
5. Conferir dashboard.
6. Conferir lista de procedimentos.
7. Conferir simulações.
8. Conferir alertas.
9. Conferir planos.
10. Conferir que dados demo aparecem somente em ambiente local/demo.
11. Conferir que não há dados pessoais reais.

## Commit esperado ao executar

```bash
git commit -m "feat(demo): popular ambiente com usuarios planos e simulacoes realistas"
```

## Execução

Status: CONCLUIDA.

Resumo:
- Backend ganhou `DadosDemoLocalRunner`, executado apenas no profile `local`, para popular dados fictícios de apresentação de forma idempotente.
- Seed local cria empresas, usuários demo, planos comerciais, assinaturas, pacientes/clientes, agenda, serviços/procedimentos, custos, estoque, equipamentos, Spaces e simulações de precificação.
- A senha demo local foi padronizada para `AtendePro@123`, incluindo o bootstrap do super admin.
- Foram criadas 15 simulações demo: 5 saudáveis, 5 com margem baixa e 5 em prejuízo.
- Dados específicos profundos do Nutri Pro foram documentados como mock planejado até existirem tabelas próprias da vertical.
- Logins e regras de dados demo foram documentados em `README.md`, `docs/deploy/LOCAL_RUNBOOK.md` e `docs/product/demo-data.md`.

Validação:
- `mvn -q -DskipTests compile` com Java 21.
- `mvn test` com Java 21: 270 testes passaram.
- `corepack pnpm typecheck`.
- `corepack pnpm lint`.
- `corepack pnpm build`.
- `docker compose up -d` e `docker compose ps`.
- `GET http://127.0.0.1:8080/actuator/health`: `UP`.
- `GET http://127.0.0.1:3000/app`: HTTP 200.
- Banco local validado com 7 empresas demo, 7 usuários principais, serviços por vertical e simulações por status.
- Login validado para os 7 usuários demo principais com `AtendePro@123`.
- API validada para dashboard, planos, procedimentos e dashboard de precificação.
- Browser validado em `/app` com Clínica Nutri Vida selecionada, simulador de precificação exibindo status e filtros, sem erros de console.

## Prompt recomendado

```md
Execute TASK-R9-004 — Popular ambiente demo com usuários, planos e simulações realistas seguindo o Harness Profissional.
Implemente somente dados demo fictícios e locais, respeitando multiempresa/tenant, sem dados reais, sem integração externa e sem push. Valide testes, Docker/local, logins demo, dashboards, procedimentos, simulações e documentação.
```
