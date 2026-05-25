# Dados demo locais do AtendePro

## Objetivo

O ambiente demo local existe para apresentação, testes manuais, validação visual e demonstração do produto com dados realistas, sem depender de integrações externas e sem usar dados reais.

Os dados são criados pelo backend apenas no profile `local`, por meio do runner `DadosDemoLocalRunner`. A carga é idempotente: pode ser executada novamente sem duplicar os registros oficiais do demo.

## Segurança e privacidade

- Todos os nomes, documentos, e-mails e telefones são fictícios.
- Não usar CPFs, CNPJs, e-mails pessoais ou dados reais de pacientes.
- Não usar este seed em produção.
- O profile `test` não executa o seed.
- O profile de produção não deve ativar o runner `local`.

## Logins demo

| Perfil | E-mail | Senha | Empresa |
|---|---|---|---|
| Super Admin | `admin@atendepro.local` | `AtendePro@123` | Global |
| Nutri Pro | `karol.nutri@atendepro.local` | `AtendePro@123` | Clínica Nutri Vida |
| Beauty Pro | `ana.estetica@atendepro.local` | `AtendePro@123` | Studio Aesthetic Premium |
| Biomed Pro | `bianca.biomed@atendepro.local` | `AtendePro@123` | Clínica Biomed Glow |
| Fisio Pro | `felipe.fisio@atendepro.local` | `AtendePro@123` | Movimento Fisio Center |
| Spaces | `paula.spaces@atendepro.local` | `AtendePro@123` | Espaço Compartilhado Pro |
| Estudante | `estudante@atendepro.local` | `AtendePro@123` | Conta Estudante Demo |

## Empresas demo

| Empresa | Área principal | Plano |
|---|---|---|
| Clínica Nutri Vida | Nutrição | Nutri Pro |
| Studio Aesthetic Premium | Estética/Beauty | Beauty Pro |
| Clínica Biomed Glow | Biomedicina estética | Biomed Pro |
| Movimento Fisio Center | Fisioterapia | Fisio Pro |
| Espaço Compartilhado Pro | Spaces/Sublocação | Spaces |
| Salão Bella Forma | Beleza/Salão | Business |
| Conta Estudante Demo | Estudante | Estudante |

## Planos comerciais

O seed local ajusta o catálogo demo para:

| Plano | Valor mensal |
|---|---:|
| Estudante | R$ 29,90 |
| Start | R$ 79,90 |
| Care | R$ 119,90 |
| Nutri Pro | R$ 149,90 |
| Beauty Pro | R$ 149,90 |
| Biomed Pro | R$ 179,90 |
| Fisio Pro | R$ 149,90 |
| Business | R$ 249,90 |
| Spaces | R$ 299,90 |
| Premium | R$ 499,90 |

## Dados persistidos hoje

O seed preenche tabelas já existentes:

- `empresas`;
- `auth_usuarios`;
- `planos` e `plano_modulos`;
- `assinaturas`;
- `clientes_pacientes`;
- `agenda_compromissos`;
- `servicos_procedimentos`;
- `custos_gerais`;
- `custos_alimentacao_transporte`;
- `estoque_produtos`;
- `equipamentos`;
- `spaces_recursos`;
- `spaces_pacotes_sublocacao`;
- `spaces_ocupacoes`;
- `precificacao_simulacoes`.

## Procedimentos e simulações

Cada vertical recebe serviços suficientes para evitar telas vazias e alimentar busca, dashboard e precificação:

- Nutrição: consulta inicial, retorno, bioimpedância, plano alimentar, acompanhamento mensal, performance e online.
- Beauty/estética: limpeza de pele, peelings, microagulhamento, radiofrequência, drenagem, massagens, protocolos, cílios e sobrancelhas.
- Biomedicina: avaliação, microagulhamento, peeling, bioestimulador, laser, intradermoterapia, protocolo capilar e harmonização simulada.
- Fisioterapia: avaliação, ortopedia, desportiva, RPG, pilates clínico, terapia manual, liberação miofascial, domiciliar e reabilitação.
- Spaces: sala por hora, cadeira por turno, cabine por diária, consultório por período e equipamento por sessão.
- Salão/beleza: cortes, escova, hidratação, coloração, mechas, progressiva, manicure, pedicure, unhas e maquiagem.

A precificação inclui pelo menos:

- 5 simulações saudáveis;
- 5 simulações com margem baixa;
- 5 simulações em prejuízo.

## Dados específicos por vertical

### Nutri Pro

Enquanto as tabelas profundas de Nutri Pro ainda não existem, ficam documentados como mock planejado:

- plano alimentar exemplo;
- refeições: café da manhã, colação, almoço, lanche da tarde e jantar;
- alimentos: pão integral, ovo, frango, arroz, feijão, banana, aveia e iogurte;
- suplementos: whey protein e creatina;
- avaliação antropométrica;
- gasto energético;
- solicitação de exames;
- prescrição de suplementação.

Quando as tasks `TASK-NUTRI-002` em diante criarem as tabelas, este seed deve ser expandido para persistir esses itens.

### Beauty Pro

O seed atual demonstra protocolos, produtos/lotes e alertas de estoque/validade. Fotos de evolução permanecem como placeholder sem imagem real de pessoa.

### Biomed Pro

O seed atual demonstra serviços biomédicos, produtos/lotes e equipamentos. Rastreabilidade clínica profunda e intercorrências ficam para tasks futuras da vertical.

### Fisio Pro

O seed atual demonstra pacientes, agenda, serviços, custos e equipamento. Avaliação funcional, plano terapêutico e evolução por sessão ficam para tasks futuras.

### Spaces

O seed persiste recursos, pacotes e uma ocupação confirmada, permitindo validar indicadores e fluxos de sublocação.

## Como validar rapidamente

1. Subir infra: `docker compose up -d`.
2. Subir backend local: `mvn spring-boot:run`.
3. Logar com `karol.nutri@atendepro.local / AtendePro@123`.
4. Conferir dashboard, procedimentos e precificação.
5. Logar com `admin@atendepro.local / AtendePro@123`.
6. Conferir planos e empresas no Admin SaaS.

## Limites conhecidos

- Não há reset dedicado de dados demo nesta task.
- O seed é idempotente, mas não remove dados manuais criados pelo usuário.
- Dados clínicos avançados do Nutri Pro, Beauty Pro, Biomed Pro e Fisio Pro serão persistidos quando as respectivas tabelas existirem.
- Nenhuma integração externa é executada.
