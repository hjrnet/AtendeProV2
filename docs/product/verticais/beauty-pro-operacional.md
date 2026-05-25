# Beauty Pro Operacional — Escopo R10

## Visão

O Beauty Pro operacional é o módulo do AtendePro para estética, beleza, salões e profissionais que precisam acompanhar clientes, protocolos, sessões, termos, produtos, agenda e precificação em uma experiência única.

O módulo deve reaproveitar o núcleo comum:

- login;
- empresa/tenant;
- usuários;
- clientes;
- agenda;
- serviços/procedimentos;
- custos;
- precificação;
- estoque;
- equipamentos;
- documentos;
- dashboard;
- planos e permissões.

## Objetivo da R10

Transformar Beauty Pro de vertical de catálogo em área operacional utilizável, sem copiar design de terceiros e sem usar imagens reais de pessoas.

## Funcionalidades previstas na R10

### Base operacional

- Área Beauty Pro dentro do shell do AtendePro.
- Endpoint de visão operacional tenant-scoped.
- Integração com dados demo existentes.
- Tela mobile-first e premium.

### Ficha estética

- Perfil estético do cliente.
- Anamnese.
- Objetivos do atendimento.
- Contraindicações e alertas.
- Histórico de avaliações.

### Protocolos e sessões

- Protocolos faciais, corporais, capilares, cílios/sobrancelhas, salão e personalizados.
- Pacotes de sessões.
- Execução de sessão.
- Evolução por sessão.
- Status do pacote.

### Termos, produtos e dashboard

- Termos de consentimento reaproveitando documentos profissionais.
- Placeholders seguros para evolução visual sem foto real.
- Produtos, insumos e lotes vinculados a protocolos/sessões.
- Alertas de validade e estoque baixo.
- Dashboard Beauty Pro.

### Integração operacional

- Agenda integrada.
- Serviços/procedimentos da vertical.
- Precificação integrada.
- Destaque de margem baixa/prejuízo.
- Fluxo: cliente → ficha → protocolo → sessão → evolução → precificação.

## Tasks oficiais da R10

| Task | Nome | Objetivo |
|---|---|---|
| TASK-BEAUTY-001 | Criar módulo Beauty Pro operacional | Base backend/web da vertical. |
| TASK-BEAUTY-002 | Criar ficha estética, anamnese e avaliação | Perfil estético do cliente. |
| TASK-BEAUTY-003 | Criar protocolos, sessões e evolução Beauty Pro | Protocolos e execução. |
| TASK-BEAUTY-004 | Criar termos, fotos placeholder, produtos/lotes e dashboard Beauty Pro | Documentos, rastreabilidade e indicadores. |
| TASK-BEAUTY-005 | Integrar Beauty Pro com agenda, precificação e experiência web | Fluxo operacional completo. |

## Fora de escopo da R10

- Upload real de fotos.
- Uso de fotos reais de pessoas.
- Diagnóstico médico.
- Pagamento real.
- WhatsApp.
- IA.
- App Expo.

## Diretrizes

- Código de negócio em português claro.
- Backend em arquitetura hexagonal.
- Controller fino com Request/Response.
- Nada de entidade JPA exposta.
- Frontend por feature, mobile-first e premium.
- Não depender apenas de cor para alertas.
- Respeitar dados fictícios no ambiente demo.
